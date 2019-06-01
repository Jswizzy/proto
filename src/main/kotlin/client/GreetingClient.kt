package client

import com.example.greet.*
import io.grpc.Channel
import io.grpc.ManagedChannelBuilder
import io.grpc.stub.StreamObserver
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

fun main() {
    println("Hello from gRPC client")

    ManagedChannelBuilder.forAddress("localhost", 50051)
        .usePlaintext()
        .build()
        .also { channel ->
            println("creating stub")

            //unaryClient(channel)
            //streamingServer(channel)
            //streamingClient(channel)
            bidiStreaming(channel)

            println("Shutting down channel")
        }.shutdown()
    println("Client is shutdown")
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

fun streamingServer(channel: Channel) {
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
}

fun streamingClient(channel: Channel) {
    val countDownLatch = CountDownLatch(1)

    GreetServiceGrpc.newStub(channel).longGreet(
        object : StreamObserver<LongGreetResponse> {
            override fun onNext(value: LongGreetResponse) {
                println("Received a response from the server: ")
                println(value.result)
            }

            override fun onError(t: Throwable) {
                t.printStackTrace()
                countDownLatch.countDown()
            }

            override fun onCompleted() {
                println("Server has completed sending us something.")
                countDownLatch.countDown()
            }
        }
    ).run {
        longGreat("Justin")
        longGreat("John")
        longGreat("Julia")
        onCompleted()
    }
    countDownLatch.await(3L, TimeUnit.SECONDS)
}

fun StreamObserver<LongGreetRequest>.longGreat(name: String) = this.onNext(
    LongGreetRequest.newBuilder()
        .setGreeting(
            Greeting.newBuilder()
                .setFirstName(name)
                .build()
        )
        .build()
)

fun bidiStreaming(channel: Channel) {
    val countDownLatch = CountDownLatch(1)

    GreetServiceGrpc.newStub(channel).greetEveryone(
        object : StreamObserver<GreetEveryoneResponse> {
            override fun onNext(value: GreetEveryoneResponse) {
                println("Response from server: ${value.result}")
            }

            override fun onError(t: Throwable) {
                t.printStackTrace()
                countDownLatch.countDown()
            }

            override fun onCompleted() {
                println("Server is done sending data.")
                countDownLatch.countDown()
            }
        }
    ).run {
        arrayOf("John", "Mark", "Luke").forEach {
            onNext(
                GreetEveryoneRequest.newBuilder()
                    .setGreeting(
                        Greeting.newBuilder()
                            .setFirstName(it)
                            .build()
                    ).build()
            )
        }
        onCompleted()
    }
    countDownLatch.await(3L, TimeUnit.SECONDS)
}