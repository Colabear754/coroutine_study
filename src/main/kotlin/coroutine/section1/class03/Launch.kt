package coroutine.section1.class03

import coroutine.section1.class01.printWithThread
import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
//    jobExample1()
    jobExample2()
//    jobExample3()
}

private suspend fun jobExample1() {
    coroutineScope {
        val job = launch(start = CoroutineStart.LAZY) {
            printWithThread("Hello launch")
        }
        delay(1000L)
        job.start()
    }
}

private suspend fun jobExample2() {
    coroutineScope {
        val job = launch {
            repeat(5) {
                printWithThread("Hello launch ${it + 1}")
                delay(500L)
            }
        }
        delay(1000L)
        job.cancel()
        printWithThread("Job is canceled")
    }
}

private suspend fun jobExample3() {
    coroutineScope {
        val job1 = launch {
            delay(1000L)
            printWithThread("Job1")
        }

        val job2 = launch {
            delay(1000L)
            printWithThread("Job2")
        }
    }
}