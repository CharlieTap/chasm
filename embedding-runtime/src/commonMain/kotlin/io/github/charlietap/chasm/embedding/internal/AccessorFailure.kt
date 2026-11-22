package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException

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
        is InvocationError.GarbageCollectionFailed,
        -> IllegalStateException(error.toString())
        else -> IllegalArgumentException(error.toString())
    }
}

internal inline fun <T> withAccessorFailures(block: () -> T): T {
    return try {
        block()
    } catch (exception: InvocationException) {
        throwAccessorFailure(exception.error)
    }
}

internal fun checkIndex(
    index: Int,
    size: Int,
) {
    if (index < 0 || index >= size) {
        throw IndexOutOfBoundsException("index $index, size $size")
    }
}

internal fun checkRange(
    offset: Int,
    count: Int,
    size: Int,
) {
    if (offset < 0 || count < 0 || offset.toLong() + count > size) {
        throw IndexOutOfBoundsException("range $offset+$count, size $size")
    }
}
