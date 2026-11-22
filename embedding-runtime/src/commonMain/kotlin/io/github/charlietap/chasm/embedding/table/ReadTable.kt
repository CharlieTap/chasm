package io.github.charlietap.chasm.embedding.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.table
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.toReferenceValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue

fun readTable(
    store: Store,
    table: Table,
    elementIndex: Int,
): ReferenceValue {
    return internalReadTable(
        store = store,
        table = table,
        elementIndex = elementIndex,
    ).getOrElse(::throwAccessorFailure)
}

internal fun internalReadTable(
    store: Store,
    table: Table,
    elementIndex: Int,
): Result<ReferenceValue, ModuleTrapError> = runCatching {
    val instance = store.table(table)
    instance.element(elementIndex).toReferenceValue()
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        is IndexOutOfBoundsException -> InvocationError.TableOperationOutOfBounds
        else -> throw e
    }
}
