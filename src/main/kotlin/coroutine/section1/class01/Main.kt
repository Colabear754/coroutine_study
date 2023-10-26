package coroutine.section1.class01

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield

fun main() = runBlocking {
    printWithThread("코루틴 시작")
    launch { coroutine() }
    yield()
    printWithThread("코루틴 끝")
}

suspend fun coroutine() {
    val num1 = 1
    val num2 = 2
    yield()
    printWithThread(num1 + num2)
}

fun printWithThread(any: Any?) = println("[${Thread.currentThread().name}] $any")