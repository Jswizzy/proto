package client

import com.example.greet.*
import io.grpc.Channel
import io.grpc.ManagedChannelBuilder

fun main() {
    println("Hello from gRPC client")

    ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build()
        .also { channel ->
            println("creating stub")

            GreetServiceGrpc.newBlockingStub(channel).also { stub ->

                GreetManyTimesRequest.newBuilder()
                    .apply {
                        greeting = Greeting.newBuilder().setFirstName("Stephen").build()
                    }.build()
                    .also { greetManyTimesRequest ->
                        stub.greetManyTimes(greetManyTimesRequest)
                            .forEachRemaining { println(it.result) }
                    }
            }

            println("Shutting down channel")
            channel.shutdown()
            println("Client is shutdown")
        }
}

fun unaryClient(channel: Channel) {
    val greeting = Greeting.newBuilder()
        .setFirstName("John")
        .setLastName("Smith")
        .build()

    val greetingRequest = GreetRequest.newBuilder().apply {
        this.greeting = greeting
    }.build()

    val greetResponse = GreetServiceGrpc.newBlockingStub(channel)
        .greet(greetingRequest)

    println(greetResponse.result)
}