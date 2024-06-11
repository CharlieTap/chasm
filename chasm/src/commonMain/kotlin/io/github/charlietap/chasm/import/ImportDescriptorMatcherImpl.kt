package io.github.charlietap.chasm.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal fun ImportDescriptorMatcherImpl(
    store: Store,
    module: Module,
    descriptor: ModuleImport.Descriptor,
    externalValue: ExternalValue,
): Result<Boolean, ModuleTrapError> =
    ImportDescriptorMatcherImpl(
        store = store,
        module = module,
        descriptor = descriptor,
        externalValue = externalValue,
        functionImportMatcher = ::FunctionImportMatcherImpl,
        tableImportMatcher = ::TableImportMatcherImpl,
        memoryImportMatcher = ::MemoryImportMatcherImpl,
        globalImportMatcher = ::GlobalImportMatcherImpl,
    )

internal fun ImportDescriptorMatcherImpl(
    store: Store,
    module: Module,
    descriptor: ModuleImport.Descriptor,
    externalValue: ExternalValue,
    functionImportMatcher: FunctionImportMatcher,
    tableImportMatcher: TableImportMatcher,
    memoryImportMatcher: MemoryImportMatcher,
    globalImportMatcher: GlobalImportMatcher,
): Result<Boolean, ModuleTrapError> = binding {
    when {
        descriptor is ModuleImport.Descriptor.Function && externalValue is ExternalValue.Function -> {
            functionImportMatcher(store, module, descriptor, externalValue).bind()
        }
        descriptor is ModuleImport.Descriptor.Table && externalValue is ExternalValue.Table -> {
            tableImportMatcher(store, descriptor, externalValue).bind()
        }
        descriptor is ModuleImport.Descriptor.Memory && externalValue is ExternalValue.Memory -> {
            memoryImportMatcher(store, descriptor, externalValue).bind()
        }
        descriptor is ModuleImport.Descriptor.Global && externalValue is ExternalValue.Global -> {
            globalImportMatcher(store, descriptor, externalValue).bind()
        }
        else -> false
    }
}
