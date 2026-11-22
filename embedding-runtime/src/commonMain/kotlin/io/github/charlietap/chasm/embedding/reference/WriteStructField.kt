package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.runtime
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.transform.FieldValueEncoder
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.value.FieldValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.Mutability

fun writeStructField(
    store: Store,
    struct: ReferenceValue.Struct,
    index: Int,
    value: FieldValue,
) {
    internalWriteStructField(
        store = store,
        struct = struct,
        index = index,
        value = value,
        fieldValueEncoder = ::FieldValueEncoder,
    ).getOrElse(::throwAccessorFailure)
}

internal fun internalWriteStructField(
    store: Store,
    struct: ReferenceValue.Struct,
    index: Int,
    value: FieldValue,
    fieldValueEncoder: FieldValueEncoder,
): Result<Unit, ModuleTrapError> = runCatching {
    val heap = store.runtime().heap
    val rawReference = struct.toLong()
    val fieldType = heap.structFieldType(rawReference, index)

    if (fieldType.mutability != Mutability.Var) {
        throw InvocationException(InvocationError.ArrayCopyOnAConstArray)
    }

    heap.setStructField(rawReference, index, fieldValueEncoder(value, fieldType))
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> throw e
    }
}
