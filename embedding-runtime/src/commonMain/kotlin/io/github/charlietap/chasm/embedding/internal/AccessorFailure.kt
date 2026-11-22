package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError

internal fun throwAccessorFailure(error: ModuleTrapError): Nothing {
    throw when (error) {
        InvocationError.MemoryOperationOutOfBounds,
        InvocationError.TableOperationOutOfBounds,
        InvocationError.ArrayOperationOutOfBounds,
        is InvocationError.TableElementLookupFailed,
        is InvocationError.ArrayFieldLookupFailed,
        is InvocationError.StructFieldLookupFailed,
        -> IndexOutOfBoundsException(error.toString())
        InvocationError.StoreDropped,
        InvocationError.InvocationOfADeinstantiatedInstance,
        -> IllegalStateException(error.toString())
        else -> IllegalArgumentException(error.toString())
    }
}
