package coroutine.section2.class06

import coroutine.section1.class01.printWithThread
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    launch {
        delay(600)
        printWithThread("첫 번째 코루틴")
    }

    launch {
        delay(500)
        throw IllegalArgumentException("코루틴 실패!")
    }
}