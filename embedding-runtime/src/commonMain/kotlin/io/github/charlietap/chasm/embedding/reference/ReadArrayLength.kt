package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.runtime
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.value.ReferenceValue

fun readArrayLength(
    store: Store,
    array: ReferenceValue.Array,
): Int {
    return internalReadArrayLength(
        store = store,
        array = array,
    ).getOrElse(::throwAccessorFailure)
}

internal fun internalReadArrayLength(
    store: Store,
    array: ReferenceValue.Array,
): Result<Int, ModuleTrapError> = runCatching {
    store.runtime().heap.arrayLengthChecked(array.toLong())
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> throw e
    }
}
