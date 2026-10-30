package io.github.charlietap.chasm.aot

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.CompiledCode
import io.github.charlietap.chasm.aot.internal.compiledModule
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals
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
            config = RuntimeConfig(),
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

    private inner class FakeCode : CompiledCode {

        override fun install(
            installation: CodeInstallation,
        ) = fail("installation belongs to the instantiator")

        override fun buildDeclarations(): ModuleDeclarations = emptyDeclarations
    }

    private fun emptyModuleDeclarations(): ModuleDeclarations {
        val bytes = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)
        val config = ModuleConfig()
        val module = WasmModuleDecoder(config, bytes).getOrThrow { IllegalStateException(it.toString()) }
        val validated = WasmModuleValidator(config, module).getOrThrow { IllegalStateException(it.toString()) }
        return ModuleDeclarationsFactory(validated)
    }
}
