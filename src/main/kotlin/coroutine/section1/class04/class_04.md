# 4강. 코루틴의 취소
- 필요하지 않은 코루틴은 적절히 취소하여 컴퓨터 자원을 아껴야 한다.
- 코루틴을 취소하기 위해선 해당 코루틴에서 협조를 해줘야한다.
```kotlin
fun main() = runBlocking {
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
```
```
[main @coroutine#3] Job2
```
- 위 코드에선 `job1`이 실행됐지만 `job1.cancel()`을 통해 취소되어 `job2`만 정상적으로 완료되었다.
## 코루틴에서 취소에 협조하는 방법
### suspend 함수를 사용하는 방법
- `delay()` 또는 `yield()`와 같은 `kotlinx.coroutines`의 suspend 함수를 사용하면 `cancel()`을 통해 코루틴을 취소할 수 있다.
- `cancel()`이 호출되기 전에 코루틴이 완료될 수도 있다.
```kotlin
fun main() = runBlocking {
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
```
```
[main @coroutine#2] Job1
[main @coroutine#3] Job2
```
- 이 경우는 `job1`이 `delay(10)`을 통해 10ms만에 완료되어 `cancel()`이 호출되기 전에 완료되어 Job1과 Job2가 모두 출력된다.
- suspend 함수를 사용하지 않는 코루틴은 취소할 수 없다.
```kotlin
fun main() = runBlocking {
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
```
```
[main @coroutine#2] 1 번째 반복중!
[main @coroutine#2] 2 번째 반복중!
[main @coroutine#2] 3 번째 반복중!
[main @coroutine#2] 4 번째 반복중!
[main @coroutine#2] 5 번째 반복중!
```
- 위 코드는 `job.cancel()`을 호출하여 `job`을 취소하려고 시도하지만 `job`은 suspend 함수를 사용하지 않기 때문에 취소되지 않는다.
- `job.cancel()`은 `job`이 완전히 종료된 이후에 호출된다.
### `CancellationException`을 던지는 방법
- 코루틴에서 `isActive`라는 프로퍼티를 통해 직접 스스로의 상태를 확인하여 취소에 협조할 수 있다.
  - `isActive`는 `CoroutineScope`의 확장 프로퍼티로, 현재 코루틴이 활성화 되어있는지 또는 취소 신호를 받았는지 확인할 수 있다.
  - `isActive`를 통해 현재 코루틴이 취소 신호를 받았다면 `CancellationException`을 던져서 suspend 함수를 쓰지 않고도 취소할 수 있다.
- 취소 신호를 정상적으로 전달하려면 해당 코루틴이 다른 스레드에서 동작해야 한다.
  - `launch()`의 인자로 `Dispatchers.Default`를 넘겨주면 해당 코루틴은 다른 스레드에서 동작한다.
```kotlin
fun main() = runBlocking {
    val job = launch(Dispatchers.Default) {
        var i = 1
        var nextPrintTime = System.currentTimeMillis()
        while (i <= 5) {
            if (System.currentTimeMillis() >= nextPrintTime) {
                printWithThread("${i++} 번째 반복중!")
                nextPrintTime += 1000
            }

            if (!isActive) {
                throw CancellationException()
            }
        }
    }

    delay(2500)
    job.cancel()
}
```
```
[DefaultDispatcher-worker-1 @coroutine#3] 1 번째 반복중!
[DefaultDispatcher-worker-1 @coroutine#3] 2 번째 반복중!
[DefaultDispatcher-worker-1 @coroutine#3] 3 번째 반복중!
```
- `while`문이 실행되는 동안 `isActive`를 통해 현재 코루틴이 취소 신호를 받았는지 확인하고, 취소 신호를 받았다면 `CancellationException`을 던져서 코루틴을 취소한다.
## `CancellationException`
- `delay()`와 `yield()`와 같은 suspend 함수도 실제로는 취소 신호를 받으면 `CancellationException`을 던져서 코루틴을 취소한다.
- 이로 인해 try-catch문을 사용하여 `CancellationException`을 처리할 때 다시 `CancellationException`을 던지지 않으면 코루틴이 취소되지 않는다.
- 따라서, 코루틴 내부에서 try-catch문을 적절히 사용할 수도 있지만 주의해서 사용해야 한다.
```kotlin
fun main() = runBlocking {
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
```
```
[main @coroutine#1] 취소!
[main @coroutine#2] 취소 신호 감지!
[main @coroutine#2] 하지만 취소되지 않았다!
```
- 위 코드는 `job.cancel()`을 통해 `job`을 취소하려고 시도하지만 `job`은 취소 신호를 감지하여 문자열만 출력하고 별도의 동작을 수행하지 않는다.
- 이로 인해 `job`은 취소되지 않고 정상적으로 완료된다.