package org.example.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import org.example.GreetingGrpc;
import org.example.HelloRequest;
import org.example.HelloResponse;

import java.io.IOException;
import java.util.logging.Logger;

public class TestGreetingServer {

    private static final Logger logger = Logger.getLogger(GreetingImpl.class.getName());

    private Server server;

    private void start() throws IOException {
        /* The port on which the server should run */
        int port = 1337;

        server = ServerBuilder.forPort(port)
                .addService(new GreetingImpl())
                .build()
                .start();

        logger.info("Server started, listening on " + port);

        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                logger.info("*** shutting down gRPC server since JVM is shutting down");
                TestGreetingServer.this.stop();
                logger.info("*** server shut down");
            }
        });
    }

    private void stop() {
        if (server != null) {
            server.shutdown();
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


    public static void main(String[] args) throws IOException, InterruptedException {
        final TestGreetingServer server = new TestGreetingServer();
        server.start();
        server.blockUntilShutdown();
    }

    static class GreetingImpl extends GreetingGrpc.GreetingImplBase {

        @Override
        public void greet(HelloRequest req, StreamObserver<HelloResponse> responseObserver) {
            String greeting = "This is from the test server:  " + req.getName();
            HelloResponse response = HelloResponse.newBuilder().setGreeting(greeting).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }
}
