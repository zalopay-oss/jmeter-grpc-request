package vn.zalopay.benchmark.core;

import com.google.protobuf.InvalidProtocolBufferException;
import org.apache.commons.io.FileUtils;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.any;

public class ClientListTest extends BaseTest{

    @Test
    public void testCanListAllMethodsInProtoWithoutExternalLibs() {
        // Arrange
        List<String> methods = ClientList.listServices(PROTO_FOLDER.toString(), LIB_FOLDER.toString());

        // Action
        List<String> list = Arrays.asList(
                "data_services_seg.SegmentServices/checkSeg",
                "helloworld.Greeter/SayHello"
        );

        // Assertion
        Assert.assertEquals(list, methods);
    }

    @Test
    public void testCanListAllMethodsInProtoWithExternalLibs() {
        // Arrange
        List<String> methods = ClientList.listServices(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), LIB_FOLDER.toString());

        // Action
        List<String> list = Arrays.asList(
                "bookstore.Bookstore/ListShelves",
                "bookstore.Bookstore/CreateShelf"
        );

        // Assertion
        Assert.assertEquals(list, methods);
    }

    @Test
    public void testCanListAllMethodsInProtoWithNullLibFolderPath() {
        // Arrange
        List<String> methods = ClientList.listServices(PROTO_WITH_EXTERNAL_IMPORT_FOLDER.toString(), null);

        // Action
        List<String> list = Arrays.asList(
                "bookstore.Bookstore/ListShelves",
                "bookstore.Bookstore/CreateShelf"
        );

        // Assertion
        Assert.assertEquals(list, methods);
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Unable to resolve service by invoking protoc")
    public void testThrowExceptionWhenInvokeInvalidProtocPath() {
        ClientList.listServices("", LIB_FOLDER.toString());
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Unable to resolve service by invoking protoc")
    public void testThrowExceptionWhenInvokeProtocPathWithFilePath() {
        ClientList.listServices(PROTO_PATH_WITH_INVALID_FILE_PATH.toString(), LIB_FOLDER.toString());
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Unable to resolve service by invoking protoc")
    public void testThrowExceptionWhenInvokeProtocPathWithCantCreateTempFolder() {
        MockedStatic<FileUtils> fileUtils = Mockito.mockStatic(FileUtils.class);
        fileUtils.when(() -> FileUtils.copyDirectory(any(File.class), any(File.class))).thenThrow(IOException.class);
        ClientList.listServices(PROTO_FOLDER.toString(), LIB_FOLDER.toString());
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Unable to resolve service by invoking protoc")
    public void testThrowExceptionWhenCantCreateTempFileForFileDescriptorSet() {
        MockedStatic<com.google.protobuf.DescriptorProtos.FileDescriptorSet> fileDescriptorSet = Mockito.mockStatic(com.google.protobuf.DescriptorProtos.FileDescriptorSet.class);
        fileDescriptorSet.when(() -> com.google.protobuf.DescriptorProtos.FileDescriptorSet.parseFrom(any(byte[].class))).thenThrow(InvalidProtocolBufferException.class);
        ClientList.listServices(PROTO_FOLDER.toString(), LIB_FOLDER.toString());
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Unable to resolve service by invoking protoc")
    public void testThrowExceptionWhenCantInvokeProtocBinary() {
        MockedStatic<com.github.os72.protocjar.Protoc> fileDescriptorSet = Mockito.mockStatic(com.github.os72.protocjar.Protoc.class);
        fileDescriptorSet.when(() -> com.github.os72.protocjar.Protoc.runProtoc(any(String[].class))).thenThrow(InterruptedException.class);
        ClientList.listServices(PROTO_FOLDER.toString(), LIB_FOLDER.toString());
    }

    @Test(expectedExceptions = RuntimeException.class, expectedExceptionsMessageRegExp = "Unable to resolve service by invoking protoc")
    public void testThrowExceptionWhenCantCreateTempFileForDescriptorPath() {
        MockedStatic<org.slf4j.LoggerFactory> logger = Mockito.mockStatic(org.slf4j.LoggerFactory.class);
        MockedStatic<java.nio.file.Files> files = Mockito.mockStatic(java.nio.file.Files.class);
        logger.when(() -> org.slf4j.LoggerFactory.getLogger(any(String.class))).thenReturn(null);
        files.when(() -> java.nio.file.Files.exists(any(Path.class))).thenReturn(true);
        files.when(() -> java.nio.file.Files.createTempDirectory(Mockito.anyString())).thenReturn(FileSystems.getDefault().getPath("/tmp/stub"));
        files.when(() -> java.nio.file.Files.createDirectories(FileSystems.getDefault().getPath("/tmp/stub/google/protobuf"))).thenReturn(FileSystems.getDefault().getPath("/tmp/stub/google/protobuf"));
        files.when(() -> java.nio.file.Files.createTempFile("descriptor", ".pb.bin")).thenThrow(IOException.class);
        files.when(() -> java.nio.file.Files.copy(any(java.io.InputStream.class), any(java.nio.file.Path.class), any(CopyOption[].class))).thenReturn(10000L);
        ClientList.listServices(PROTO_FOLDER.toString(), LIB_FOLDER.toString());
    }
}
