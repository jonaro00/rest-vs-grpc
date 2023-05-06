package org.example;

import org.example.server.InventoryServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
        startGrpcServer();
    }

    private static void startGrpcServer() throws IOException, InterruptedException {
        InventoryServiceImpl service = new InventoryServiceImpl();
        Server server = ServerBuilder
                .forPort(9090)
                .addService(service)
                .build();
        server.start();
        System.out.println("gRPC server started on port " + server.getPort());
        server.awaitTermination();
    }
}

/*
        Server server = ServerBuilder.forPort(9090)
                .addService(new InventoryServiceImpl())
                .build();
        server.start();
        server.awaitTermination();
    }

    @Bean
    public Server grpcServer(@Autowired InventoryServiceImpl inventoryService,
                             @Autowired GRpcServerBuilderConfigurer configurer) {
        ServerBuilder<?> serverBuilder = ServerBuilder.forPort(8080)
                .addService(inventoryService);
        configurer.configure(serverBuilder);
        return serverBuilder.build();
    }
 */

