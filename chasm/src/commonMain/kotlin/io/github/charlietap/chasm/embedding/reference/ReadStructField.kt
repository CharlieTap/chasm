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
import io.github.charlietap.chasm.runtime.ext.struct
import io.github.charlietap.chasm.runtime.value.FieldValue
import io.github.charlietap.chasm.runtime.value.ReferenceValue

fun readStructField(
    store: Store,
    struct: ReferenceValue.Struct,
    index: Int,
): ChasmResult<FieldValue, ChasmError.ExecutionError> =
    internalReadStructField(
        store = store,
        struct = struct,
        index = index,
        fieldValueDecoder = ::FieldValueDecoder,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalReadStructField(
    store: Store,
    struct: ReferenceValue.Struct,
    index: Int,
    fieldValueDecoder: FieldValueDecoder,
): Result<FieldValue, ModuleTrapError> = runCatching {
    val instance = store.store.struct(struct.address)
    val fieldType = instance.structType.fields.getOrNull(index)
        ?: throw InvocationException(InvocationError.StructFieldLookupFailed(index))
    fieldValueDecoder(instance.fields[index], fieldType)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> InvocationError.StructFieldLookupFailed(index)
    }
}
