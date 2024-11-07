package io.github.charlietap.chasm.executor.instantiator.matching

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal typealias ImportDescriptorMatcher = (InstantiationContext, ModuleImport.Descriptor, ExternalValue) -> Result<Boolean, ModuleTrapError>

internal fun ImportDescriptorMatcher(
    context: InstantiationContext,
    descriptor: ModuleImport.Descriptor,
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
    descriptor: ModuleImport.Descriptor,
    externalValue: ExternalValue,
    functionImportMatcher: FunctionImportMatcher,
    tableImportMatcher: TableImportMatcher,
    memoryImportMatcher: MemoryImportMatcher,
    globalImportMatcher: GlobalImportMatcher,
    tagImportMatcher: TagImportMatcher,
): Result<Boolean, ModuleTrapError> = binding {
    when (descriptor) {
        is ModuleImport.Descriptor.Function -> if (externalValue is ExternalValue.Function) {
            functionImportMatcher(context, descriptor, externalValue).bind()
        } else {
            false
        }
        is ModuleImport.Descriptor.Table -> if (externalValue is ExternalValue.Table) {
            tableImportMatcher(context, descriptor, externalValue).bind()
        } else {
            false
        }
        is ModuleImport.Descriptor.Memory -> if (externalValue is ExternalValue.Memory) {
            memoryImportMatcher(context, descriptor, externalValue).bind()
        } else {
            false
        }
        is ModuleImport.Descriptor.Global -> if (externalValue is ExternalValue.Global) {
            globalImportMatcher(context, descriptor, externalValue).bind()
        } else {
            false
        }
        is ModuleImport.Descriptor.Tag -> if (externalValue is ExternalValue.Tag) {
            tagImportMatcher(context, descriptor, externalValue).bind()
        } else {
            false
        }
    }
}
