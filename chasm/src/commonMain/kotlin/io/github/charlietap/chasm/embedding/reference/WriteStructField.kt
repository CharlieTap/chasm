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
import io.github.charlietap.chasm.runtime.ext.struct
import io.github.charlietap.chasm.runtime.value.FieldValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.Mutability

fun writeStructField(
    store: Store,
    struct: ReferenceValue.Struct,
    index: Int,
    value: FieldValue,
): ChasmResult<Unit, ChasmError.ExecutionError> =
    internalWriteStructField(
        store = store,
        struct = struct,
        index = index,
        value = value,
        fieldValueEncoder = ::FieldValueEncoder,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalWriteStructField(
    store: Store,
    struct: ReferenceValue.Struct,
    index: Int,
    value: FieldValue,
    fieldValueEncoder: FieldValueEncoder,
): Result<Unit, ModuleTrapError> = runCatching {
    val instance = store.store.struct(struct.address)
    val fieldType = instance.structType.fields.getOrNull(index)
        ?: throw InvocationException(InvocationError.StructFieldLookupFailed(index))

    if (fieldType.mutability != Mutability.Var) {
        throw InvocationException(InvocationError.ArrayCopyOnAConstArray)
    }

    instance.fields[index] = fieldValueEncoder(value, fieldType)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> InvocationError.StructFieldLookupFailed(index)
    }
}
