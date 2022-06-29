package vn.zalopay.benchmark.core.protobuf;

import com.github.os72.protocjar.Protoc;
import com.github.os72.protocjar.ProtocVersion;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.DescriptorProtos.FileDescriptorSet;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A utility class which facilitates invoking the protoc compiler on all proto files in a
 * directory tree.
 */
public class ProtocInvoker {
    private static final Logger logger = LoggerFactory.getLogger(ProtocInvoker.class);
    private static final PathMatcher PROTO_MATCHER =
            FileSystems.getDefault().getPathMatcher("glob:**/*.proto");

    private final ImmutableList<Path> protocIncludePaths;
    private final Path discoveryRoot;
    private final int largeFolderLimit = 100;

    /**
     * Takes an optional path to pass to protoc as --proto_path. Uses the invocation-time proto root
     * if none is passed.
     */
    private ProtocInvoker(Path discoveryRoot, ImmutableList<Path> protocIncludePaths) {
        this.protocIncludePaths = protocIncludePaths;
        this.discoveryRoot = discoveryRoot;
    }

    /**
     * Creates a new {@link ProtocInvoker} with the supplied configuration.
     */
    public static ProtocInvoker forConfig(String protoDiscoveryRoot, String libFolder) {
        Path discoveryRootPath = Paths.get(protoDiscoveryRoot);
        if (!discoveryRootPath.isAbsolute()) {
            discoveryRootPath = Paths.get(FileServer.getFileServer().getBaseDir(), protoDiscoveryRoot);
        }

        ImmutableList.Builder<Path> includePaths = ImmutableList.builder();

        List<String> includePathsList = getProtocIncludes(libFolder);

        for (String includePathString : includePathsList) {
            Path path = Paths.get(includePathString);
            if (!path.isAbsolute()) {
                path = Paths.get(FileServer.getFileServer().getBaseDir(), includePathString);
            }
            Preconditions.checkArgument(Files.exists(path), "Invalid proto include path: " + path);
            includePaths.add(path.toAbsolutePath());
        }

        return new ProtocInvoker(discoveryRootPath, includePaths.build());
    }

    /**
     * Executes protoc on all .proto files in the subtree rooted at the supplied path and returns a
     * {@link FileDescriptorSet} which describes all the protos.
     */
    public FileDescriptorSet invoke() throws ProtocInvocationException {
        Path wellKnownTypesInclude;
        Path googleTypesInclude;
        try {
            wellKnownTypesInclude = setupWellKnownTypes();
        } catch (IOException e) {
            throw new ProtocInvocationException("Unable to extract well known types", e);
        }

        Path descriptorPath;
        try {
            descriptorPath = Files.createTempFile("descriptor", ".pb.bin");
        } catch (IOException e) {
            throw new ProtocInvocationException("Unable to create temporary file", e);
        }

        // Large folder processing, solve CreateProcess error=206
        final ImmutableSet<String> protoFilePaths = scanProtoFiles(discoveryRoot);
        ImmutableList<String> protocArgs = null;
        String protocVersion = JMeterUtils.getPropDefault("grpc.request.protoc.version", ProtocVersion.PROTOC_VERSION.mVersion);

        if (protoFilePaths.size() > largeFolderLimit) {
            try {
                File argumentsFile = createFileWithArguments(protoFilePaths.toArray(new String[0]));
                protocArgs = ImmutableList.<String>builder()
                        .add("@" + argumentsFile.getAbsolutePath())
                        .addAll(includePathArgs(wellKnownTypesInclude))
                        .add("--descriptor_set_out=" + descriptorPath.toAbsolutePath().toString())
                        .add("--include_imports")
                        .add("-v" + protocVersion)
                        .build();
            } catch (IOException e) {
                logger.error("Unable to create protoc parameter file", e);
            }
        }

        if (protocArgs == null) {
            protocArgs = ImmutableList.<String>builder()
                    .addAll(protoFilePaths)
                    .addAll(includePathArgs(wellKnownTypesInclude))
                    .add("--descriptor_set_out=" + descriptorPath.toAbsolutePath().toString())
                    .add("--include_imports")
                    .add("-v" + protocVersion)
                    .build();
        }

        invokeBinary(protocArgs);
        try {
            return FileDescriptorSet.parseFrom(Files.readAllBytes(descriptorPath));
        } catch (IOException e) {
            throw new ProtocInvocationException("Unable to parse the generated descriptors", e);
        }
    }

