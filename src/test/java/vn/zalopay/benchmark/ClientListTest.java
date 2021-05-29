package vn.zalopay.benchmark;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import vn.zalopay.benchmark.core.ClientList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class ClientListTest {

    private static final Path PROTO_PATH_WITH_INVALID_FILE_PATH =
            Paths.get("dist/benchmark/grpc-server/src/main/resources/protos-v2/shelf.proto1");

    private static final Path PROTO_WITH_EXTERNAL_IMPORT_FOLDER =
            Paths.get("dist/benchmark/grpc-server/src/main/resources/protos-v2");
    private static final Path PROTO_FOLDER =
            Paths.get("dist/benchmark/grpc-server/src/main/resources/protos");

    /* Download at https://github.com/googleapis/googleapis */
    private static final Path LIB_FOLDER =
            Paths.get("dist/benchmark/grpc-server/src/main/resources/libs");

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

    @Test(expected = RuntimeException.class)
    public void testThrowExceptionWhenInvokeInvalidProtocPath() {
        ClientList.listServices("", LIB_FOLDER.toString());
    }

    @Test(expected = RuntimeException.class)
    public void testThrowExceptionWhenInvokeProtocPathWithFilePath() {
        ClientList.listServices(PROTO_PATH_WITH_INVALID_FILE_PATH.toString(), LIB_FOLDER.toString());
    }

    @Test
    public void testThrowExceptionWhenInvokeProtocPathWithCantCreateTempFolder() throws IOException {
        MockedStatic<FileUtils> files = Mockito.mockStatic(FileUtils.class);
        files.when(() -> FileUtils.copyDirectory(new File("polyglot-google-types"), new File("polyglot-google-types"))).thenThrow(IOException.class);
//        ClientList.listServices(PROTO_FOLDER.toString(), LIB_FOLDER.toString());
        FileUtils.copyDirectory(new File("polyglot-google-types"), new File("polyglot-google-types1"));
    }
}
