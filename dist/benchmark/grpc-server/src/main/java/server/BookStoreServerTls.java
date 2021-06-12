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

import com.google.protobuf.Empty;
import generated.com.google.endpoints.examples.bookstore.BookstoreGrpc;
import generated.com.google.endpoints.examples.bookstore.CreateShelfRequest;
import generated.com.google.endpoints.examples.bookstore.ListShelvesResponse;
import generated.com.google.endpoints.examples.bookstore.ShelfProto.Shelf;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Server that manages startup/shutdown of a {@code BookStore} server.
 */
public class BookStoreServerTls {

    private static final Logger logger = Logger.getLogger(BookStoreServerTls.class.getName());

    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 8006;
        ClassLoader classLoader = this.getClass().getClassLoader();
        InputStream certFile = classLoader.getResourceAsStream("cert/localhost.crt");
        InputStream keyFile = classLoader.getResourceAsStream("cert/localhost.key");
        server = ServerBuilder.forPort(port)
                .addService(new BookstoreServicesImpl())
                .useTransportSecurity(certFile, keyFile)
                .build()
                .start();
        logger.info("TLS Server started, listening on " + port);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                // Use stderr here since the logger may have been reset by its JVM shutdown hook.
                System.err.println("*** shutting down gRPC server since JVM is shutting down");
                try {
                    BookStoreServerTls.this.stop();
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
        final BookStoreServerTls server = new BookStoreServerTls();
        server.start();
        server.blockUntilShutdown();
    }

    private static class BookstoreServicesImpl extends BookstoreGrpc.BookstoreImplBase {

        @Override
        public void listShelves(Empty request, StreamObserver<ListShelvesResponse> responseObserver) {
            String messageServer = "SERVER";
            ListShelvesResponse.Builder listBuilder = ListShelvesResponse.newBuilder();

            for (int i = 0; i <= 7; i++) {
                listBuilder
                        .addShelves(Shelf.newBuilder()
                                .setId(i)
                                .setTheme("THEME_" + new Random().nextInt(10000) + "_" + messageServer)
                                .build());
            }

            logger.info(request.toString());
            responseObserver.onNext(listBuilder.build());
            responseObserver.onCompleted();
        }

        @Override
        public void createShelf(CreateShelfRequest request, StreamObserver<Shelf> responseObserver) {
            String messageServer = "SERVER";

            try {
                String theme = request.getShelf().getTheme();
                long id = request.getShelf().getId();

                Shelf shelf = Shelf.newBuilder()
                        .setId(id)
                        .setTheme(theme + "_" + new Random().nextInt(10000) + "_" + messageServer)
                        .build();

                logger.info(request.toString());
                responseObserver.onNext(shelf);
                responseObserver.onCompleted();

            } catch (Exception e) {
                logger.info(e.getMessage());
            }

        }
    }
}