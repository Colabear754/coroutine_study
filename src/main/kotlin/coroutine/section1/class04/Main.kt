package coroutine.section1.class04

import coroutine.section1.class01.printWithThread
import kotlinx.coroutines.*

fun main() = runBlocking {
//    cancelExample1()
//    cancelExample2()
//    cancelExample3()
//    cancelExample4()
    cancelExample5()
}

private suspend fun cancelExample1() = runBlocking {
    val job1 = launch {
        delay(1000)
        printWithThread("Job1")
    }

    val job2 = launch {
        delay(1000)
        printWithThread("Job2")
    }

    delay(100)
    job1.cancel()
}

private suspend fun cancelExample2() = runBlocking {
    val job1 = launch {
        delay(10)
        printWithThread("Job1")
    }

    val job2 = launch {
        delay(1000)
        printWithThread("Job2")
    }

    delay(100)
    job1.cancel()
}

private suspend fun cancelExample3() = runBlocking {
    val job = launch {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                printWithThread("${i++} 번째 반복중!")
                nextPrintTime += 1000
            }
        }
    }

    delay(100)
    job.cancel()
}

private suspend fun cancelExample4() = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (nextPrintTime <= System.currentTimeMillis()) {
                nextPrintTime += 1000
                printWithThread("${i++} 번째 반복중!")
            }

            if (!isActive) {
                throw CancellationException()
            }
        }
    }

    delay(2500)
    printWithThread("취소!")
    job.cancel()
}

private suspend fun cancelExample5() = runBlocking {
    val job = launch {
        try {
            delay(1000)
        } catch (e: CancellationException) {
            printWithThread("취소 신호 감지!")
        }

        printWithThread("하지만 취소되지 않았다!")
    }

    delay(100)
    printWithThread("취소!")
    job.cancel()
}
