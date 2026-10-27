package io.github.charlietap.chasm.decoder

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.parallel.ParallelTaskExecutor
import io.github.charlietap.chasm.parallel.ParallelTaskScope
import io.github.charlietap.chasm.parallel.ParallelismPolicy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalAtomicApi::class)
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
        assertEquals(2, executor.taskCount)
    }

    @Test
    fun `automatic decoding starts a worker for the sections and each batch at most`() = runTest {
        val module = nopModule(8_000, 40_000, 8_000, 40_000)
        val executor = TestParallelTaskExecutor()

        val serial = WasmModuleDecoder(ModuleConfig(), module)
        val parallel = ParallelWasmModuleDecoder(
            config = ModuleConfig(),
            bytes = module,
            taskExecutor = executor,
            mode = DecodingMode.AUTO,
            availableProcessors = 8,
            policy = ParallelismPolicy(minimumWork = 64 * 1024L, workPerWorker = 16 * 1024L),
        )

        assertEquals(serial, parallel)
        assertEquals(3, executor.taskCount)
    }

    @Test
    fun `automatic decoding is serial when the batches are below the policy minimum`() = runTest {
        val module = nopModule(8_000, 280_000, 8_000, 280_000)
        val executor = TestParallelTaskExecutor()

        val serial = WasmModuleDecoder(ModuleConfig(), module)
        val parallel = ParallelWasmModuleDecoder(
            config = ModuleConfig(),
            bytes = module,
            taskExecutor = executor,
            mode = DecodingMode.AUTO,
            availableProcessors = 8,
            policy = ParallelismPolicy(minimumWork = 560 * 1024L, workPerWorker = 64 * 1024L, minimumWorkers = 4),
        )

        assertEquals(serial, parallel)
        assertEquals(0, executor.taskCount)
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

    @Test
    fun `an observer follows the module while it decodes`() = runTest {
        val executor = TestParallelTaskExecutor()
        val observer = RecordingObserver()

        val serial = WasmModuleDecoder(ModuleConfig(), TWO_NOP_FUNCTION_MODULE)
        val observed = ParallelWasmModuleDecoder(
            config = ModuleConfig(),
            bytes = TWO_NOP_FUNCTION_MODULE,
            taskExecutor = executor,
            mode = DecodingMode.PARALLEL,
            availableProcessors = 4,
            observer = observer,
        )

        assertEquals(serial, observed)
        assertEquals(2, executor.taskCount)
        assertEquals(listOf(2), observer.preludeFunctionCounts)
        assertEquals(listOf(0, 1), observer.begun.sorted())
        assertEquals(listOf(0, 1), observer.ended.sorted())
        assertEquals(listOf(NOP, NOP), observer.opcodes)
        assertEquals(1, observer.remainders)
    }

    @Test
    fun `bodies decoded before the observer is ready are shown to it afterwards`() = runTest {
        val executor = ConcurrentTaskExecutor()
        // the worker decoding the sections is held in the prelude until the other has decoded every body
        val observer = RecordingObserver(beforePrelude = { while (executor.completed.load() < 1) Unit })

        val serial = WasmModuleDecoder(ModuleConfig(), TWO_NOP_FUNCTION_MODULE)
        val observed = ParallelWasmModuleDecoder(
            config = ModuleConfig(),
            bytes = TWO_NOP_FUNCTION_MODULE,
            taskExecutor = executor,
            mode = DecodingMode.PARALLEL,
            availableProcessors = 4,
            observer = observer,
        )

        assertEquals(serial, observed)
        assertEquals(listOf(0, 1), observer.begun)
        assertEquals(listOf(0, 1), observer.ended)
        assertEquals(listOf(FunctionBodyObserver.UNKNOWN_OPCODE, FunctionBodyObserver.UNKNOWN_OPCODE), observer.opcodes)
        assertEquals(1, observer.remainders)
    }

    @Test
    fun `a single worker observes on the calling thread`() = runTest {
        val executor = TestParallelTaskExecutor()
        val observer = RecordingObserver()

        val serial = WasmModuleDecoder(ModuleConfig(), TWO_FUNCTION_MODULE)
        val observed = ParallelWasmModuleDecoder(
            config = ModuleConfig(),
            bytes = TWO_FUNCTION_MODULE,
            taskExecutor = executor,
            mode = DecodingMode.AUTO,
            availableProcessors = 1,
            observer = observer,
        )

        assertEquals(serial, observed)
        assertEquals(0, executor.taskCount)
        assertEquals(listOf(0, 1), observer.ended)
        assertEquals(1, observer.remainders)
    }

    @Test
    fun `bodies are not observed when the observer declines them`() = runTest {
        val observer = RecordingObserver(observeBodies = false)

        ParallelWasmModuleDecoder(
            config = ModuleConfig(),
            bytes = TWO_FUNCTION_MODULE,
            taskExecutor = TestParallelTaskExecutor(),
            mode = DecodingMode.PARALLEL,
            availableProcessors = 4,
            observer = observer,
        )

        assertEquals(emptyList(), observer.begun)
        assertEquals(0, observer.remainders)
    }

    @Test
    fun `a body that fails to decode is never ended`() = runTest {
        val observer = RecordingObserver()
        val malformed = TWO_FUNCTION_MODULE.copyOf().also { it[24] = 0xFF.toByte() }

        val observed = ParallelWasmModuleDecoder(
            config = ModuleConfig(),
            bytes = malformed,
            taskExecutor = TestParallelTaskExecutor(),
            mode = DecodingMode.PARALLEL,
            availableProcessors = 4,
            observer = observer,
        )

        assertEquals(WasmModuleDecoder(ModuleConfig(), malformed).toString(), observed.toString())
        assertTrue(0 in observer.begun)
        assertTrue(0 !in observer.ended)
    }

    @Test
    fun `an unreadable layout is decoded without the observer`() = runTest {
        val observer = RecordingObserver()
        val malformed = TWO_FUNCTION_MODULE.copyOf(TWO_FUNCTION_MODULE.size - 1)

        ParallelWasmModuleDecoder(
            config = ModuleConfig(),
            bytes = malformed,
            taskExecutor = TestParallelTaskExecutor(),
            mode = DecodingMode.PARALLEL,
            availableProcessors = 4,
            observer = observer,
        )

        assertEquals(emptyList(), observer.preludeFunctionCounts)
        assertEquals(0, observer.remainders)
    }

    @Test
    fun `a module without a function type for every body is decoded without the observer`() = runTest {
        val observer = RecordingObserver()
        // the function section declares one function where the code section has two bodies
        val malformed = TWO_FUNCTION_MODULE.copyOfRange(0, 14) +
            byteArrayOf(0x03, 0x02, 0x01, 0x00) +
            TWO_FUNCTION_MODULE.copyOfRange(19, TWO_FUNCTION_MODULE.size)

        val observed = ParallelWasmModuleDecoder(
            config = ModuleConfig(),
            bytes = malformed,
            taskExecutor = TestParallelTaskExecutor(),
            mode = DecodingMode.PARALLEL,
            availableProcessors = 4,
            observer = observer,
        )

        assertEquals(WasmModuleDecoder(ModuleConfig(), malformed).toString(), observed.toString())
        assertTrue(observed.toString().contains("Err"))
        assertEquals(emptyList(), observer.preludeFunctionCounts)
        assertEquals(0, observer.remainders)
    }

    private fun nopModule(vararg nopCounts: Int): ByteArray {
        val code = nopCounts.fold(uleb(nopCounts.size)) { section, nops ->
            val body = byteArrayOf(0x00) + ByteArray(nops) { NOP.toByte() } + byteArrayOf(0x0B)
            section + uleb(body.size) + body
        }
        val functions = uleb(nopCounts.size) + ByteArray(nopCounts.size)
        val types = byteArrayOf(0x01, 0x60, 0x00, 0x00)
        return byteArrayOf(0x00, 0x61, 0x73, 0x6D, 0x01, 0x00, 0x00, 0x00) +
            section(0x01, types) +
            section(0x03, functions) +
            section(0x0A, code)
    }

    private fun section(
        id: Int,
        content: ByteArray,
    ): ByteArray = byteArrayOf(id.toByte()) + uleb(content.size) + content

    private fun uleb(value: Int): ByteArray {
        val bytes = mutableListOf<Byte>()
        var remaining = value
        do {
            val byte = remaining and 0x7F
            remaining = remaining ushr 7
            bytes += (if (remaining == 0) byte else byte or 0x80).toByte()
        } while (remaining != 0)
        return bytes.toByteArray()
    }

    private class RecordingObserver(
        private val observeBodies: Boolean = true,
        private val beforePrelude: () -> Unit = {},
    ) : ModuleDecodeObserver {

        val preludeFunctionCounts = mutableListOf<Int>()
        val begun = mutableListOf<Int>()
        val ended = mutableListOf<Int>()
        val opcodes = mutableListOf<Int>()
        var remainders = 0
            private set

        override fun prelude(module: Module, dataCount: UInt?): Boolean {
            beforePrelude()
            preludeFunctionCounts += module.functions.size
            return observeBodies
        }

        override fun bodyObserver(): FunctionBodyObserver = object : FunctionBodyObserver {
            private var current = 0

            override fun begin(bodyIndex: Int, locals: List<Local>) {
                current = bodyIndex
                begun += bodyIndex
            }

            override fun instruction(opcode: Int, instruction: Instruction) {
                opcodes += opcode
            }

            override fun end() {
                ended += current
            }
        }

        override fun remainder(dataSegments: List<DataSegment>) {
            remainders++
        }
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

    private class ConcurrentTaskExecutor : ParallelTaskExecutor {

        val completed = AtomicInt(0)

        override suspend fun <T> execute(tasks: List<ParallelTaskScope.() -> T>): List<T> = withContext(Dispatchers.Default) {
            tasks.map { task ->
                async { task(ParallelTaskScope {}).also { completed.incrementAndFetch() } }
            }.awaitAll()
        }
    }

    private companion object {
        const val NOP = 0x01

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

        // the same module with a nop in each body
        val TWO_NOP_FUNCTION_MODULE = TWO_FUNCTION_MODULE.copyOfRange(0, 19) +
            byteArrayOf(0x0A, 0x09, 0x02, 0x03, 0x00, 0x01, 0x0B, 0x03, 0x00, 0x01, 0x0B)
    }
}
