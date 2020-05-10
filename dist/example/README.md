# Example

<img src="../asset/benchmark-testscript-grpc.jpg" width="720px" style="padding-bottom: 20px"/>

#### ## With step by step:

- Clone project.
- Download JMeter at [jmeter-with-grpc-request.zip](../../dist/asset/jmeter-with-grpc-request.zip), unzip then start JMeter with command `jmeter-with-grpc-request/5.2/bin/jmeter`. If you already have JMeter, just copy [jmeter-grpc-request-1.0-SNAPSHOT.jar](../bin/jmeter-grpc-request-1.0-SNAPSHOT.jar) to the lib/ext directory of JMeter and restart JMeter.
- Start gRPC server, go to binary of gRPC server at [dist/benchmark/grpc-server/dist](../benchmark/grpc-server/dist), and start `java -cp "./segment-server-1.0-SNAPSHOT.jar" server.SegmentServe`.
- Open test script [SegmentService_checkSeg.jmx](../benchmark/SegmentService_checkSeg.jmx) to config request info, such as host, port, method, proto folder.
- Execute test, click button Run on top bar JMeter GUI.
- Done.