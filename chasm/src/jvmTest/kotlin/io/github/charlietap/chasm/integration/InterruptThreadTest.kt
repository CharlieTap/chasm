package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.interrupt
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.integration.InterruptTest.Companion.interrupted
import io.github.charlietap.chasm.runtime.value.NumberValue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InterruptThreadTest {

    @Test
    fun `another thread stops a running loop, and the next call runs`() {
        val started = CountDownLatch(1)
        val abandoned = AtomicBoolean(false)
        val (store, instance) = InterruptTest.instantiate(
            StoreConfig(interruptible = true),
            keepRunning = {
                started.countDown()
                !abandoned.get()
            },
        )

        val invocation = FutureTask {
            invoke(store, instance, "spin_while_host_allows")
        }
        // A daemon worker and timed waits let a broken interrupt fail the test rather than hang the JVM.
        thread(isDaemon = true, name = "interrupt-test") {
            invocation.run()
        }

        try {
            assertTrue(started.await(5, TimeUnit.SECONDS), "Wasm execution did not reach the host callback")

            assertEquals(ChasmResult.Success(true), interrupt(store))

            assertEquals(interrupted(), invocation.get(5, TimeUnit.SECONDS))
        } finally {
            // Ends the loop if the interrupt did not, so a failure leaves no thread spinning.
            abandoned.set(true)
        }

        assertEquals(
            ChasmResult.Success(listOf(NumberValue.I32(0))),
            invoke(store, instance, "count", listOf(NumberValue.I32(3))),
        )
    }
}
