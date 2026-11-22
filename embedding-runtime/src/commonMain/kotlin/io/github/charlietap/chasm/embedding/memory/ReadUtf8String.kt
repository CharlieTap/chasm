package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.memory
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.memory.read.StringReader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException

fun readUtf8String(
    store: Store,
    memory: Memory,
    pointer: Int,
    stringLengthInBytes: Int,
): String {
    return readUtf8String(
        store = store,
        memory = memory,
        pointer = pointer,
        stringLengthInBytes = stringLengthInBytes,
        stringReader = ::StringReader,
    ).getOrElse(::throwAccessorFailure)
}

internal fun readUtf8String(
    store: Store,
    memory: Memory,
    pointer: Int,
    stringLengthInBytes: Int,
    stringReader: StringReader,
): Result<String, ModuleTrapError> = runCatching {
    val instance = store.memory(memory)
    stringReader(instance.data, pointer, stringLengthInBytes)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        is IndexOutOfBoundsException -> InvocationError.MemoryOperationOutOfBounds
        else -> throw e
    }
}
