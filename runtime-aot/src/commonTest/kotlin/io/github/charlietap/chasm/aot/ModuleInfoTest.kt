package io.github.charlietap.chasm.aot

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.compiled.fixture
import io.github.charlietap.chasm.aot.internal.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.aot.internal.CompiledCode
import io.github.charlietap.chasm.aot.internal.UNSET
import io.github.charlietap.chasm.aot.internal.UNSET_LONG
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.moduleInfo
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail
import io.github.charlietap.chasm.declarations.moduleInfo as declarationsModuleInfo

@OptIn(InternalChasmApi::class)
class ModuleInfoTest {

    @Test
    fun `module info contains the compiled module's imports and exports`() {
        val declarations = declarations(fixture("call_add"))
        val module = compiledModule(fakeModuleClass(FakeCode { declarations })).expect("loads")

        val info = moduleInfo(module)

        assertEquals(declarationsModuleInfo(declarations), info)
        assertEquals(listOf("add" to "add"), info.imports.map { it.moduleName to it.entityName })
        assertEquals(listOf("call_add", "tail_add"), info.exports.map { it.name })
    }

    @Test
    fun `module info is empty for a module without imports or exports`() {
        val declarations = declarations(byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00))
        val module = compiledModule(fakeModuleClass(FakeCode { declarations })).expect("loads")

        val info = moduleInfo(module)

        assertEquals(ModuleInfo(emptyList(), emptyList()), info)
    }

    @Test
    fun `a declarations failure rejects the module as malformed`() {
        val result = compiledModule(fakeModuleClass(FakeCode { throw IllegalStateException("broken declarations") }))

        assertEquals(
            ChasmResult.Error(ModuleError.Malformed("Compiled module rejected: broken declarations")),
            result,
        )
    }

    @Test
    fun `a declarations failure without a message uses its string representation`() {
        val failure = IllegalStateException()

        val result = compiledModule(fakeModuleClass(FakeCode { throw failure }))

        assertEquals(
            ChasmResult.Error(ModuleError.Malformed("Compiled module rejected: $failure")),
            result,
        )
    }

    private fun declarations(
        bytes: ByteArray,
    ): ModuleDeclarations {
        val config = ModuleConfig()
        val decoded = WasmModuleDecoder(config, bytes).getOrThrow { IllegalStateException(it.toString()) }
        val validated = WasmModuleValidator(config, decoded).getOrThrow { IllegalStateException(it.toString()) }
        return ModuleDeclarationsFactory(validated)
    }

    private class FakeCode(
        private val declarations: () -> ModuleDeclarations,
    ) : CompiledCode {

        override fun buildDeclarations(): ModuleDeclarations = declarations()

        override fun install(
            installation: CodeInstallation,
        ) = fail("module info does not install code")

        override fun trapSitesResource(): String? = fail("module info does not read trap sites")

        override fun runtimeAbi(): Int = COMPILED_RUNTIME_ABI

        override fun meterFuel(): Boolean = false

        override fun interruptible(): Boolean = false

        override fun stacktraces(): Boolean = false

        override fun initialFuel(): Long = UNSET_LONG

        override fun gcStrategy(): Int = UNSET

        override fun gcThresholdKind(): Int = UNSET

        override fun gcThresholdValue(): Int = UNSET

        override fun gcHeapBytes(): Long = UNSET_LONG

        override fun memoryPages(): Int = UNSET

        override fun tableElements(): Int = UNSET

        override fun trapOnGrowFailure(): Boolean = false

        override fun stackBytes(): Long = UNSET_LONG
    }
}
