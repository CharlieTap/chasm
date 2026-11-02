package io.github.charlietap.chasm.aot

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.aot.internal.CompiledCode
import io.github.charlietap.chasm.aot.internal.UNSET
import io.github.charlietap.chasm.aot.internal.UNSET_LONG
import io.github.charlietap.chasm.aot.internal.compiledModule
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.GCThreshold
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.config.StoreLimits
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.test.fail

@OptIn(InternalChasmApi::class)
class InstanceTest {

    private val emptyDeclarations = emptyModuleDeclarations()

    @Test
    fun `a module is instantiated from its declarations into the store`() {
        val store = store(StoreConfig())
        val module = compiledModule(FakeCode())
        var instantiated = false

        val result = instance(
            store = store,
            module = module,
            imports = emptyList(),
            instantiator = { _, runtimeStore, moduleDeclarations, imports, _ ->
                instantiated = true
                assertSame(store.store, runtimeStore)
                assertSame(emptyDeclarations, moduleDeclarations)
                assertEquals(emptyList(), imports)
                Err(InstantiationError.DataUnavailable("no data"))
            },
        )

        assertEquals(true, instantiated)
        assertIs<ChasmResult.Error<*>>(result)
        assertTrue("no data" in result.error.toString(), result.error.toString())
    }

    @Test
    fun `a store that does not match the module's fuel and interrupt checks is rejected before instantiation`() {
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
            assertEquals(rejected(reason), instantiateOnly(code, config), reason)
        }
    }

    @Test
    fun `a module compiled for another runtime is rejected before anything else is read from it`() {
        val code = FakeCode(abi = COMPILED_RUNTIME_ABI + 1, readOnlyAbi = true)

        val result = instantiateOnly(code, StoreConfig())

        assertEquals(
            rejected("the module was compiled for runtime ABI ${COMPILED_RUNTIME_ABI + 1} but this runtime supports $COMPILED_RUNTIME_ABI"),
            result,
        )
    }

    @Test
    fun `a module compiled before its metadata existed is rejected`() {
        val code = FakeCode(missingAbi = true)

        val result = instantiateOnly(code, StoreConfig())

        assertEquals(rejected("the module was compiled for an older runtime"), result)
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

        val metadata = compiledModule(code).metadata

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
        val metadata = compiledModule(FakeCode()).metadata

        assertEquals(null, metadata.initialFuel)
        assertEquals(StoreLimits().stackBytes, metadata.storeConfig.limits.stackBytes)
        assertEquals(null, metadata.storeConfig.gcStrategy)
        assertEquals(null, metadata.storeConfig.gcThreshold)
    }

    private fun instantiateOnly(
        code: FakeCode,
        config: StoreConfig,
    ): ChasmResult<*, *> {
        var instantiated = false
        val result = instance(
            store = store(config),
            module = compiledModule(code),
            imports = emptyList(),
            instantiator = { _, _, _, _, _ ->
                instantiated = true
                Err(InstantiationError.DataUnavailable("no data"))
            },
        )
        assertFalse(instantiated, "a rejected module reached instantiation")
        assertFalse(code.declarationsBuilt, "a rejected module built its declarations")
        return result
    }

    private fun rejected(
        reason: String,
    ) = ChasmResult.Error(ChasmError.ExecutionError("Compiled module rejected: $reason"))

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

        override fun install(
            installation: CodeInstallation,
        ) = fail("installation belongs to the instantiator")

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
