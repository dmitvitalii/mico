package sample

actual class Sample {
    actual fun checkMe() = 344
}

actual object Platform {
    actual val name = "JVM"
}


fun main() {
    println(hello())
}