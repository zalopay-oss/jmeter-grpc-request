package vn.zalopay.benchmark;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import vn.zalopay.benchmark.core.ClientList;

@Ignore
public class ClientListTest {

  private static final Path PROTO_FOLDER =
      Paths.get("dist/benchmark/grpc-server/src/main/resources/protos-v2");

  /* Download at https://github.com/googleapis/googleapis */
  private static final Path LIB_FOLDER =
      Paths.get("/Users/lap13227/Desktop/request-proto/googleapis-master");

  @Test
  public void list_method() {
    List<String> methods = ClientList.listServices(PROTO_FOLDER.toString(), LIB_FOLDER.toString());

    // Expected
    List<String> list = Arrays.asList(
        "bookstore.Bookstore/ListShelves",
        "bookstore.Bookstore/CreateShelf"
    );

    // Assertion
    Assert.assertEquals(list, methods);
  }
}
