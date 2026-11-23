@file:OptIn(InternalChasmApi::class)

package io.github.charlietap.chasm.aot

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.aot.internal.AotModule
import io.github.charlietap.chasm.aot.internal.COMPILED_RUNTIME_ABI
import io.github.charlietap.chasm.aot.internal.UNSET_LONG
import io.github.charlietap.chasm.config.Bytes
import io.github.charlietap.chasm.config.CpuLimits
import io.github.charlietap.chasm.config.Fuel
import io.github.charlietap.chasm.config.GrowthFailureStrategy
import io.github.charlietap.chasm.config.Limits
import io.github.charlietap.chasm.config.MemoryLimits
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.declarations.moduleInfo
import io.github.charlietap.chasm.embedding.error.ModuleError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.shapes._module

@Suppress("TooGenericExceptionCaught")
fun module(compiled: CompiledModule): ChasmResult<Module, ModuleError> {

    val abi = try {
        compiled.runtimeAbi()
    } catch (_: LinkageError) {
        return malformed("the module was compiled for an older runtime")
    }
    if (abi != COMPILED_RUNTIME_ABI) {
        return malformed("the module was compiled for runtime ABI $abi but this runtime supports $COMPILED_RUNTIME_ABI")
    }

    val fuel = compiled.fuel()
    val linearMemory = compiled.linearMemoryBytes()
    val gcHeap = compiled.gcHeapBytes()
    val tables = compiled.tableBytes()
    val stack = compiled.stackBytes()
    if (listOf(fuel, linearMemory, gcHeap, tables, stack).any { value -> value < UNSET_LONG }) {
        return malformed("the module's settings are invalid")
    }

    val declarations = try {
        compiled.buildDeclarations()
    } catch (exception: Exception) {
        return malformed(exception.message ?: exception.toString())
    }
    val info = moduleInfo(declarations)

    val config = RuntimeConfig(
        interrupts = compiled.interrupts(),
        stacktraces = compiled.stacktraces(),
        limits = Limits(
            cpu = if (fuel == UNSET_LONG) CpuLimits.Unmetered else CpuLimits.Metered(Fuel(fuel)),
            memory = MemoryLimits(
                linearMemory = if (linearMemory == UNSET_LONG) null else Bytes(linearMemory),
                gcHeap = if (gcHeap == UNSET_LONG) null else Bytes(gcHeap),
                tables = if (tables == UNSET_LONG) null else Bytes(tables),
                stack = if (stack == UNSET_LONG) null else Bytes(stack),
                onGrowthFailure = if (compiled.trapOnGrowthFailure()) GrowthFailureStrategy.Trap else GrowthFailureStrategy.Continue,
            ),
        ),
    )

    val payload = AotModule(compiled, config, declarations, compiled.resourceRoot())

    return Success(_module(info.imports, info.exports, payload))
}

private fun malformed(reason: String): Error<ModuleError.Malformed> {
    return Error(ModuleError.Malformed("Compiled module rejected: $reason"))
}
