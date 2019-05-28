import com.google.protobuf.GeneratedMessageV3
import example.simple.Simple
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception
import example.simple.Simple.SimpleMessage as Msg

fun main() {
    getMessage(42, "Dustin").writeOut(FileOutputStream("simple_message.bin"))

    println(File("simple_message.bin").readLines())
}

fun getMessage(id: Int, name: String): Simple.SimpleMessage =
    Msg.newBuilder().apply {
        this.id = id
        this.name = name
        this.isSimple = true
        this.addAllSampleList(
            listOf(1, 2, 3)
        )
    }.build()


fun GeneratedMessageV3.writeOut(out: OutputStream) = try {
    out.use { this.writeTo(it) }
} catch (e: Exception) {
    e.printStackTrace()
}
