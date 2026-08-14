package io.github.charlietap.chasm.decoder

import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ParallelWasmModuleDecoderTest {

    @Test
    fun `parallel decoding produces the serial module`() = runTest {
        val executor = TestParallelTaskExecutor()

        val serial = WasmModuleDecoder(ModuleConfig(), TWO_FUNCTION_MODULE)
        val parallel = ParallelWasmModuleDecoder(
            config = ModuleConfig(),
            bytes = TWO_FUNCTION_MODULE,
            taskExecutor = executor,
            mode = DecodingMode.PARALLEL,
            availableProcessors = 4,
        )

        assertEquals(serial, parallel)
        assertEquals(3, executor.taskCount)
    }

    @Test
    fun `invalid layout falls back to serial decoding`() = runTest {
        val executor = TestParallelTaskExecutor()
        val malformed = TWO_FUNCTION_MODULE.copyOf(TWO_FUNCTION_MODULE.size - 1)

        val serial = WasmModuleDecoder(ModuleConfig(), malformed)
        val parallel = ParallelWasmModuleDecoder(
            config = ModuleConfig(),
            bytes = malformed,
            taskExecutor = executor,
            mode = DecodingMode.PARALLEL,
            availableProcessors = 4,
        )

        assertEquals(serial.toString(), parallel.toString())
        assertEquals(0, executor.taskCount)
    }

    @Test
    fun `body errors are independent of task completion order`() = runTest {
        val executor = TestParallelTaskExecutor(reverse = true)
        val malformed = TWO_FUNCTION_MODULE.copyOf().also { it[24] = 0xFF.toByte() }

        val serial = WasmModuleDecoder(ModuleConfig(), malformed)
        val parallel = ParallelWasmModuleDecoder(
            config = ModuleConfig(),
            bytes = malformed,
            taskExecutor = executor,
            mode = DecodingMode.PARALLEL,
            availableProcessors = 4,
        )

        assertEquals(serial.toString(), parallel.toString())
        assertTrue(parallel.toString().contains("Err"))
    }

    private class TestParallelTaskExecutor(
        private val reverse: Boolean = false,
    ) : ParallelTaskExecutor {

        var taskCount = 0
            private set

        override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> {
            taskCount = tasks.size
            val scope = ParallelTaskScope {}
            val indices = if (reverse) tasks.indices.reversed() else tasks.indices
            val results = arrayOfNulls<Any?>(tasks.size)
            for (index in indices) results[index] = tasks[index](scope)
            @Suppress("UNCHECKED_CAST")
            return results.asList() as List<T>
        }
    }

    private companion object {
        val TWO_FUNCTION_MODULE = byteArrayOf(
            0x00,
            0x61,
            0x73,
            0x6D,
            0x01,
            0x00,
            0x00,
            0x00,
            0x01,
            0x04,
            0x01,
            0x60,
            0x00,
            0x00,
            0x03,
            0x03,
            0x02,
            0x00,
            0x00,
            0x0A,
            0x07,
            0x02,
            0x02,
            0x00,
            0x0B,
            0x02,
            0x00,
            0x0B,
        )
    }
}
