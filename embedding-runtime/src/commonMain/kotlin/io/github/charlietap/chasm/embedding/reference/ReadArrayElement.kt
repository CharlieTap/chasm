package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.runtime
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.transform.FieldValueDecoder
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.value.FieldValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue

fun readArrayElement(
    store: Store,
    array: ReferenceValue.Array,
    index: Int,
): FieldValue {
    return internalReadArrayElement(
        store = store,
        array = array,
        index = index,
        fieldValueDecoder = ::FieldValueDecoder,
    ).getOrElse(::throwAccessorFailure)
}

internal fun internalReadArrayElement(
    store: Store,
    array: ReferenceValue.Array,
    index: Int,
    fieldValueDecoder: FieldValueDecoder,
): Result<FieldValue, ModuleTrapError> = runCatching {
    val heap = store.runtime().heap
    val rawReference = array.toLong()
    val fieldType = heap.arrayFieldType(rawReference)
    fieldValueDecoder(heap.getArrayElement(rawReference, index), fieldType)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> throw e
    }
}
