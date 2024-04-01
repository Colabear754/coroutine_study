package coroutine.section2.class07

import coroutine.section1.class01.printWithThread
import kotlinx.coroutines.*

suspend fun main() {
    CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        printWithThread("runBlocking을 사용하지 않고 생성한 CoroutineScope!")
    }.join()

    printWithThread("main 종료")
}