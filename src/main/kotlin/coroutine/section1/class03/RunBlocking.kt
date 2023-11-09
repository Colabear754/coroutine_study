package coroutine.section1.class03

import coroutine.section1.class01.printWithThread
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {
        printWithThread("시작")
        launch {
            delay(2000L)
            printWithThread("launch 종료")
        }
    }
    printWithThread("종료")
}