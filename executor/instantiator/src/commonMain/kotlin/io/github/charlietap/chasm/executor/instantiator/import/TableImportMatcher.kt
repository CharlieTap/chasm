package io.github.charlietap.chasm.executor.instantiator.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.ast.module.Import as ModuleImport

internal typealias TableImportMatcher = (Store, Import.Descriptor.Table, ExternalValue.Table) -> Result<Boolean, ModuleTrapError>

internal fun TableImportMatcher(
    store: Store,
    descriptor: ModuleImport.Descriptor.Table,
    import: ExternalValue.Table,
): Result<Boolean, ModuleTrapError> =
    TableImportMatcher(
        store = store,
        descriptor = descriptor,
        import = import,
        limitsMatcher = ::LimitsMatcherImpl,
    )

internal fun TableImportMatcher(
    store: Store,
    descriptor: ModuleImport.Descriptor.Table,
    import: ExternalValue.Table,
    limitsMatcher: LimitsMatcher,
): Result<Boolean, ModuleTrapError> = binding {
    val actualTable = store.table(import.address).bind()
    val actualTableType = actualTable.type

    val requiredTableType = descriptor.type

    requiredTableType.referenceType == actualTableType.referenceType &&
        limitsMatcher(actualTableType.limits, requiredTableType.limits)
}
