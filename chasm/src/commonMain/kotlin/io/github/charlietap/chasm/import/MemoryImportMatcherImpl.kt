package io.github.charlietap.chasm.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal fun MemoryImportMatcherImpl(
    store: Store,
    descriptor: ModuleImport.Descriptor.Memory,
    import: ExternalValue.Memory,
): Result<Boolean, ModuleTrapError> =
    MemoryImportMatcherImpl(
        store = store,
        descriptor = descriptor,
        import = import,
        limitsMatcher = ::LimitsMatcherImpl,
    )

internal fun MemoryImportMatcherImpl(
    store: Store,
    descriptor: ModuleImport.Descriptor.Memory,
    import: ExternalValue.Memory,
    limitsMatcher: LimitsMatcher,
): Result<Boolean, ModuleTrapError> = binding {
    val actualMemory = store.memory(import.address).bind()
    val actualMemoryType = actualMemory.type

    val requiredMemoryType = descriptor.type

    limitsMatcher(actualMemoryType.limits, requiredMemoryType.limits)
}
