# 5강. 코루틴의 예외 처리와 Job의 상태 변화
## root 코루틴
- 코루틴의 예외 처리를 살펴보기 전에 root 코루틴의 개념을 알아보자.
```kotlin
fun main() = runBlocking {
    val job1 = launch {
        delay(1000)
        printWithThread("job1 launched")
    }
    
    val job2 = launch {
        delay(100)
        printWithThread("job2 launched")
    }
}
```
- 위 코드에서는 `runBlocking`으로 만들어진 코루틴 1개와 `launch`로 만들어진 코루틴 2개로, 총 3개의 코루틴이 존재한다.
- 여기서 `runBlocking`으로 만들어진 코루틴과 같이 최상위에 있는 코루틴이 root 코루틴이자 부모 코루틴이 된다.
- 그 외에 `launch`로 만들어진 코루틴들은 자식 코루틴이 된다.
- 새로운 root 코루틴을 만들기 위해선 `CoroutineScope`함수로 새로운 스코프를 만들고, 여기서 `launch`를 호출하면 된다.
```kotlin
fun main() = runBlocking {
    val job1 = CoroutineScope(Dispatchers.Default).launch {
        delay(1000)
        printWithThread("job1 launched")
    }

    val job2 = CoroutineScope(Dispatchers.Default).launch {
        delay(100)
        printWithThread("job2 launched")
    }
}
```
- 이 코드에서는 `runBlocking`과 각각의 `launch`가 다른 스코프에서 동작하므로 모두 root 코루틴이 된다.
## `launch`와 `async`의 예외 발생 차이
- `launch`와 `async`는 모두 코루틴을 생성하는 함수이지만, 예외 발생 시 동작 방식에 차이가 있다.
### `launch`의 예외 발생
```kotlin
fun main() = runBlocking {
    val job = CoroutineScope(Dispatchers.Default).launch {
        throw IllegalArgumentException()
    }

    delay(1000)
}
```
```
Exception in thread "DefaultDispatcher-worker-1 @coroutine#2" java.lang.IllegalArgumentException
	at coroutine.section1.class05.MainKt$main$1$job$1.invokeSuspend(Main.kt:8)
	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:108)
	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:584)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:793)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:697)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:684)
	Suppressed: kotlinx.coroutines.internal.DiagnosticCoroutineContextException: [CoroutineId(2), "coroutine#2":StandaloneCoroutine{Cancelling}@602603dc, Dispatchers.Default]
```
- 위와 같이 `launch`로 만들어진 코루틴에서 예외가 발생하면 해당 코루틴은 예외 사항을 출력하고 즉시 종료된다.
### `async`의 예외 발생
```kotlin
fun main() = runBlocking {
    val job = CoroutineScope(Dispatchers.Default).async {
        throw IllegalArgumentException()
    }

    delay(1000)
}
```
- `async`로 만들어진 코루틴에서 예외가 발생하면 해당 코루틴은 예외 사항을 출력하지 않는다.
- `async`로 만들어진 코루틴의 예외를 확인하고 싶다면 `await()`를 호출하면 된다.
```kotlin
fun main(): Unit = runBlocking {
    val job = CoroutineScope(Dispatchers.Default).async {
        throw IllegalArgumentException()
    }

    delay(1000)
    job.await()
}
```
```
Exception in thread "main" java.lang.IllegalArgumentException
	at coroutine.section1.class05.MainKt$main$1$job$1.invokeSuspend(Main.kt:8)
	at _COROUTINE._BOUNDARY._(CoroutineDebugging.kt:46)
	at coroutine.section1.class05.MainKt$main$1.invokeSuspend(Main.kt:12)
Caused by: java.lang.IllegalArgumentException
	at coroutine.section1.class05.MainKt$main$1$job$1.invokeSuspend(Main.kt:8)
	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:108)
	at kotlinx.coroutines.scheduling.CoroutineScheduler.runSafely(CoroutineScheduler.kt:584)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.executeTask(CoroutineScheduler.kt:793)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.runWorker(CoroutineScheduler.kt:697)
	at kotlinx.coroutines.scheduling.CoroutineScheduler$Worker.run(CoroutineScheduler.kt:684)
```
- `asynch`는 결과값을 반환하는 코루틴이므로 예외도 값을 반환할 때 처리할 수 있도록 설계되어 있다.
## 자식 코루틴에서의 예외 발생
- 위 상황까지는 모두 root 코루틴에서의 예외 상황에 대해서만 살펴보았다.
- 이번에는 자식 코루틴에서의 예외 상황에 대해서 살펴보자.
```kotlin
fun main(): Unit = runBlocking {
    val job = async {
        throw IllegalArgumentException()
    }

    delay(1000)
    job.await()
}
```
```
Exception in thread "main" java.lang.IllegalArgumentException
	at coroutine.section1.class05.MainKt$main$1$job$1.invokeSuspend(Main.kt:8)
	at kotlin.coroutines.jvm.internal.BaseContinuationImpl.resumeWith(ContinuationImpl.kt:33)
	at kotlinx.coroutines.DispatchedTask.run(DispatchedTask.kt:108)
	at kotlinx.coroutines.EventLoopImplBase.processNextEvent(EventLoop.common.kt:280)
	at kotlinx.coroutines.BlockingCoroutine.joinBlocking(Builders.kt:85)
	at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking(Builders.kt:59)
	at kotlinx.coroutines.BuildersKt.runBlocking(Unknown Source)
	at kotlinx.coroutines.BuildersKt__BuildersKt.runBlocking$default(Builders.kt:38)
	at kotlinx.coroutines.BuildersKt.runBlocking$default(Unknown Source)
	at coroutine.section1.class05.MainKt.main(Main.kt:6)
	at coroutine.section1.class05.MainKt.main(Main.kt)
```
- 이와 같이 자식 코루틴에서 예외가 발생하면 `async`라고 하더라도 부모 코루틴에 예외가 전파된다.
- 이는 `launch`와 `async`의 차이가 아니라, 부모 코루틴과 자식 코루틴의 관계 때문에 발생하는 현상이다.
- 이 경우에는 `runBlocking`이 예외가 발생했을 때 해당 예외를 출력하는 코루틴이기 때문에 `async`의 예외가 즉시 출력된다.
### `SupervisorJob()`
- 자식 코루틴의 예외를 부모 코루틴에 전파하지 않으려면 `SupervisorJob()`을 사용하면 된다.
```kotlin
fun main(): Unit = runBlocking {
    val job = async(SupervisorJob()) {
        throw IllegalArgumentException()
    }
    
    delay(1000)
}
```
- `async`의 인자로 `SupervisorJob()`을 사용하면 자식 코루틴에서 예외가 발생해도 부모 코루틴에 예외가 전파되지 않는다.
- 이 경우에는 root 코루틴이 `async`일 때와 마찬가지로 `await()`를 호출해야 예외가 출력된다.
## 코루틴에서 발생하는 예외 처리
- 코루틴에서 발생하는 예외를 처리하는 방법은 크게 2가지가 있다.
- 첫 번째는 전통적인 `try-catch`를 사용하는 방법이고, 두 번째는 `CoroutineExceptionHandler`를 사용하는 방법이다.
### `try-catch`
```kotlin
fun main(): Unit = runBlocking {
    val job = launch {
        try {
            throw IllegalArgumentException()
        } catch (e: IllegalArgumentException) {
            printWithThread("정상 종료")
        }
        printWithThread("코루틴이 종료되지 않았다!")
    }
}
```
```
[main @coroutine#2] 정상 종료
[main @coroutine#2] 코루틴이 종료되지 않았다!
```
- `try-catch`를 사용해서 코루틴에서 예외가 발생했을 때 코루틴이 취소되지 않고 계속 동작하도록 할 수 있다.
- 코루틴이 취소되지 않기 때문에 예외 상황에 대한 적절한 처리를 하여 코루틴을 계속 동작시키거나, 별도의 처리 후 다시 예외를 던질 수도 있다.
### `CoroutineExceptionHandler`
- `try-catch` 대신 예외가 발생했을 때 에러를 기록하거나 에러 메시지를 보내는 등의 공통 로직을 처리하고 싶을 때 `CoroutineExceptionHandler`를 활용해볼 수 있다.
```kotlin
val exceptionHandler = CoroutineExceptionHandler { context, throwable ->
    printWithThread("예외 발생: ${throwable.localizedMessage}")
}
```
- `CoroutineExceptionHandler`는 람다의 파라미터로 `CoroutineContext`와 `Throwable`을 받는다.
  - `CoroutineContext`: 코루틴의 구성요소.
  - `Throwable`: 발생한 예외
