package vn.zalopay.benchmark.core.grpc;

import com.google.protobuf.DescriptorProtos;

import org.apache.commons.io.FileUtils;
import org.apache.jmeter.services.FileServer;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import vn.zalopay.benchmark.core.BaseTest;
import vn.zalopay.benchmark.core.protobuf.ProtocInvoker;
import vn.zalopay.benchmark.exception.ProtocInvocationException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.IntStream;

public class ProtocInvokerTest extends BaseTest {
    private static final Path PROTO_WITH_MORE_THAN_100_PROTO_FILES =
            Paths.get(
                    System.getProperty("user.dir"),
                    "dist/benchmark/grpc-server/src/main/resources/protos-100");
    private static final String PROTO_WITH_RELATIVE_PATH = "./protos";

    private static final String PROTO_CONTENT_TEMPLATE =
            "syntax = \"proto3\";\n"
                    + "package helloworld_%d;\n"
                    + "service Greeter%d {\n"
                    + "    rpc SayHello (HelloRequest) returns (HelloReply) {};\n"
                    + "    rpc SayHelloWithJsonMetadata(HelloRequest) returns(HelloReply){};\n"
                    + "}\n"
                    + "message HelloRequest {\n"
                    + "    string name = 1;\n"
                    + "}\n"
                    + "message HelloReply {\n"
                    + "    string message = 1;\n"
                    + "}";

    @Test
    public void canGenerateFileDescriptorSet() throws ProtocInvocationException {
        ProtocInvoker protocInvoker =
                ProtocInvoker.forConfig(
                        PROTO_FOLDER.toAbsolutePath().toString(),
                        LIB_FOLDER.toAbsolutePath().toString());
        DescriptorProtos.FileDescriptorSet fileDescriptorSet = protocInvoker.invoke();
        Assert.assertTrue(fileDescriptorSet.getFileCount() > 0);
    }

    @Test(
            expectedExceptions = ProtocInvocationException.class,
            expectedExceptionsMessageRegExp = ".*[Missing input file].*")
    public void cannotGenerateFileDescriptorSet() throws ProtocInvocationException {
        ProtocInvoker protocInvoker =
                ProtocInvoker.forConfig(JMETER_PROPERTIES_FILE.toAbsolutePath().toString(), "");
        protocInvoker.invoke();
    }

    @Test
    public void canGenerateWithProtoFolderHasMoreThan100Files() throws IOException {
        File folder = new File(PROTO_WITH_MORE_THAN_100_PROTO_FILES.toAbsolutePath().toString());
        try {
            createDummyProtoFiles();
            ProtocInvoker protocInvoker =
                    ProtocInvoker.forConfig(
                            PROTO_WITH_MORE_THAN_100_PROTO_FILES.toAbsolutePath().toString(), "");
            DescriptorProtos.FileDescriptorSet fileDescriptorSet = protocInvoker.invoke();
            Assert.assertEquals(fileDescriptorSet.getFileCount(), 150);
            FileUtils.deleteDirectory(folder);
        } finally {
            FileUtils.deleteDirectory(folder);
        }
    }

    @Test
    public void canGenerateWithProtoWithFileServerInDistributedTest() {
        MockedStatic<FileServer> fileServerMockedStatic = Mockito.mockStatic(FileServer.class);
        fileServerMockedStatic
                .when(FileServer::getFileServer)
                .then(
                        (i) -> {
                            FileServer fileServerMock = Mockito.mock(FileServer.class);
                            Mockito.when(fileServerMock.getBaseDir())
                                    .then(
                                            (_i) ->
                                                    Paths.get(
                                                                    System.getProperty("user.dir"),
                                                                    "dist/benchmark/grpc-server/src/main/resources")
                                                            .toAbsolutePath()
                                                            .toString());
                            return fileServerMock;
                        });
        ProtocInvoker protocInvoker = ProtocInvoker.forConfig(PROTO_WITH_RELATIVE_PATH, "");
        DescriptorProtos.FileDescriptorSet fileDescriptorSet = protocInvoker.invoke();
        Assert.assertEquals(fileDescriptorSet.getFileCount(), 4);
    }

