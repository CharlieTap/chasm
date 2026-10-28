package io.github.charlietap.chasm.executor.instantiator.allocation.export

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.declarations.ExportDeclarations
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue

internal typealias ExportAllocator = (InstantiationContext, Byte, Int) -> Result<ExternalValue, ModuleTrapError>

internal inline fun ExportAllocator(
    context: InstantiationContext,
    kind: Byte,
    index: Int,
): Result<ExternalValue, ModuleTrapError> {
    val instance = context.instance!!
    return when (kind) {
        ExportDeclarations.FUNCTION -> Ok(ExternalValue.Function(instance.functionAddresses[index]))
        ExportDeclarations.TABLE -> Ok(ExternalValue.Table(instance.tableAddresses[index]))
        ExportDeclarations.GLOBAL -> Ok(ExternalValue.Global(instance.globalAddresses[index]))
        ExportDeclarations.MEMORY -> Ok(ExternalValue.Memory(instance.memAddresses[index]))
        ExportDeclarations.TAG -> Ok(ExternalValue.Tag(instance.tagAddresses[index]))
        else -> error("export kind $kind is not one of ExportDeclarations' kinds")
    }
}
