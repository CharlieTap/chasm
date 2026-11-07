package io.github.charlietap.chasm.aot

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.compiled.fixture
import io.github.charlietap.chasm.aot.internal.CompiledCode
import io.github.charlietap.chasm.aot.internal.compiledModule
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ModuleInfo
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.validator.WasmModuleValidator
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.fail
import io.github.charlietap.chasm.declarations.moduleInfo as declarationsModuleInfo

@OptIn(InternalChasmApi::class)
class ModuleInfoTest {

    @Test
    fun `module info contains the compiled module's imports and exports`() {
        val declarations = declarations(fixture("call_add"))
        val module = compiledModule(FakeCode { declarations })

        val result = moduleInfo(module)

        val info = assertIs<ChasmResult.Success<ModuleInfo>>(result).result
        assertEquals(declarationsModuleInfo(declarations), info)
        assertEquals(listOf("add" to "add"), info.imports.map { it.moduleName to it.entityName })
        assertEquals(listOf("call_add", "tail_add"), info.exports.map { it.name })
    }

    @Test
    fun `module info is empty for a module without imports or exports`() {
        val declarations = declarations(byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00))
        val module = compiledModule(FakeCode { declarations })

        val result = moduleInfo(module)

        assertEquals(ChasmResult.Success(ModuleInfo(emptyList(), emptyList())), result)
    }

    @Test
    fun `a declarations failure is returned as an execution error`() {
        val module = compiledModule(FakeCode { throw IllegalStateException("broken declarations") })

        val result = moduleInfo(module)

        assertEquals(
            ChasmResult.Error(ChasmError.ExecutionError("Compiled module rejected: broken declarations")),
            result,
        )
    }

    @Test
    fun `a declarations failure without a message uses its string representation`() {
        val failure = IllegalStateException()
        val module = compiledModule(FakeCode { throw failure })

        val result = moduleInfo(module)

        assertEquals(
            ChasmResult.Error(ChasmError.ExecutionError("Compiled module rejected: $failure")),
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

        override fun runtimeAbi(): Int = fail("module info does not read metadata")

        override fun meterFuel(): Boolean = fail("module info does not read metadata")

        override fun interruptible(): Boolean = fail("module info does not read metadata")

        override fun stacktraces(): Boolean = fail("module info does not read metadata")

        override fun initialFuel(): Long = fail("module info does not read metadata")

        override fun gcStrategy(): Int = fail("module info does not read metadata")

        override fun gcThresholdKind(): Int = fail("module info does not read metadata")

        override fun gcThresholdValue(): Int = fail("module info does not read metadata")

        override fun gcHeapBytes(): Long = fail("module info does not read metadata")

        override fun memoryPages(): Int = fail("module info does not read metadata")

        override fun tableElements(): Int = fail("module info does not read metadata")

        override fun trapOnGrowFailure(): Boolean = fail("module info does not read metadata")

        override fun stackBytes(): Long = fail("module info does not read metadata")
    }
}
