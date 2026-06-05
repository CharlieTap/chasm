package io.github.charlietap.chasm.embedding.extern

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
import io.github.charlietap.chasm.runtime.ext.host
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType

fun readExternValue(
    store: Store,
    reference: ReferenceValue,
): ChasmResult<Any?, ChasmError.ExecutionError> =
    internalReadExternValue(
        store = store,
        reference = reference,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun internalReadExternValue(
    store: Store,
    reference: ReferenceValue,
): Result<Any?, ModuleTrapError> = runCatching {
    when (reference) {
        is ReferenceValue.Null -> when (reference.heapType) {
            AbstractHeapType.Extern,
            AbstractHeapType.NoExtern,
            -> null
            else -> throw InvocationException(InvocationError.ExternReferenceExpected)
        }
        is ReferenceValue.Extern -> when (val wrapped = reference.referenceValue) {
            is ReferenceValue.Host -> store.store.host(wrapped.address).value
            else -> throw InvocationException(InvocationError.UnexpectedReferenceValue)
        }
        else -> throw InvocationException(InvocationError.ExternReferenceExpected)
    }
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> InvocationError.ExternReferenceExpected
    }
}
