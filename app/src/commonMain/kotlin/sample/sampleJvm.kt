package sample

expect class Sample() {
    fun checkMe(): Int
}

expect object Platform {
    val name: String
}

fun hello(): String = "Mico for ${Platform.name}"

class Proxy {
    fun proxyHello() = hello()
}

fun main() {
    println(hello())
}