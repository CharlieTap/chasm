package io.github.charlietap.chasm.compiled

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.compiled.code.fixture
import io.github.charlietap.chasm.compiled.code.generate
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiled.format.UNPINNED_OFFSET
import io.github.charlietap.chasm.compiled.format.UNSET_LONG
import io.github.charlietap.chasm.compiled.internal.platformTarget
import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.declarations.DataSource
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.fail

class InstanceTest {

    private val emptyDeclarations = emptyModuleDeclarations()

    @Test
    fun `a module is instantiated from its declarations into the store`() = runBlocking<Unit> {

        val compiled = FakeCompiledModule()
        val module = loaded(compiled)

        val result = instance(store(module), module)

        assertIs<ChasmResult.Success<*>>(result)
        assertTrue(compiled.declarationsBuilt)
        assertTrue(compiled.installed)
    }

    @Test
    fun `a dropped store is refused before the compiled module is checked`() = runBlocking<Unit> {

        val store = store(loaded(FakeCompiledModule()))
        dropStore(store)

        assertFailsWith<IllegalStateException> {
            instance(store, loaded(FakeCompiledModule(fuel = 10)))
        }
    }

    @Test
    fun `a module compiled for another runtime is rejected before anything else is read from it`() {

        val compiled = FakeCompiledModule(abi = COMPILED_RUNTIME_ABI + 1, readOnlyAbi = true)

        val result = module(compiled)

        assertEquals(
            malformed("the module was compiled for runtime ABI ${COMPILED_RUNTIME_ABI + 1} but this runtime supports $COMPILED_RUNTIME_ABI"),
            result,
        )
        assertFalse(compiled.declarationsBuilt, "a rejected module built its declarations")
    }

    @Test
    fun `a module compiled before its metadata existed is rejected`() {

        val compiled = FakeCompiledModule(missingAbi = true)

        val result = module(compiled)

        assertEquals(malformed("the module was compiled for an older runtime"), result)
        assertFalse(compiled.declarationsBuilt, "a rejected module built its declarations")
    }

    @Test
    fun `a module whose settings are out of range is rejected`() {

        val modules = listOf(
            FakeCompiledModule(fuel = -2),
            FakeCompiledModule(linearMemoryBytes = -2),
            FakeCompiledModule(gcHeapBytes = -2),
            FakeCompiledModule(tableBytes = -2),
            FakeCompiledModule(stackBytes = -2),
        )

        modules.forEach { compiled ->
            assertEquals(malformed("the module's settings are invalid"), module(compiled))
            assertFalse(compiled.declarationsBuilt, "a rejected module built its declarations")
        }
    }

    @Test
    fun `an unset stack limit gives the store the platform default`() = runBlocking<Unit> {

        val defaults = generate(fixture(LIMITS)).compiledModule()
        val small = generate(fixture(LIMITS), JvmOptions(runtime = RuntimeConfig(limits = Limits(memory = MemoryLimits(stack = Bytes(SMALL_STACK_BYTES)))))).compiledModule()
        val deep = listOf(I32(DEEP_RECURSION))

        val unset = invoke(instance(store(defaults), defaults).expect("instantiates"), "recurse", deep)
        val limited = invoke(instance(store(small), small).expect("instantiates"), "recurse", deep)

        assertEquals(ChasmResult.Success(listOf(I32(DEEP_RECURSION))), unset)
        val trapped = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<*>>(limited).error)
        assertEquals(TrapReason.CALL_STACK_EXHAUSTED, trapped.trap.reason)
    }

    private fun loaded(compiled: FakeCompiledModule): Module {
        return module(compiled).expect("loads")
    }

    private fun malformed(reason: String): ChasmResult.Error<ModuleError.Malformed> {
        return ChasmResult.Error(ModuleError.Malformed("Compiled module rejected: $reason"))
    }

    private inner class FakeCompiledModule(
        private val abi: Int = COMPILED_RUNTIME_ABI,
        private val readOnlyAbi: Boolean = false,
        private val missingAbi: Boolean = false,
        private val fuel: Long = UNSET_LONG,
        private val linearMemoryBytes: Long = UNSET_LONG,
        private val gcHeapBytes: Long = UNSET_LONG,
        private val tableBytes: Long = UNSET_LONG,
        private val stackBytes: Long = UNSET_LONG,
    ) : CompiledModule {

        var declarationsBuilt = false

        var installed = false

        override fun install(installation: CodeInstallation) {
            installed = true
        }

        override fun buildDeclarations(data: DataSource): ModuleDeclarations {
            declarationsBuilt = true
            return emptyDeclarations
        }

        override fun resourceRoot(): String {
            return "chasm/aot/FakeModule"
        }

        override fun target(): Int {
            return setting { platformTarget().code }
        }

        override fun byteArrayBaseOffset(): Long {
            return setting { UNPINNED_OFFSET }
        }

        override fun classes(): Array<String> {
            return setting { emptyArray() }
        }

        override fun initializesEagerly(): Boolean {
            return setting { false }
        }

        override fun structLanes(): LongArray {
            return LongArray(0)
        }

        override fun newStruct(
            type: Int,
            rtt: Any,
        ): Any {
            throw UnsupportedOperationException("the fake module declares no struct types")
        }

        override fun runtimeAbi(): Int {

            if (missingAbi) {
                throw AbstractMethodError("runtimeAbi")
            }

            return abi
        }

        override fun interrupts(): Boolean {
            return setting { false }
        }

        override fun stacktraces(): Boolean {
            return setting { false }
        }

        override fun fuel(): Long {
            return setting { fuel }
        }

        override fun linearMemoryBytes(): Long {
            return setting { linearMemoryBytes }
        }

        override fun gcHeapBytes(): Long {
            return setting { gcHeapBytes }
        }

        override fun tableBytes(): Long {
            return setting { tableBytes }
        }

        override fun stackBytes(): Long {
            return setting { stackBytes }
        }

        override fun trapOnGrowthFailure(): Boolean {
            return setting { false }
        }

        private inline fun <T> setting(value: () -> T): T {

            if (readOnlyAbi) {
                fail("only the runtime ABI may be read from a module for another runtime")
            }

            return value()
        }
    }

    private fun emptyModuleDeclarations(): ModuleDeclarations {

        val bytes = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { IllegalStateException(it.toString()) }
        val validated = WasmModuleValidator(config, module).getOrThrow { IllegalStateException(it.toString()) }

        return ModuleDeclarationsFactory(validated)
    }

    private companion object {
        const val LIMITS = "limits"
        const val SMALL_STACK_BYTES = 4L * 1024L
        const val DEEP_RECURSION = 1_000
    }
}
