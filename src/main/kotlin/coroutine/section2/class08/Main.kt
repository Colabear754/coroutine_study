package coroutine.section2.class08

import coroutine.section1.class01.printWithThread
import kotlinx.coroutines.*
import kotlinx.coroutines.future.await
import java.util.concurrent.CompletableFuture

fun main(): Unit = runBlocking {
    val result1 = async { apiCall1() }
    val result2 = async { apiCall2(result1.await()) }
    printWithThread(result2.await())
}

suspend fun apiCall1(): String {
    return CoroutineScope(Dispatchers.Default).async {
        delay(1000)
        "World"
    }.await()
}

suspend fun apiCall2(name: String): String {
    return CompletableFuture.supplyAsync {
        Thread.sleep(1000)
        "Hello, $name!"
    }.await()
}

// suspending function을 활용하지 않은 방식
//
//fun apiCall1(): String {
//    Thread.sleep(1000)
//    return "World"
//}
//
//fun apiCall2(name: String): String {
//    Thread.sleep(1000)
//    return "Hello, $name!"
//}