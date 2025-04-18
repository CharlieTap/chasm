package io.github.charlietap.chasm.executor.instantiator.matching

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.ir.module.Import
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.matching.TableTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.ir.module.Import as ModuleImport

internal typealias TableImportMatcher = (InstantiationContext, Import.Descriptor.Table, ExternalValue.Table) -> Result<Boolean, ModuleTrapError>

internal fun TableImportMatcher(
    context: InstantiationContext,
    descriptor: ModuleImport.Descriptor.Table,
    import: ExternalValue.Table,
): Result<Boolean, ModuleTrapError> =
    TableImportMatcher(
        context = context,
        descriptor = descriptor,
        import = import,
        tableTypeMatcher = ::TableTypeMatcher,
    )

internal inline fun TableImportMatcher(
    context: InstantiationContext,
    descriptor: ModuleImport.Descriptor.Table,
    import: ExternalValue.Table,
    crossinline tableTypeMatcher: TypeMatcher<TableType>,
): Result<Boolean, ModuleTrapError> = binding {
    val store = context.store
    val actualTable = store.table(import.address)
    val actualTableType = actualTable.type

    val requiredTableType = descriptor.type

    tableTypeMatcher(actualTableType, requiredTableType, context)
}
