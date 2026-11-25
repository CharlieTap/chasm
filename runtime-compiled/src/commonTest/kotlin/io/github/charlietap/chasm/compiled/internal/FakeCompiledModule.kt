package io.github.charlietap.chasm.compiled.internal

import com.github.michaelbull.result.getOrThrow
import io.github.charlietap.chasm.compiled.CompiledModule
import io.github.charlietap.chasm.compiled.format.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.compiled.format.UNPINNED_OFFSET
import io.github.charlietap.chasm.compiled.format.UNSET_LONG
import io.github.charlietap.chasm.config.ModuleConfig
import io.github.charlietap.chasm.declarations.DataSource
import io.github.charlietap.chasm.declarations.ModuleDeclarations
import io.github.charlietap.chasm.decoder.WasmModuleDecoder
import io.github.charlietap.chasm.module.ModuleDeclarationsFactory
import io.github.charlietap.chasm.runtime.compiled.CodeInstallation
import io.github.charlietap.chasm.validator.WasmModuleValidator

internal class FakeCompiledModule(
    private val abi: Int = COMPILED_RUNTIME_ABI,
    private val target: Int = platformTarget().code,
    private val byteArrayBaseOffset: Long = UNPINNED_OFFSET,
    private val classes: () -> Array<String> = { emptyArray() },
    private val initializesEagerly: Boolean = false,
    private val fuel: Long = UNSET_LONG,
    private val stacktraces: Boolean = false,
    private val lanes: LongArray = LongArray(0),
    private val declarations: (DataSource) -> ModuleDeclarations = { emptyDeclarations() },
) : CompiledModule {

    override fun runtimeAbi(): Int {
        return abi
    }

    override fun target(): Int {
        return target
    }

    override fun byteArrayBaseOffset(): Long {
        return byteArrayBaseOffset
    }

    override fun interrupts(): Boolean {
        return false
    }

    override fun stacktraces(): Boolean {
        return stacktraces
    }

    override fun fuel(): Long {
        return fuel
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

    override fun resourceRoot(): String {
        return RESOURCE_ROOT
    }

    override fun classes(): Array<String> {
        return classes.invoke()
    }

    override fun initializesEagerly(): Boolean {
        return initializesEagerly
    }

    override fun buildDeclarations(data: DataSource): ModuleDeclarations {
        return declarations(data)
    }

    override fun structLanes(): LongArray {
        return lanes
    }

    override fun newStruct(
        type: Int,
        rtt: Any,
    ): Any {
        throw UnsupportedOperationException("a fake module allocates no structs")
    }

    override fun install(installation: CodeInstallation) {
        throw UnsupportedOperationException("a fake module installs no code")
    }

    companion object {

        const val RESOURCE_ROOT = "chasm/compiled/FakeModule"

        fun emptyDeclarations(): ModuleDeclarations {

            val bytes = byteArrayOf(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)
            val config = ModuleConfig()
            val module = WasmModuleDecoder(config, bytes).getOrThrow { error -> IllegalStateException(error.toString()) }
            val validated = WasmModuleValidator(config, module).getOrThrow { error -> IllegalStateException(error.toString()) }

            return ModuleDeclarationsFactory(validated)
        }
    }
}
