package io.github.charlietap.chasm.executor.instantiator.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal typealias ImportDescriptorMatcher = (Store, Module, ModuleImport.Descriptor, ExternalValue) -> Result<Boolean, ModuleTrapError>

internal fun ImportDescriptorMatcher(
    store: Store,
    module: Module,
    descriptor: ModuleImport.Descriptor,
    externalValue: ExternalValue,
): Result<Boolean, ModuleTrapError> =
    ImportDescriptorMatcher(
        store = store,
        module = module,
        descriptor = descriptor,
        externalValue = externalValue,
        functionImportMatcher = ::FunctionImportMatcher,
        tableImportMatcher = ::TableImportMatcher,
        memoryImportMatcher = ::MemoryImportMatcher,
        globalImportMatcher = ::GlobalImportMatcher,
        tagImportMatcher = ::TagImportMatcher,
    )

internal fun ImportDescriptorMatcher(
    store: Store,
    module: Module,
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
            functionImportMatcher(store, module, descriptor, externalValue).bind()
        } else {
            false
        }
        is ModuleImport.Descriptor.Table -> if (externalValue is ExternalValue.Table) {
            tableImportMatcher(store, descriptor, externalValue).bind()
        } else {
            false
        }
        is ModuleImport.Descriptor.Memory -> if (externalValue is ExternalValue.Memory) {
            memoryImportMatcher(store, descriptor, externalValue).bind()
        } else {
            false
        }
        is ModuleImport.Descriptor.Global -> if (externalValue is ExternalValue.Global) {
            globalImportMatcher(store, descriptor, externalValue).bind()
        } else {
            false
        }
        is ModuleImport.Descriptor.Tag -> if (externalValue is ExternalValue.Tag) {
            tagImportMatcher(store, descriptor, externalValue).bind()
        } else {
            false
        }
    }
}
