package io.github.charlietap.chasm.embedding.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.ext.table
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ReferenceValue

fun writeTable(
    store: Store,
    table: Table,
    elementIndex: Int,
    value: ReferenceValue,
): ChasmResult<Unit, ChasmError.ExecutionError> =
    internalWriteTable(
        store = store,
        table = table,
        elementIndex = elementIndex,
        value = value,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalWriteTable(
    store: Store,
    table: Table,
    elementIndex: Int,
    value: ReferenceValue,
): Result<Unit, ModuleTrapError> = runCatching {
    val table = store.table(table)
    table.elements[elementIndex] = value.toLongFromBoxed()
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        is IndexOutOfBoundsException -> InvocationError.TableOperationOutOfBounds
        else -> throw e
    }
}
