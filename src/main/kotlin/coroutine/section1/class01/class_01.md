# 1강. 루틴과 코루틴
## 코루틴 사용을 위한 기본 설정
- 코루틴을 사용하기 위해서는 build.gradle 파일에 다음과 같은 의존성을 추가해야 한다.
```groovy
dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
```
``kotlinx-coroutines-core``의 최신 버전은 2023-10-26 기준으로 1.7.3이다.
## 코루틴을 사용하기 위한 메인 코드 설명

```kotlin
fun main() = runBlocking {
    println("코루틴 시작")
    launch { coroutine() }
    yield()
    println("코루틴 끝")
}

suspend fun coroutine() {
    val num1 = 1
    val num2 = 2
    yield()
    println(num1 + num2)
}
```
### 각 메소드 및 키워드 설명
* ``runBlocking``: 코루틴을 생성하기 위한 메소드이다. 이 메소드를 통해 새로운 코루틴이 만들어지고, ``runBlocing``에 전달된 람다식이 새로운 코루틴 안에 들어가게 된다.
* ``launch``: ``runBlocking``처럼 코루틴을 생성하기 위한 메소드이다. 주로 반환값이 없는 코루틴을 생성할 때 사용한다.
* ``yield``: 코루틴이 실행되는 도중에 현재 코루틴을 일시 중지하고 다른 코루틴에게 실행을 양보하고 싶을 때 사용한다.
* ``suspend``: 일시 중단이 가능한 메소드에 붙는 키워드. ``suspend fun``은 ``suspend fun`` 내부에서 실행될 수 있다.

### 코루틴 실행 결과
```
코루틴 시작
코루틴 끝
3
```
### 코드 동작 과정
1. ``main`` 메소드가 실행되면서 ``runBlocking`` 메소드가 실행된다.
2. ``runBlocking`` 메소드가 실행되면서 새로운 코루틴이 생성되고, ``runBlocking``에 전달된 람다식이 새로운 코루틴 안에 들어가게 된다.
3. ``코루틴 시작``이 출력되고, ``launch`` 메소드를 통해 ``coroutine``이 포함된 코루틴이 생성된다. ``coroutine``은 실행되지 않고 대기한다.
4. ``main`` 메소드의 ``yield`` 메소드가 실행되면서 ``runBlocking``에 포함된 코루틴이 일시 중지되고 ``coroutine``이 실행된다.
5. ``coroutine``이 실행되면서 ``num1``과 ``num2``가 초기화된다.
6. ``coroutine``의 ``yield`` 메소드가 실행되면서 ``coroutine``이 일시 중지되고 ``runBlocking``에 포함된 코루틴이 재개된다.
7. ``코루틴 끝``이 출력되고, ``runBlocking``에 포함된 코루틴이 종료된다.
8. ``coroutine``이 재개되면서 ``num1 + num2``가 출력되고 프로그램이 종료된다.
## 루틴과 코루틴의 차이
* 루틴은 시작되면 끝날 때까지 실행되지만, 코루틴은 일시 중지되고 재개될 수 있다.
* 루틴이 끝나면 루틴 내의 정보는 사라지지만, 코루틴은 일시 중지되더라도 루틴 내의 정보가 사라지지 않는다.
## 코루틴 디버깅
코루틴 디버깅을 하기 위해선 ``Run/Debug Configurations``의 ``VM options``에 ``-Dkotlinx.coroutines.debug``를 추가해야 한다. 이 옵션을 추가하면 ``Thread.currentThread().name``을 통해 현재 코루틴의 이름을 확인할 수 있다.
```kotlin
fun printWithThread(any: Any) = println("[${Thread.currentThread().name}] $any")
```
위 메소드를 작성하고 본문 코드의 ``println()``을 모두 ``printWithThread()``로 변경하면 다음과 같은 결과를 얻을 수 있다.
```
[main @coroutine#1] 코루틴 시작
[main @coroutine#1] 코루틴 끝
[main @coroutine#2] 3
```