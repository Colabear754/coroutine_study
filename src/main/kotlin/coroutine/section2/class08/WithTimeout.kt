package coroutine.section2.class08

import coroutine.section1.class01.printWithThread
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.withTimeoutOrNull

fun main(): Unit = runBlocking {
    printWithThread(withTimeoutOrNull(1000) {
        delay(1500)
        "END"
    })
//    timeout()
}

private suspend fun timeout() {
    printWithThread(withTimeout(1000) {
        delay(1500)
        "END"
    })
}