package coroutine.section2.class08

import coroutine.section1.class01.printWithThread
import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    printWithThread("START")
    printWithThread(calculateResult())
    printWithThread("END")
}

private suspend fun calculateResult() = withContext(Dispatchers.Default) {
    val num1 = async {
        delay(1000)
        100
    }

    val num2 = async {
        delay(1000)
        200
    }

    num1.await() + num2.await()
}