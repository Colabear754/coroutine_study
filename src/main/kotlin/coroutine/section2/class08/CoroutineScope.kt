package coroutine.section2.class08

import coroutine.section1.class01.printWithThread
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    printWithThread("START")
    printWithThread(calculateResult())
    printWithThread("END")
}

private suspend fun calculateResult() = coroutineScope {
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