```kotlin
fun main(): Unit = runBlocking {
    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        printWithThread("예외 발생 : ${throwable.localizedMessage}")
    }
    val job = CoroutineScope(Dispatchers.Default).launch(exceptionHandler) {
        throw IllegalArgumentException("IllegalArgumentException을 던졌다!")
    }
    delay(1000)
}
```
```
[DefaultDispatcher-worker-1 @coroutine#2] 예외 발생 : IllegalArgumentException을 던졌다!
```
- `launch`의 인자로 `CoroutineExceptionHandler`를 넘겨주면 해당 코루틴에서 예외가 발생했을 때 `CoroutineExceptionHandler`가 동작한다.
```kotlin
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
```
```
[DefaultDispatcher-worker-1 @coroutine#2] try-catch로 예외를 잡으면 CoroutineExceptionHandler는 동작하지 않는다.
```
- `CoroutineExceptionHandler`가 적용된 코루틴에서 `try-catch`를 사용하면 `try-catch`가 우선적으로 동작하여 다시 예외를 던지지 않는 한 `CoroutineExceptionHandler`는 동작하지 않는다.
- `try-catch`로 처리한 예외를 `CoroutineExceptionHandler`로 처리하고 싶다면 `try-catch`에서 예외를 다시 던져줘야 한다.
- `CoroutineExceptionHandler`는 `launch`에만 적용할 수 있고, root 코루틴에서만 동작한다.
## 코루틴의 취소와 예외는 어떻게 다를까?
- 코루틴의 취소는 `CancellationException`을 던지는 방식으로 동작한다.
  - 이 경우에는 코루틴을 취소한 것으로 간주하기 때문에 부모 코루틴에 전파하지 않는다.
- `CancellationException`을 제외한 다른 예외가 발생했을 경우에는 코루틴의 실패로 간주한다.
  - 이 경우에는 코루틴이 실패했기 때문에 부모 코루틴에 전파한다.
- 다만 내부적으로는 `CancellationException`이 아닌 다른 예외가 발생했을 때도 **취소됨 상태**로 간주한다.
