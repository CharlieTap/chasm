package io.github.charlietap.chasm.embedding.extern

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.chasm.runtime.value.ReferenceValue

inline fun <reified T> readExternValueAs(
    store: Store,
    reference: ReferenceValue,
): ChasmResult<T?, ChasmError.ExecutionError> =
    readExternValue(
        store = store,
        reference = reference,
    ).fold(
        onSuccess = { value ->
            if (value == null || value is T) {
                Success(value)
            } else {
                Error(ChasmError.ExecutionError("Expected host value ${T::class} but found ${value::class}"))
            }
        },
        onError = ::Error,
    )
