package coroutine.section2.class09

suspend fun main() {
    val service = UserService()
    println(service.findUser(1L))
}