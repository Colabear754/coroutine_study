package coroutine.section1.class03

import coroutine.section1.class01.printWithThread
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main(): Unit = runBlocking {
    val time = measureTimeMillis {
        val job1 = async(start = CoroutineStart.LAZY) { doSomething1() }
        val job2 = async(start = CoroutineStart.LAZY) { doSomething2() }
        job1.start()
        job2.start()
        printWithThread("The answer is ${job1.await() + job2.await()}")
    }
    printWithThread("Completed in $time ms")
}

private suspend fun doSomething1(): Int {
    delay(1000L)
    return 1
}

private suspend fun doSomething2(): Int {
    delay(1000L)
    return 2
}