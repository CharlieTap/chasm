package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.runtime
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.transform.FieldValueDecoder
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.value.FieldValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue

fun readStructField(
    store: Store,
    struct: ReferenceValue.Struct,
    index: Int,
): FieldValue {
    return internalReadStructField(
        store = store,
        struct = struct,
        index = index,
        fieldValueDecoder = ::FieldValueDecoder,
    ).getOrElse(::throwAccessorFailure)
}

internal fun internalReadStructField(
    store: Store,
    struct: ReferenceValue.Struct,
    index: Int,
    fieldValueDecoder: FieldValueDecoder,
): Result<FieldValue, ModuleTrapError> = runCatching {
    val heap = store.runtime().heap
    val rawReference = struct.toLong()
    val fieldType = heap.structFieldType(rawReference, index)
    fieldValueDecoder(heap.getStructField(rawReference, index), fieldType)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> throw e
    }
}
