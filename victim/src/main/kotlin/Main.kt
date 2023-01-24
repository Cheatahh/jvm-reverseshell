@file:Suppress("Unchecked_Cast")

import java.io.DataOutputStream
import java.io.ObjectInputStream
import java.net.ServerSocket
import kotlin.concurrent.thread

fun main() {
    val myCoolServer = ServerSocket(1337)
    Runtime.getRuntime().addShutdownHook(thread(start = false) {
        myCoolServer.close()
        println("Server stopped")
    })
    println("Server started")

    while(true) {
        val client = myCoolServer.accept()
        println("Client connected: $client")

        try {
            // purpose of this server is to receive two values, add & return then
            // not very useful, this is just an example

            val input = ObjectInputStream(client.getInputStream())
            val (a, b) = input.readObject() as Pair<Int, Int>
            println("Got a = $a, b = $b -> ${a + b}")
            DataOutputStream(client.getOutputStream()).writeInt(a + b)
        } catch (_: Exception) {}

        println("Client disconnected: $client")
        client.close()
    }
}