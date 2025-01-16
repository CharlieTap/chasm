package io.github.charlietap.chasm.embedding.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.element
import io.github.charlietap.chasm.executor.runtime.ext.table
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue

fun readTable(
    store: Store,
    table: Table,
    elementIndex: Int,
): ChasmResult<ReferenceValue, ChasmError.ExecutionError> =
    internalReadTable(
        store = store,
        table = table,
        elementIndex = elementIndex,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalReadTable(
    store: Store,
    table: Table,
    elementIndex: Int,
): Result<ReferenceValue, ModuleTrapError> = binding {
    val table = store.store.table(table.reference.address).bind()
    table.element(elementIndex)
}
