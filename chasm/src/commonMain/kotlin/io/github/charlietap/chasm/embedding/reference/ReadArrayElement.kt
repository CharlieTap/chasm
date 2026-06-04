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
import io.github.charlietap.chasm.embedding.transform.FieldValueDecoder
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.value.FieldValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue

fun readArrayElement(
    store: Store,
    array: ReferenceValue.Array,
    index: Int,
): ChasmResult<FieldValue, ChasmError.ExecutionError> =
    internalReadArrayElement(
        store = store,
        array = array,
        index = index,
        fieldValueDecoder = ::FieldValueDecoder,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalReadArrayElement(
    store: Store,
    array: ReferenceValue.Array,
    index: Int,
    fieldValueDecoder: FieldValueDecoder,
): Result<FieldValue, ModuleTrapError> = runCatching {
    val instance = store.store.array(array.address)
    val value = instance.fields.getOrNull(index)
        ?: throw InvocationException(InvocationError.ArrayFieldLookupFailed(index))
    fieldValueDecoder(value, instance.arrayType.fieldType)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> InvocationError.ArrayFieldLookupFailed(index)
    }
}
