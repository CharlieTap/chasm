package io.github.charlietap.chasm.aot

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.aot.internal.CompiledCode
import io.github.charlietap.chasm.aot.internal.UNSET
import io.github.charlietap.chasm.aot.internal.UNSET_LONG
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.config.StoreLimits
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes.expect
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

@OptIn(InternalChasmApi::class)
class InstanceTest {

    private val emptyDeclarations = emptyModuleDeclarations()

    @Test
    fun `a module is instantiated from its declarations into the store`() = runBlocking<Unit> {
        val code = FakeCode()

        val result = instance(store(StoreConfig()), module(code))

        assertIs<ChasmResult.Success<*>>(result)
        assertTrue(code.declarationsBuilt)
    }

    @Test
    fun `a dropped store is refused before the compiled module is checked`() = runBlocking<Unit> {

        val store = store(StoreConfig())
        dropStore(store)

        assertFailsWith<IllegalStateException> {
            instance(store, module(FakeCode(meterFuel = true)))
        }
    }

    @Test
    fun `a store that does not match the module's fuel and interrupt checks is rejected before instantiation`() = runBlocking<Unit> {
        val cases = listOf(
            Triple(FakeCode(meterFuel = true), StoreConfig(), "the module was compiled with fuel checks but the store does not meter fuel"),
            Triple(FakeCode(), StoreConfig(meterFuel = true), "the store meters fuel but the module was compiled without fuel checks"),
            Triple(
                FakeCode(interruptible = true),
                StoreConfig(),
                "the module was compiled with interrupt checks but the store is not interruptible",
            ),
            Triple(
                FakeCode(),
                StoreConfig(interruptible = true),
                "the store is interruptible but the module was compiled without interrupt checks",
            ),
        )

        cases.forEach { (code, config, reason) ->
            val result = instance(store(config), module(code))

            assertEquals(ChasmResult.Error(ExecutionError.Unsupported("Compiled module rejected: $reason")), result, reason)
            assertFalse(code.installed, "a rejected module was installed")
        }
    }

    @Test
    fun `a module compiled for another runtime is rejected before anything else is read from it`() {
        val code = FakeCode(abi = COMPILED_RUNTIME_ABI + 1, readOnlyAbi = true)

        val result = compiledModule(fakeModuleClass(code))

        assertEquals(
            malformed("the module was compiled for runtime ABI ${COMPILED_RUNTIME_ABI + 1} but this runtime supports $COMPILED_RUNTIME_ABI"),
            result,
        )
        assertFalse(code.declarationsBuilt, "a rejected module built its declarations")
    }

    @Test
    fun `a module compiled before its metadata existed is rejected`() {
        val code = FakeCode(missingAbi = true)

        val result = compiledModule(fakeModuleClass(code))

        assertEquals(malformed("the module was compiled for an older runtime"), result)
        assertFalse(code.declarationsBuilt, "a rejected module built its declarations")
    }

    @Test
    fun `a class that is not on the class path is rejected`() {
        val result = compiledModule("chasm.aot.missing.WasmModule", InstanceTest::class.java.classLoader)

        assertEquals(malformed("chasm.aot.missing.WasmModule is not on the class path"), result)
    }

    @Test
    fun `a class without a compiled module instance is rejected`() {
        val result = compiledModule(String::class.java)

        assertEquals(malformed("java.lang.String is not a compiled module class"), result)
    }

    @Test
    fun `a class whose compiled module instance is not static is rejected`() {

        val moduleClass = InstanceFieldModuleClass::class.java

        val result = compiledModule(moduleClass)

        assertEquals(malformed("${moduleClass.name} is not a compiled module class"), result)
    }

    @Test
    fun `a class whose initializer fails is rejected`() {

        val moduleClass = FailingModuleClass::class.java

        val fromClass = compiledModule(moduleClass)
        val fromName = compiledModule(moduleClass.name, moduleClass.classLoader)

        listOf(fromClass, fromName).forEach { result ->
            val error = assertIs<ChasmResult.Error<ModuleError>>(result).error
            val message = assertIs<ModuleError.Malformed>(error).message
            assertTrue(message.startsWith("Compiled module rejected: ${moduleClass.name} could not be initialised"), message)
        }
    }

