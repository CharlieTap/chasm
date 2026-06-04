package io.github.charlietap.chasm.embedding.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.value.ReferenceValue

fun readArrayLength(
    store: Store,
    array: ReferenceValue.Array,
): ChasmResult<Int, ChasmError.ExecutionError> =
    internalReadArrayLength(
        store = store,
        array = array,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalReadArrayLength(
    store: Store,
    array: ReferenceValue.Array,
): Result<Int, ModuleTrapError> = runCatching {
    store.store.array(array.address).fields.size
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> InvocationError.ArrayLookupFailed(array.address)
    }
}
