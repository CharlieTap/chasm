package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.memory
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.memory.read.BytesReader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException

fun readByte(
    store: Store,
    memory: Memory,
    pointer: Int,
): Byte {
    return readByte(
        store = store,
        memory = memory,
        pointer = pointer,
        bytesReader = ::BytesReader,
    ).getOrElse(::throwAccessorFailure)
}

internal fun readByte(
    store: Store,
    memory: Memory,
    pointer: Int,
    bytesReader: BytesReader,
): Result<Byte, ModuleTrapError> = runCatching {
    val instance = store.memory(memory)
    bytesReader(instance.data, ByteArray(1), pointer, 1, 0).first()
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        is IndexOutOfBoundsException -> InvocationError.MemoryOperationOutOfBounds
        else -> throw e
    }
}
