package io.github.charlietap.chasm.memory.atomic

import io.github.charlietap.chasm.memory.ByteBufferLinearMemory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AtomicWaitNotifyJvmTest {

    private val WAIT_OK = 0
    private val WAIT_MISMATCH = 1
    private val WAIT_TIMEOUT = 2

    @Test
    fun i32WaitReturnsMismatchWhenValueDiffers() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x1234)

        val result = I32AtomicWait(memory, 0, 0x5678, -1)

        assertEquals(WAIT_MISMATCH, result)
    }

    @Test
    fun i64WaitReturnsMismatchWhenValueDiffers() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putLong(0, 0x123456789ABCDEFL)

        val result = I64AtomicWait(memory, 0, 0x0FEDCBA987654321L, -1)

        assertEquals(WAIT_MISMATCH, result)
    }

    @Test
    fun i32WaitReturnsTimeoutWhenNotNotified() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x1234)

        val startTime = System.nanoTime()
        val result = I32AtomicWait(memory, 0, 0x1234, 10_000_000) // 10ms timeout
        val elapsed = System.nanoTime() - startTime

        assertEquals(WAIT_TIMEOUT, result)
        assertTrue(elapsed >= 10_000_000, "Should have waited at least 10ms")
    }

    @Test
    fun i64WaitReturnsTimeoutWhenNotNotified() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putLong(0, 0x1234L)

        val startTime = System.nanoTime()
        val result = I64AtomicWait(memory, 0, 0x1234L, 10_000_000) // 10ms timeout
        val elapsed = System.nanoTime() - startTime

        assertEquals(WAIT_TIMEOUT, result)
        assertTrue(elapsed >= 10_000_000, "Should have waited at least 10ms")
    }

    @Test
    fun notifyReturnsZeroWhenNoWaiters() {
        val memory = linearMemory()

        val result = AtomicNotify(memory, 0, 10)

        assertEquals(0, result)
    }

    @Test
    fun notifyWakesWaiterAndReturnsOk() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x1234)

        val executor = Executors.newSingleThreadExecutor()
        val latch = CountDownLatch(1)
        val waitResult = AtomicInteger(-1)

        executor.submit {
            latch.countDown()
            waitResult.set(I32AtomicWait(memory, 0, 0x1234, 5_000_000_000L)) // 5s timeout
        }

        latch.await()
        Thread.sleep(50) // Give it time to enter wait

        val notifyCount = AtomicNotify(memory, 0, 1)

        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.SECONDS)

        assertEquals(1, notifyCount)
        assertEquals(WAIT_OK, waitResult.get())
    }

    @Test
    fun notifyWakesWaitersInFifoOrder() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x1234)

        val executor = Executors.newFixedThreadPool(3)
        val startLatch = CountDownLatch(3)
        val wakeOrder = mutableListOf<Int>()
        val orderLock = Object()

        // Start 3 waiters
        for (i in 1..3) {
            val waiterId = i
            executor.submit {
                startLatch.countDown()
                I32AtomicWait(memory, 0, 0x1234, 5_000_000_000L)
                synchronized(orderLock) {
                    wakeOrder.add(waiterId)
                }
            }
            Thread.sleep(20) // Stagger the waits to ensure FIFO order
        }

        startLatch.await()
        Thread.sleep(50) // Ensure all are waiting

        // Wake them one by one
        for (i in 1..3) {
            AtomicNotify(memory, 0, 1)
            Thread.sleep(20) // Give time for wake to be recorded
        }

        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.SECONDS)

        assertEquals(listOf(1, 2, 3), wakeOrder, "Waiters should wake in FIFO order")
    }

    @Test
    fun notifyCountLimitsWokenWaiters() {
        val memory = linearMemory()
        val buffer = memory.memory
        buffer.putInt(0, 0x1234)

        val executor = Executors.newFixedThreadPool(3)
        val startLatch = CountDownLatch(3)
        val wokenCount = AtomicInteger(0)

        // Start 3 waiters
        for (i in 1..3) {
            executor.submit {
                startLatch.countDown()
                val result = I32AtomicWait(memory, 0, 0x1234, 100_000_000L) // 100ms timeout
                if (result == WAIT_OK) {
                    wokenCount.incrementAndGet()
                }
            }
        }

        startLatch.await()
        Thread.sleep(50) // Ensure all are waiting

        // Only wake 2
        val notified = AtomicNotify(memory, 0, 2)

        executor.shutdown()
        executor.awaitTermination(1, TimeUnit.SECONDS)

        assertEquals(2, notified)
        assertEquals(2, wokenCount.get())
    }

    @Test
    fun fenceDoesNotThrow() {
        val memory = linearMemory()

        AtomicFence(memory)
    }

    private fun linearMemory() = ByteBufferLinearMemory(LinearMemory.Pages(1u))
}

