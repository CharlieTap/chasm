package io.github.charlietap.chasm.executor.instantiator.matching

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.ir.module.Import
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.instance.ExternalValue

internal typealias ImportDescriptorMatcher = (InstantiationContext, Import.Descriptor, ExternalValue) -> Result<Boolean, ModuleTrapError>

internal fun ImportDescriptorMatcher(
    context: InstantiationContext,
    descriptor: Import.Descriptor,
    externalValue: ExternalValue,
): Result<Boolean, ModuleTrapError> =
    ImportDescriptorMatcher(
        context = context,
        descriptor = descriptor,
        externalValue = externalValue,
        functionImportMatcher = ::FunctionImportMatcher,
        tableImportMatcher = ::TableImportMatcher,
        memoryImportMatcher = ::MemoryImportMatcher,
        globalImportMatcher = ::GlobalImportMatcher,
        tagImportMatcher = ::TagImportMatcher,
    )

internal fun ImportDescriptorMatcher(
    context: InstantiationContext,
    descriptor: Import.Descriptor,
    externalValue: ExternalValue,
    functionImportMatcher: FunctionImportMatcher,
    tableImportMatcher: TableImportMatcher,
    memoryImportMatcher: MemoryImportMatcher,
    globalImportMatcher: GlobalImportMatcher,
    tagImportMatcher: TagImportMatcher,
): Result<Boolean, ModuleTrapError> = binding {
    when (descriptor) {
        is Import.Descriptor.Function -> if (externalValue is ExternalValue.Function) {
            functionImportMatcher(context, descriptor, externalValue).bind()
        } else {
            false
        }
        is Import.Descriptor.Table -> if (externalValue is ExternalValue.Table) {
            tableImportMatcher(context, descriptor, externalValue).bind()
        } else {
            false
        }
        is Import.Descriptor.Memory -> if (externalValue is ExternalValue.Memory) {
            memoryImportMatcher(context, descriptor, externalValue).bind()
        } else {
            false
        }
        is Import.Descriptor.Global -> if (externalValue is ExternalValue.Global) {
            globalImportMatcher(context, descriptor, externalValue).bind()
        } else {
            false
        }
        is Import.Descriptor.Tag -> if (externalValue is ExternalValue.Tag) {
            tagImportMatcher(context, descriptor, externalValue).bind()
        } else {
            false
        }
    }
}
