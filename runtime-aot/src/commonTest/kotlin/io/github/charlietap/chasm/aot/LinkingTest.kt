package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.aot.compiled.fixture
import io.github.charlietap.chasm.aot.compiled.generate
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.GrowthFailureStrategy
import io.github.charlietap.chasm.config.KB
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MB
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.bytes
import io.github.charlietap.chasm.config.fuel
import io.github.charlietap.chasm.config.pages
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.remainingFuel
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class LinkingTest {

    @Test
    fun `a second module compiled with the same settings joins the store`() {
        runBlocking {

            val config = RuntimeConfig(
                interrupts = true,
                limits = Limits(
                    cpu = CpuLimits.Metered(100.fuel),
                    memory = MemoryLimits(linearMemory = 4.pages, onGrowthFailure = GrowthFailureStrategy.Trap),
                ),
            )
            val first = compiled(ADD, config)
            val second = compiled(CALL_ADD, config)
            val store = store(first)

            val adder = instance(store, first).expect("first instantiates")
            val add = assertNotNull(exports(adder).function("add"))
            val caller = instance(store, second, listOf(Import("add", "add", add))).expect("second instantiates")

            assertEquals(ChasmResult.Success(listOf(I32(5))), invoke(caller, "call_add", listOf(I32(2), I32(3))))
        }
    }

    @Test
    fun `a second module compiled with a different fuel is unlinkable`() {
        assertUnlinkable(
            firstConfig = cpuLimits(CpuLimits.Metered(100.fuel)),
            secondConfig = cpuLimits(CpuLimits.Metered(50.fuel)),
            message = "the module was compiled with fuel 50 but the store has fuel 100",
        )
    }

    @Test
    fun `a second module compiled with different interrupts is unlinkable`() {
        assertUnlinkable(
            firstConfig = RuntimeConfig(),
            secondConfig = RuntimeConfig(interrupts = true),
            message = "the module was compiled with interrupts true but the store has interrupts false",
        )
    }

    @Test
    fun `a second module compiled with a different linearMemory is unlinkable`() {
        assertUnlinkable(
            firstConfig = memoryLimits(MemoryLimits(linearMemory = 256.MB)),
            secondConfig = memoryLimits(MemoryLimits(linearMemory = 16.MB)),
            message = "the module was compiled with linearMemory 16 MB but the store allows 256 MB",
        )
    }

    @Test
    fun `a second module compiled with a different gcHeap is unlinkable`() {
        assertUnlinkable(
            firstConfig = memoryLimits(MemoryLimits(gcHeap = 1.MB)),
            secondConfig = RuntimeConfig(),
            message = "the module was compiled with gcHeap no limit but the store allows 1 MB",
        )
    }

    @Test
    fun `a second module compiled with different tables is unlinkable`() {
        assertUnlinkable(
            firstConfig = memoryLimits(MemoryLimits(tables = 128.bytes)),
            secondConfig = memoryLimits(MemoryLimits(tables = 100.bytes)),
            message = "the module was compiled with tables 100 bytes but the store allows 128 bytes",
        )
    }

    @Test
    fun `a second module compiled with a different stack is unlinkable`() {
        assertUnlinkable(
            firstConfig = memoryLimits(MemoryLimits(stack = 64.KB)),
            secondConfig = memoryLimits(MemoryLimits(stack = 32.KB)),
            message = "the module was compiled with stack 32 KB but the store allows 64 KB",
        )
    }

    @Test
    fun `a second module compiled with the default stack is unlinkable in a store with a set stack`() {
        assertUnlinkable(
            firstConfig = memoryLimits(MemoryLimits(stack = 64.KB)),
            secondConfig = RuntimeConfig(),
            message = "the module was compiled with stack default but the store allows 64 KB",
        )
    }

    @Test
    fun `a second module compiled with a different onGrowthFailure is unlinkable`() {
        assertUnlinkable(
            firstConfig = RuntimeConfig(),
            secondConfig = memoryLimits(MemoryLimits(onGrowthFailure = GrowthFailureStrategy.Trap)),
            message = "the module was compiled with onGrowthFailure Trap but the store has onGrowthFailure Continue",
        )
    }

    @Test
    fun `a module compiled without stack traces joins a store whose first module has them`() {
        runBlocking {

            val traced = RuntimeConfig(stacktraces = true)
            val first = generate(fixture(ADD), JvmOptions(entryClass = "chasm.aot.traced.WasmModule", runtime = traced)).compiledModule()
            val second = generate(fixture(ADD), JvmOptions(entryClass = "chasm.aot.untraced.WasmModule")).compiledModule()
            val store = store(first)

            instance(store, first).expect("first instantiates")
            val result = instance(store, second)

            assertIs<ChasmResult.Success<*>>(result)
        }
    }

    @Test
    fun `a module is not instantiated in another module's store without matching it`() {
        runBlocking {

            val metered = compiled(ADD, cpuLimits(CpuLimits.Metered(10.fuel)))
            val unrelated = generate(fixture(LIMITS), JvmOptions(entryClass = "chasm.aot.unrelated.WasmModule")).compiledModule()

            val result = instance(store(metered), unrelated)

            assertEquals(ChasmResult.Error(ExecutionError.Unlinkable("the module was compiled with fuel unmetered but the store has fuel 10")), result)
        }
    }

    @Test
    fun `store adds the fuel the module was compiled with`() {

        val module = compiled(ADD, cpuLimits(CpuLimits.Metered(1_234.fuel)))

        val store = store(module)

        assertEquals(1_234L, remainingFuel(store))
    }

    @Test
    fun `a store made from a module compiled with limits enforces them`() {
        runBlocking {

            val module = compiled(LIMITS, memoryLimits(MemoryLimits(linearMemory = 2.pages)))
            val instance = instance(store(module), module).expect("instantiates")

            assertEquals(ChasmResult.Success(listOf(I32(1))), invoke(instance, "memory-grow", listOf(I32(1))))
            assertEquals(ChasmResult.Success(listOf(I32(-1))), invoke(instance, "memory-grow", listOf(I32(1))))
        }
    }

    private fun assertUnlinkable(
        firstConfig: RuntimeConfig,
        secondConfig: RuntimeConfig,
        message: String,
    ) {

        val first = compiled(ADD, firstConfig)
        val second = compiled(ADD, secondConfig)

        val result = runBlocking { instance(store(first), second) }

        assertEquals(ChasmResult.Error(ExecutionError.Unlinkable(message)), result)
    }

    private fun cpuLimits(cpu: CpuLimits): RuntimeConfig {
        return RuntimeConfig(limits = Limits(cpu = cpu))
    }

    private fun memoryLimits(memory: MemoryLimits): RuntimeConfig {
        return RuntimeConfig(limits = Limits(memory = memory))
    }

    private fun compiled(
        name: String,
        config: RuntimeConfig,
    ): Module {
        return generate(fixture(name), JvmOptions(entryClass = "chasm.aot.linking.WasmModule", runtime = config)).compiledModule()
    }

    private companion object {
        const val ADD = "add"
        const val CALL_ADD = "call_add"
        const val LIMITS = "limits"
    }
}
