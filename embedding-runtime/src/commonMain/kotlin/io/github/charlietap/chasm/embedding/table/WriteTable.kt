package io.github.charlietap.chasm.embedding.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.table
import io.github.charlietap.chasm.embedding.internal.matchesType
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.ValueType

fun writeTable(
    store: Store,
    table: Table,
    elementIndex: Int,
    value: ReferenceValue,
) {
    internalWriteTable(
        store = store,
        table = table,
        elementIndex = elementIndex,
        value = value,
    ).getOrElse(::throwAccessorFailure)
}

internal fun internalWriteTable(
    store: Store,
    table: Table,
    elementIndex: Int,
    value: ReferenceValue,
): Result<Unit, ModuleTrapError> {
    return runCatching {

        val instance = store.table(table)
        val elementType = ValueType.Reference(instance.type.referenceType)
        require(matchesType(elementType, value)) { "value $value does not match table element type ${instance.type.referenceType}" }

        instance.writeRaw(elementIndex, value.toLongFromBoxed())
    }.mapError { failure ->
        when (failure) {
            is InvocationException -> failure.error
            is IndexOutOfBoundsException -> InvocationError.TableOperationOutOfBounds
            else -> throw failure
        }
    }
}
