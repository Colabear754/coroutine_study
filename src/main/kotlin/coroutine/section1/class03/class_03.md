# 3강. 코루틴 빌더와 Job
- 코루틴 빌더: 코루틴을 새로 생성하는 메소드의 총칭

### ``runBlocking``
- 생성된 코루틴을 포함하여 내부의 다른 코루틴이 모두 종료될 때까지 스레드를 붙잡아서, 블로킹이 풀릴 때까지 다른 코드를 실행할 수 없게 하는 코루틴 빌더.
```kotlin
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
```
- 위 코드에서 ``launch`` 내부의 코드는 ``delay(2000L)``를 통해 2초 동안 지연되어 다른 코루틴을 실행하도록 한다.
- 하지만 ``runBlocking``은 내부의 코루틴이 모두 종료될 때까지 스레드를 붙잡아 블로킹이 풀리지 않는다.
- 따라서 위 코드는 ``launch`` 내부의 코드가 모두 종료될 때까지 2초를 기다려야 ``printWithThread("종료")``가 실행된다.
- ``runBlocking``을 함부로 사용하면 애플리케이션 실행 중에 블로킹이 풀리지 않아 애플리케이션이 중지되는 상황이 발생할 수 있으므로 최초 ``main`` 메소드나 테스트 코드를 시작할 때만 사용하는 것이 좋다.
### ``launch``
- 코루틴을 제어할 수 있는 ``Job`` 객체를 반환하는 코루틴 빌더.
  - 여기서 '제어'란 코루틴을 시작하고, 취소하고, 종료할 때까지 기다릴 수 있다는 의미이다.
  - 내부 코드의 결과를 반환할 수 없으므로 주로 반환값이 없는 코루틴을 생성할 때 사용한다.
- ``launch``는 CorutineScope의 확장 함수이므로, ``CoroutineScope``의 객체(코루틴 내부)에서만 호출할 수 있다.
#### ``Job`` 객체의 제어
1. ``start()``
```kotlin
fun main(): Unit = runBlocking {
    val job = launch(start = CoroutineStart.LAZY) {
        printWithThread("Hello launch")
    }
    delay(1000L)
    job.start()
}
```
- 위 코드와 같이 ``launch``의 ``start`` 파라미터를 ``CoroutineStart.LAZY``로 설정하면, ``launch``가 바로 실행되지 않고 ``job.start()``가 호출될 때 실행된다.
2. ``cancel()``
```kotlin
fun main(): Unit = runBlocking {
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
```
- 위 코드와 같이 ``job.cancel()``을 호출하면, ``launch`` 내부의 코드가 실행되다가 ``cancel()``이 호출되는 시점에 ``launch`` 내부의 코드가 종료된다.
- 이 경우에는 ``Hello launch 1``부터 ``Hello launch 5``까지 출력할 수 있는 코드를 중간에 취소하여 ``Hello launch 2``까지만 출력하고 종료된다.
```
[main @coroutine#2] Hello launch 1
[main @coroutine#2] Hello launch 2
[main @coroutine#1] Job is canceled
```
- ``job.cancel()``을 호출할 때 ``CancellationException``을 전달하여 취소 사유를 명시할 수도 있다.
3. ``join()``
```kotlin
fun main(): Unit = runBlocking {
    val job1 = launch {
        delay(1000L)
        printWithThread("Job1")
    }

    job1.join()

    val job2 = launch {
        delay(1000L)
        printWithThread("Job2")
    }
}
```
- 위 코드와 같이 ``job1.join()``을 호출하면, ``job1``이 종료될 때까지 ``job2``는 실행되지 않는다.
- ``join()``은 ``cancel()``과 함께 사용하여 취소된 코루틴이 종료될 때까지 기다릴 수도 있다.
### ``async``
- ``launch``와 마찬가지로 코루틴을 제어할 수 있는 ``Job`` 객체를 반환하는 코루틴 빌더.
- ``launch``와 다른 점은 ``async``는 ``job.await()``를 통해 코루틴 내부 코드의 결과를 반환할 수 있다는 점이다.
```kotlin
fun main(): Unit = runBlocking {
    val time = measureTimeMillis {
        val job1 = async { doSomething1() }
        val job2 = async { doSomething2() }
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
```
```
[main @coroutine#1] The answer is 3
[main @coroutine#1] Completed in 1031 ms
```
- 주의할 점은 ``async``의 ``start`` 파라미터를 ``CoroutineStart.LAZY``로 설정하면, ``await()``를 호출할 때 결과 값이 반환될 때까지 기다린다는 점이다.
```kotlin
fun main(): Unit = runBlocking {
    val time = measureTimeMillis {
        val job1 = async(start = CoroutineStart.LAZY) { doSomething1() }
        val job2 = async(start = CoroutineStart.LAZY) { doSomething2() }
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
```
```
[main @coroutine#1] The answer is 3
[main @coroutine#1] Completed in 2037 ms
```
- 지연 코루틴을 ``async``로 사용할 때, 동시에 호출하고 싶다면 ``start()``를 호출하여 실행시켜야 한다.
```kotlin
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
```
```
[main @coroutine#1] The answer is 3
[main @coroutine#1] Completed in 1031 ms
```
