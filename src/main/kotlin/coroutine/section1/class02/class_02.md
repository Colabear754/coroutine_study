# 2강. 스레드와 코루틴
## 스레드와 코루틴
* 프로세스: 컴퓨터에서 실행중인 프로그램.
* 스레드: 프로세스 내에서 실행되는 흐름의 단위. 코드가 실행되는 흐름이라고 생각할 수 있다.
* 코루틴은 스레드와 비슷하게 코드를 포함하고 있지만 그 자체로 코드를 실행할 수 없다. 코루틴에 포함된 코드는 스레드로 옮겨져서 실행된다.
  * 다만 코루틴은 특정 스레드에 종속된 관계가 아니기 때문에 코루틴이 중단되었다가 재개될 때 다른 스레드에서 실행될 수 있다.
## 스레드와 코루틴의 context switching 비용 차이
* 프로세스가 실행되면 프로세스는 힙 메모리에 적재된다.
* 프로세스에서 스레드가 실행될 때 스레드는 힙 메모리에 적재된다.
  * 한 프로세스 내의 스레드들은 동일한 힙 메모리를 공유하고 각각 스택 메모리에 적재되어 실행된다.
  * 스레드 간의 context switching이 일어날 때는 스택 메모리만 교체하면 되기 때문에 프로세스에 비해 비용이 적게 든다.
* 코루틴은 포함된 코드를 스레드에 넘겨서 실행시킨다는 특징으로 인해 상황에 따라 다르다.
  * 여러 코루틴에 포함된 코드가 실행되는 과정에서 동일한 스레드에서 실행된다면 스레드 간의 context switching이 일어나지 않기 때문에 힙 메모리와 스택 메모리 모두 공유하게 되어 비용이 적게 든다.
    * 이렇게 하나의 스레드에서 여러 개의 코루틴이 번갈아가면서 실행되는 것으로 인해 코루틴은 하나의 스레드만으로도 **동시성**을 얻을 수 있다.
  * 여러 코루틴이 각기 다른 스레드에서 실행된다면 스레드 간의 context switching 비용과 동일하다.
## 기타
* 코루틴은 ``yeild()``를 통해 스스로 중단되고 메모리를 양보할 수 있기 때문에 **비선점형**의 특징을 가진다.
* 스레드는 OS가 직접 개입해서 스레드를 중단시키고 다른 스레드를 실행시키기 때문에 **선점형**의 특징을 가진다.