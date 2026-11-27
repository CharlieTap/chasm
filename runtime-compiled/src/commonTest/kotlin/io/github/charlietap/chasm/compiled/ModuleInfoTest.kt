package io.github.charlietap.chasm.compiled

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.compiled.code.fixture
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiled.format.UNPINNED_OFFSET
import io.github.charlietap.chasm.compiled.format.UNSET_LONG
import io.github.charlietap.chasm.compiled.internal.platformTarget
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.declarations.DataSource
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

class ModuleInfoTest {

    @Test
    fun `module info contains the compiled module's imports and exports`() {
        val declarations = declarations(fixture("call_add"))
        val module = module(FakeCompiledModule { declarations }).expect("loads")

        val info = moduleInfo(module)

        assertEquals(declarationsModuleInfo(declarations), info)
        assertEquals(listOf("add" to "add"), info.imports.map { it.moduleName to it.entityName })
        assertEquals(listOf("call_add", "tail_add"), info.exports.map { it.name })
    }

    @Test
    fun `module info is empty for a module without imports or exports`() {
        val declarations = declarations(byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00))
        val module = module(FakeCompiledModule { declarations }).expect("loads")

        val info = moduleInfo(module)

        assertEquals(ModuleInfo(emptyList(), emptyList()), info)
    }

    @Test
    fun `a declarations failure rejects the module as malformed`() {
        val result = module(FakeCompiledModule { throw IllegalStateException("broken declarations") })

        assertEquals(
            ChasmResult.Error(ModuleError.Malformed("Compiled module rejected: broken declarations")),
            result,
        )
    }

    @Test
    fun `a declarations failure without a message uses its string representation`() {
        val failure = IllegalStateException()

        val result = module(FakeCompiledModule { throw failure })

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

    private class FakeCompiledModule(
        private val declarations: () -> ModuleDeclarations,
    ) : CompiledModule {

        override fun buildDeclarations(data: DataSource): ModuleDeclarations {
            return declarations()
        }

        override fun target(): Int {
            return platformTarget().code
        }

        override fun byteArrayBaseOffset(): Long {
            return UNPINNED_OFFSET
        }

        override fun classes(): Array<String> {
            return emptyArray()
        }

        override fun initializesEagerly(): Boolean {
            return false
        }

        override fun structLanes(): LongArray {
            return LongArray(0)
        }

        override fun newStruct(
            type: Int,
            rtt: Any,
        ): Any {
            fail("module info allocates no structs")
        }

        override fun install(installation: CodeInstallation) {
            fail("module info does not install code")
        }

        override fun resourceRoot(): String {
            return "chasm/aot/FakeModule"
        }

        override fun runtimeAbi(): Int {
            return COMPILED_RUNTIME_ABI
        }

        override fun interrupts(): Boolean {
            return false
        }

        override fun stacktraces(): Boolean {
            return false
        }

        override fun fuel(): Long {
            return UNSET_LONG
        }

        override fun linearMemoryBytes(): Long {
            return UNSET_LONG
        }

        override fun gcHeapBytes(): Long {
            return UNSET_LONG
        }

        override fun tableBytes(): Long {
            return UNSET_LONG
        }

        override fun stackBytes(): Long {
            return UNSET_LONG
        }

        override fun trapOnGrowthFailure(): Boolean {
            return false
        }
    }
}
