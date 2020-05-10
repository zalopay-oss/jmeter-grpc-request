# Benchmark: jmter-grpc-request

## 1. Purpose

Verify that *jmeter-grpc-request* is really stable when performing load testing for the gRPC system. To do this, create a simple gRPC server then use JMeter with *jmter-grpc-request* sampler to load test with multiple test cases.

## 2. Prepare materials

### Load test model

<img src="../asset/distributed-model.png" width="590" alt="jmeter distributed model"/>

Apply JMeter's distributed testing model running in non-GUI mode. Using 4 servers to load test, configure each server as follows:

- OS: CentOS Linux
- Mem: 32G
- Swap: 4.0G
- CPU: Intel(R) Xeon(R) 8 core

### Creating the gRPC server 

Proto file information:

```java
syntax = "proto3";

option java_multiple_files = true;
option java_package = "generated.xn.services.ex.api.grpcstream.protos";
option java_outer_classname = "SegmentProtos";
package data_services_seg;

service SegmentServices {
    rpc checkSeg (SegmentReq) returns (SegmentResp) {
    }
}
message SegmentReq {
    string id = 1;
    int32 segment = 2;
    string mac = 3;
    int32 client = 4;
    int64 reqdate = 5;
}

message SegmentResp {
    string result = 1;
}
```

The code override for the gRPC server:

```java
static class SegmentServicesImpl extends SegmentServicesGrpc.SegmentServicesImplBase {

    @Override
    public void checkSeg(SegmentReq request, StreamObserver<SegmentResp> responseObserver) {
        String result = request.toString();
        SegmentResp ruleResponse = SegmentResp.newBuilder()
                .setResult("okay-" + System.currentTimeMillis() + " " + result)
                .build();
        logger.info(request.toString());
        responseObserver.onNext(ruleResponse);
        responseObserver.onCompleted();
    }
}
```

Deploy information:

- Call via IP, port.
- Only 1 instance, run with command `java -cp "./segment-server-1.0-SNAPSHOT.jar" server.SegmentServe`.
- Very simple, no gateway no load balance.

## 3. Execute load test

<img src="../asset/benchmark-testscript-grpc.jpg" width="720px" style="padding-bottom: 20px"/>

Execute JMeter in non-GUI mode and combine with *jmter-grpc-request* sampler. With test script here [SegmentService_checkSeg.jmx](./SegmentService_checkSeg.jmx).

## 4. Reports

With test cases as below.

### ## 1st result:

- CCU: 500 user
- Duration: 5 min

<img src= "../asset/report-500-300s.jpg" />

### ## 2nd result:

- CCU: 500 user
- Duration: 15 min

<img src= "../asset/report-500-900s.jpg" />

### ## 3rd result:

- CCU: 1000 user
- Duration: 5 min

<img src= "../asset/report-1k-300s.jpg" />

### ## 4th result:

- CCU: 120 user
- Duration: 30 min

<img src= "../asset/report-120-1800s.jpg" />

### ## 5th result:

- CCU: 120 user
- Duration: 60 min

<img src= "../asset/report-120-3600s.jpg" />