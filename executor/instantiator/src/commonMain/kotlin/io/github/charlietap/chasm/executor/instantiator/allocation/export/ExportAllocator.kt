package io.github.charlietap.chasm.executor.instantiator.allocation.export

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue

internal typealias ExportAllocator = (InstantiationContext, Export.Descriptor) -> Result<ExternalValue, ModuleTrapError>

internal inline fun ExportAllocator(
    context: InstantiationContext,
    descriptor: Export.Descriptor,
): Result<ExternalValue, ModuleTrapError> = binding {
    val instance = context.instance!!
    when (val descriptor = descriptor) {
        is Export.Descriptor.Function -> {
            ExternalValue.Function(instance.functionAddresses[descriptor.functionIndex.idx.toInt()])
        }
        is Export.Descriptor.Table -> {
            ExternalValue.Table(instance.tableAddresses[descriptor.tableIndex.idx.toInt()])
        }
        is Export.Descriptor.Global -> {
            ExternalValue.Global(instance.globalAddresses[descriptor.globalIndex.idx.toInt()])
        }
        is Export.Descriptor.Memory -> {
            ExternalValue.Memory(instance.memAddresses[descriptor.memoryIndex.idx.toInt()])
        }
        is Export.Descriptor.Tag -> {
            ExternalValue.Tag(instance.tagAddresses[descriptor.tagIndex.idx.toInt()])
        }
    }
}
