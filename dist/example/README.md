# Example

<img src="../asset/benchmark-testscript-grpc.jpg" width="720px" style="padding-bottom: 20px"/>

#### ## With step by step:

- Download JMeter at [apache-jmeter-5.1.1.zip](https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.1.1.zip) and unzip.
- Then copy the latest version [jmeter-grpc-request](https://github.com/zalopay-oss/jmeter-grpc-request/releases) to the *lib/ext* directory of JMeter and restart JMeter.
- Start gRPC server, go to binary of gRPC server at [dist/benchmark/grpc-server/dist](../benchmark/grpc-server/dist), and start `java -cp "./gprc-server-1.0-SNAPSHOT.jar" server.BookStoreServer`.
- Open test script [BookService_element_gui.jmx](../benchmark/BookService_element_gui.jmx) to config request info such as host, port, method, proto folder.
- Execute test, click button Run on top bar JMeter GUI.
- Done.