    private void invokeBinary(ImmutableList<String> protocArgs) throws ProtocInvocationException {
        int status;
        String[] protocInfoLogLines;
        String[] protocErrorLogLines;

        // The "protoc" library unconditionally writes to stdout. So, we replace stdout right before
        // calling into the library in order to gather its output.
        PrintStream stdoutBackup = System.out;
        PrintStream stderrBackup = System.err;
        try {
            ByteArrayOutputStream protocStdout = new ByteArrayOutputStream();
            ByteArrayOutputStream protocStderr = new ByteArrayOutputStream();
            System.setOut(new PrintStream(protocStdout));
            System.setErr(new PrintStream(protocStderr));
            status = Protoc.runProtoc(protocArgs.toArray(new String[0]));
            protocInfoLogLines = protocStdout.toString().split("\n");
            protocErrorLogLines = protocStderr.toString().split("\n");
        } catch (IOException | InterruptedException e) {
            throw new ProtocInvocationException("Unable to execute protoc binary", e);
        } finally {
            // Restore stdout.
            System.setOut(stdoutBackup);
            System.setErr(stderrBackup);
        }
        if (status != 0) {
            protocInvokerErrorHandler(protocArgs, status, protocInfoLogLines, protocErrorLogLines);
        }
    }

    /**
     * Put args into a temp file to be referenced using the @ option in protoc command line.
     *
     * @param args
     * @return the temporary file wth the arguments
     * @throws IOException
     */
    private File createFileWithArguments(String[] args) throws IOException {
        PrintWriter writer = null;
        try {
            final File tempFile = File.createTempFile("protoc", null, null);
            tempFile.deleteOnExit();

            writer = new PrintWriter(tempFile, "UTF-8");
            for (final String arg : args) {
                writer.println(arg);
            }
            writer.flush();

            return tempFile;
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    private ImmutableSet<String> scanProtoFiles(Path protoRoot) throws ProtocInvocationException {
        try (final Stream<Path> protoPaths = Files.walk(protoRoot)) {
            return ImmutableSet.copyOf(protoPaths
                    .filter(PROTO_MATCHER::matches)
                    .map(path -> path.toAbsolutePath().toString())
                    .collect(Collectors.toSet()));
        } catch (IOException e) {
            throw new ProtocInvocationException("Unable to scan proto tree for files", e);
        }
    }

    private ImmutableList<String> includePathArgs(Path wellKnownTypesInclude) {
        ImmutableList.Builder<String> resultBuilder = ImmutableList.builder();
        for (Path path : protocIncludePaths) {
            resultBuilder.add("-I" + path.toString());
        }

        // Add the include path which makes sure that protoc finds the well known types. Note that we
        // add this *after* the user types above in case users want to provide their own well known
        // types.
        resultBuilder.add("-I" + wellKnownTypesInclude.toString());

        // Protoc requires that all files being compiled are present in the subtree rooted at one of
        // the import paths (or the proto_root argument, which we don't use). Therefore, the safest
        // thing to do is to add the discovery path itself as the *last* include.
        resultBuilder.add("-I" + discoveryRoot.toAbsolutePath().toString());

        return resultBuilder.build();
    }

    /**
     * Get paths lib protoc
     */
    private static List<String> getProtocIncludes(String libFolder) {
        if (Objects.isNull(libFolder)) {
            return Collections.emptyList();
        }

        List<String> protocIncludes = new LinkedList<>();
        for (String pathString : libFolder.split(",")) {
            Path includePath = Paths.get(pathString);
            if (Files.exists(includePath)) {
                protocIncludes.add(includePath.toString());
            }
        }
        return protocIncludes;
    }

    /**
     * Extracts the .proto files for the well-known-types into a directory and returns a proto
     * include path which can be used to point protoc to the files.
     */
    private static Path setupWellKnownTypes() throws IOException {
        Path tmpdir = Files.createTempDirectory("polyglot-well-known-types");
        Path protoDir = Files.createDirectories(Paths.get(tmpdir.toString(), "google", "protobuf"));
        for (String file : WellKnownTypes.fileNames()) {
            Files.copy(
                    ProtocInvoker.class.getResourceAsStream("/google/protobuf/" + file),
                    Paths.get(protoDir.toString(), file));
        }
        return tmpdir;
    }

    /**
     * An error indicating that something went wrong while invoking protoc.
     */
    public class ProtocInvocationException extends Exception {
        private static final long serialVersionUID = 1L;

        private ProtocInvocationException(String message) {
            super(message);
        }

        private ProtocInvocationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    private void protocInvokerErrorHandler(ImmutableList<String> protocArgs, int status, String[] protocInfoLogLines,
                                           String[] protocErrorLogLines) throws ProtocInvocationException {
        // If protoc failed, we dump its output as a warning.
        logger.error("Protoc invocation failed with status: " + status);
        for (String line : protocInfoLogLines) {
            logger.error("[Protoc log] " + line);
        }

        for (String line : protocErrorLogLines) {
            logger.error("[Protoc error log] " + line);
        }

        throw new ProtocInvocationException(
                String.format("Got error exit code [%d] from protoc: protoc command [%s] has " +
                                "error" +
                                " [%s]",
                        status,
                        String.join("\n", protocInfoLogLines), String.join("\n", protocErrorLogLines)));
    }
}
