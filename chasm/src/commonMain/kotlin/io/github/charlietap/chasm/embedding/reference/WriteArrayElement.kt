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
import io.github.charlietap.chasm.embedding.transform.FieldValueEncoder
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.array
import io.github.charlietap.chasm.runtime.value.FieldValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.Mutability

fun writeArrayElement(
    store: Store,
    array: ReferenceValue.Array,
    index: Int,
    value: FieldValue,
): ChasmResult<Unit, ChasmError.ExecutionError> =
    internalWriteArrayElement(
        store = store,
        array = array,
        index = index,
        value = value,
        fieldValueEncoder = ::FieldValueEncoder,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalWriteArrayElement(
    store: Store,
    array: ReferenceValue.Array,
    index: Int,
    value: FieldValue,
    fieldValueEncoder: FieldValueEncoder,
): Result<Unit, ModuleTrapError> = runCatching {
    val instance = store.store.array(array.address)
    val fieldType = instance.arrayType.fieldType

    if (fieldType.mutability != Mutability.Var) {
        throw InvocationException(InvocationError.ArrayCopyOnAConstArray)
    }
    if (index !in instance.fields.indices) {
        throw InvocationException(InvocationError.ArrayFieldLookupFailed(index))
    }

    instance.fields[index] = fieldValueEncoder(value, fieldType)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> InvocationError.ArrayFieldLookupFailed(index)
    }
}