    private void createDummyProtoFiles() throws IOException {
        File folder = new File(PROTO_WITH_MORE_THAN_100_PROTO_FILES.toAbsolutePath().toString());
        FileUtils.deleteDirectory(folder);
        folder.mkdirs();
        IntStream.range(0, 150)
                .forEach(
                        i -> {
                            Path fileName =
                                    Paths.get(
                                            PROTO_WITH_MORE_THAN_100_PROTO_FILES
                                                    .toAbsolutePath()
                                                    .toString(),
                                            String.format("proto_%d.proto", i));

                            File protoFile = new File(fileName.toAbsolutePath().toString());
                            try (FileWriter fileWriter =
                                    new FileWriter(protoFile.getAbsoluteFile().toString())) {
                                fileWriter.write(String.format(PROTO_CONTENT_TEMPLATE, i, i));
                            } catch (IOException e) {
                                Assert.fail();
                            }
                        });
    }

    @Test(
            expectedExceptions = ProtocInvocationException.class,
            expectedExceptionsMessageRegExp = ".*" + "Unable to extract well known types.*")
    public void testThrowExceptionWhenGenerateWellKnownType() {
        MockedStatic<FileUtils> fileUtilsMockedStatic = Mockito.mockStatic(FileUtils.class);
        fileUtilsMockedStatic
                .when(() -> FileUtils.forceDeleteOnExit(Mockito.any(File.class)))
                .thenThrow(IOException.class);
        ProtocInvoker protocInvoker =
                ProtocInvoker.forConfig(PROTO_FOLDER.toAbsolutePath().toString(), "");
        protocInvoker.invoke();
    }

    @Test
    public void testCanGetListOfTempFolderToGenProtoc() {
        ProtocInvoker protocInvoker =
                ProtocInvoker.forConfig(PROTO_FOLDER.toAbsolutePath().toString(), "");
        protocInvoker.invoke();
        List<String> listTempPath = ProtocInvoker.getTempFolderPathToGenerateProtoFiles();
        Assert.assertTrue(listTempPath.size() >= 2);
    }

    @Test
    public void testCanCleanTampFolder() {
        ProtocInvoker protocInvoker =
                ProtocInvoker.forConfig(PROTO_FOLDER.toAbsolutePath().toString(), "");
        protocInvoker.invoke();
        ProtocInvoker.cleanTempFolderForGeneratingProtoc();
        SoftAssert softAssert = new SoftAssert();
        ProtocInvoker.getTempFolderPathToGenerateProtoFiles()
                .forEach(
                        path -> {
                            File tempFolder = new File(path);
                            softAssert.assertFalse(tempFolder.exists());
                        });
        softAssert.assertAll();
    }

    @Test(
            expectedExceptions = ProtocInvocationException.class,
            expectedExceptionsMessageRegExp = ".*Unable to execute" + " protoc binary.*")
    public void canGenerateDefaultBuilderWithProtoFolderHasMoreThan100FilesWhenIOException()
            throws IOException {
        File folder = new File(PROTO_WITH_MORE_THAN_100_PROTO_FILES.toAbsolutePath().toString());
        try {
            createDummyProtoFiles();
            MockedStatic<File> fileMockedStatic = Mockito.mockStatic(File.class);
            fileMockedStatic
                    .when(() -> File.createTempFile(Mockito.any(), Mockito.any(), Mockito.any()))
                    .thenThrow(IOException.class);
            ProtocInvoker protocInvoker =
                    ProtocInvoker.forConfig(
                            PROTO_WITH_MORE_THAN_100_PROTO_FILES.toAbsolutePath().toString(), "");
            DescriptorProtos.FileDescriptorSet fileDescriptorSet = protocInvoker.invoke();
        } finally {
            FileUtils.deleteDirectory(folder);
        }
    }
}
