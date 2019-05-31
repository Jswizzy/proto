package server

import io.grpc.ServerBuilder

fun main() {
    println("Hello gRPC")

    ServerBuilder
        .forPort(50051)
        .addService(GreetServiceImpl())
        .build()
        .also { server ->
            server.start()
            Runtime.getRuntime().addShutdownHook(Thread {
                println("Requesting shutdown")
                server.shutdown()
                println("Server shutdown")
            })
            server.awaitTermination()
        }
}