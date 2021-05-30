/*
 * Copyright 2015 The gRPC Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package server;

import generated.io.grpc.examples.helloworld.GreeterGrpc;
import generated.io.grpc.examples.helloworld.HelloReply;
import generated.io.grpc.examples.helloworld.HelloRequest;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code Greeter} server.
 */
public class HelloWorldServerTls {
    private static final Logger logger = Logger.getLogger(HelloWorldServerTls.class.getName());
    private static final Path CERT_PATH =
            Paths.get("dist/benchmark/grpc-server/src/main/resources/protos");
    private static final Path KEY_PATH =
            Paths.get("dist/benchmark/grpc-server/src/main/resources/protos");
    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 50052;
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream certFile = classLoader.getResourceAsStream("cert/localhost.crt");
        InputStream keyFile = classLoader.getResourceAsStream("cert/localhost.key");
        server = ServerBuilder.forPort(port)
                .addService(new GreeterImpl())
                .useTransportSecurity(certFile, keyFile)
                .build()
                .start();
        logger.info("Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    HelloWorldServerTls.this.stop();
                } catch (InterruptedException e) {
                    e.printStackTrace(System.err);
                }
                System.err.println("*** server shut down");
            }
        });
    }

    private void stop() throws InterruptedException {
        if (server != null) {
            server.shutdown().awaitTermination(30, TimeUnit.SECONDS);
        }
    }

    /**
     * Await termination on the main thread since the grpc library uses daemon threads.
     */
    private void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * Main launches the server from the command line.
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        final HelloWorldServerTls server = new HelloWorldServerTls();
        server.start();
        server.blockUntilShutdown();
    }

    static class GreeterImpl extends GreeterGrpc.GreeterImplBase {

        @Override
        public void sayHello(HelloRequest req, StreamObserver<HelloReply> responseObserver) {
            HelloReply reply = HelloReply.newBuilder().setMessage("Hello " + req.getName()).build();
            logger.info(req.toString());
            responseObserver.onNext(reply);
            responseObserver.onCompleted();
        }
    }
}
