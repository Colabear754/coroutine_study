package coroutine.section2.class07

import coroutine.section1.class01.printWithThread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class AsyncClass {
    private val scope = CoroutineScope(Dispatchers.Default)

    fun print() {
        scope.launch {
            printWithThread("특정 클래스의 독립적인 CoroutineScope!")
        }
    }

    fun destroy() {
        scope.cancel()
    }
}