    @Test
    fun `the metadata reads back what the module was built with`() {
        val code = FakeCode(
            meterFuel = true,
            stacktraces = true,
            initialFuel = 42,
            gcStrategy = 1,
            gcThresholdKind = 1,
            gcThresholdValue = 512,
            gcHeapBytes = 1024,
            memoryPages = 3,
            stackBytes = 4096,
        )

        val metadata = compiledModuleMetadata(module(code))

        assertEquals(Capabilities(meterFuel = true, interruptible = false, stacktraces = true), metadata.capabilities)
        assertEquals(42L, metadata.initialFuel)
        assertEquals(COMPILED_RUNTIME_ABI, metadata.runtimeAbi)
        assertEquals(
            StoreConfig(
                meterFuel = true,
                gcStrategy = GCStrategy.TRADITIONAL,
                gcThreshold = GCThreshold.KB(512),
                limits = StoreLimits(gcHeapBytes = 1024, memoryPages = 3, stackBytes = 4096),
            ),
            metadata.storeConfig,
        )
    }

    @Test
    fun `an unset stack limit reads back as the platform default`() {
        val metadata = compiledModuleMetadata(module(FakeCode()))

        assertEquals(null, metadata.initialFuel)
        assertEquals(StoreLimits().stackBytes, metadata.storeConfig.limits.stackBytes)
        assertEquals(null, metadata.storeConfig.gcStrategy)
        assertEquals(null, metadata.storeConfig.gcThreshold)
    }

    private fun module(
        code: FakeCode,
    ): Module = compiledModule(fakeModuleClass(code)).expect("loads")

    private fun malformed(reason: String): ChasmResult.Error<ModuleError.Malformed> {
        return ChasmResult.Error(ModuleError.Malformed("Compiled module rejected: $reason"))
    }

    private inner class FakeCode(
        private val abi: Int = COMPILED_RUNTIME_ABI,
        private val readOnlyAbi: Boolean = false,
        private val missingAbi: Boolean = false,
        private val meterFuel: Boolean = false,
        private val interruptible: Boolean = false,
        private val stacktraces: Boolean = false,
        private val initialFuel: Long = UNSET_LONG,
        private val gcStrategy: Int = UNSET,
        private val gcThresholdKind: Int = UNSET,
        private val gcThresholdValue: Int = UNSET,
        private val gcHeapBytes: Long = UNSET_LONG,
        private val memoryPages: Int = UNSET,
        private val stackBytes: Long = UNSET_LONG,
    ) : CompiledCode {

        var declarationsBuilt = false

        var installed = false

        override fun install(
            installation: CodeInstallation,
        ) {
            installed = true
        }

        override fun buildDeclarations(): ModuleDeclarations {
            declarationsBuilt = true
            return emptyDeclarations
        }

        override fun trapSitesResource(): String? = null

        override fun runtimeAbi(): Int = if (missingAbi) throw AbstractMethodError("runtimeAbi") else abi

        override fun meterFuel(): Boolean = metadata { meterFuel }

        override fun interruptible(): Boolean = metadata { interruptible }

        override fun stacktraces(): Boolean = metadata { stacktraces }

        override fun initialFuel(): Long = metadata { initialFuel }

        override fun gcStrategy(): Int = metadata { gcStrategy }

        override fun gcThresholdKind(): Int = metadata { gcThresholdKind }

        override fun gcThresholdValue(): Int = metadata { gcThresholdValue }

        override fun gcHeapBytes(): Long = metadata { gcHeapBytes }

        override fun memoryPages(): Int = metadata { memoryPages }

        override fun tableElements(): Int = metadata { UNSET }

        override fun trapOnGrowFailure(): Boolean = metadata { false }

        override fun stackBytes(): Long = metadata { stackBytes }

        private inline fun <T> metadata(
            value: () -> T,
        ): T = if (readOnlyAbi) fail("only the runtime ABI may be read from a module for another runtime") else value()
    }

    private fun emptyModuleDeclarations(): ModuleDeclarations {
        val bytes = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { IllegalStateException(it.toString()) }
        val validated = WasmModuleValidator(config, module).getOrThrow { IllegalStateException(it.toString()) }
        return ModuleDeclarationsFactory(validated)
    }
}
