package coroutine.section1.class05

import coroutine.section1.class01.printWithThread
import kotlinx.coroutines.*

fun main(): Unit = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        printWithThread("예외 발생 : ${throwable.localizedMessage}")
    }
    val job = CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
        try {
            throw IllegalArgumentException("IllegalArgumentException을 던졌다!")
        } catch (e: Exception) {
            printWithThread("try-catch로 예외를 잡으면 CoroutineExceptionHandler는 동작하지 않는다.")
        }
    }
    delay(1000)
}

private suspend fun exceptionHandler() {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        printWithThread("예외 발생 : ${throwable.localizedMessage}")
    }
    val job = CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
        throw IllegalArgumentException("IllegalArgumentException을 던졌다!")
    }
    delay(1000)
}

private fun CoroutineScope.tryCatch() {
    val job = launch {
        try {
            throw IllegalArgumentException()
        } catch (e: IllegalArgumentException) {
            printWithThread("정상 종료")
        }
        printWithThread("코루틴이 종료되지 않았다!")
    }
}

private suspend fun supervisorJob() {
    coroutineScope {
        val job = async(SupervisorJob()) {
            throw IllegalArgumentException()
        }
        delay(1000)
    }
}

private suspend fun childCoroutineException() {
    coroutineScope {
        val job = async {
            throw IllegalArgumentException()
        }
        
        delay(1000)
        job.await()
    }
}

private suspend fun asynchException() {
    val job = CoroutineScope(Dispatchers.Default).async {
        throw IllegalArgumentException()
    }

    delay(1000)
    job.await()
}

private suspend fun launchException() {
    val job = CoroutineScope(Dispatchers.Default).launch {
        throw IllegalArgumentException()
    }

    delay(1000)
}

private fun rootCourtine() {
    val job1 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        printWithThread("job1 launched")
    }

    val job2 = CoroutineScope(Dispatchers.Default).launch {
        delay(100)
        printWithThread("job2 launched")
    }
}