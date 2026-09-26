package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.interrupt
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.value.NumberValue
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals

class InterruptThreadTest {

    @Test
    fun `another thread stops a running loop, and the next call runs`() {
        val (store, instance) = InterruptTest.instantiate(StoreConfig(interruptible = true))

        // An interrupt made before the call starts is dropped, so keep interrupting until it returns.
        val returned = AtomicBoolean(false)
        val stopper = thread {
            while (!returned.get()) {
                Thread.sleep(10)
                interrupt(store)
            }
        }
        val result = invoke(store, instance, "spin")
        returned.set(true)
        stopper.join()

        assertEquals(
            ChasmResult.Error(ChasmError.ExecutionError(InvocationError.Interrupted.toString())),
            result,
        )

        assertEquals(
            ChasmResult.Success(listOf(NumberValue.I32(0))),
            invoke(store, instance, "count", listOf(NumberValue.I32(3))),
        )
    }
}
