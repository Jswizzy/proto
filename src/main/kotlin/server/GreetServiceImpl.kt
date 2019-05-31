package server

import com.example.greet.*
import io.grpc.stub.StreamObserver

class GreetServiceImpl : GreetServiceGrpc.GreetServiceImplBase() {
    override fun greet(request: GreetRequest, responseObserver: StreamObserver<GreetResponse>) {
        val greeting = request.greeting
        val firstName = greeting.firstName

        val result = "Hello $firstName."

        val greetResponse = GreetResponse.newBuilder()
            .setResult(result)
            .build()

        responseObserver.run {
            this.onNext(greetResponse)
            this.onCompleted()
        }
    }

    override fun greetManyTimes(
        request: GreetManyTimesRequest,
        responseObserver: StreamObserver<GreetManyTimesResponse>
    ) {
        val greeting = request.greeting
        val firstName = greeting.firstName

        val result = "Hello $firstName."

        repeat(10) { i ->
            val greetResponse = GreetManyTimesResponse.newBuilder()
                .setResult("$result, response number: $i")
                .build()

            Thread.sleep(1_000L)
            responseObserver.onNext(greetResponse)
        }

        responseObserver.onCompleted()
    }
}