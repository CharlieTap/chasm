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

fun readBytes(
    store: Store,
    memory: Memory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int = 0,
): ByteArray {
    return readBytes(
        store = store,
        memory = memory,
        buffer = buffer,
        memoryPointer = memoryPointer,
        bytesToRead = bytesToRead,
        bufferPointer = bufferPointer,
        bytesReader = ::BytesReader,
    ).getOrElse(::throwAccessorFailure)
}

internal fun readBytes(
    store: Store,
    memory: Memory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
    bytesReader: BytesReader,
): Result<ByteArray, ModuleTrapError> = runCatching {
    val instance = store.memory(memory)
    bytesReader(instance.data, buffer, memoryPointer, bytesToRead, bufferPointer)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        is IndexOutOfBoundsException -> InvocationError.MemoryOperationOutOfBounds
        else -> throw e
    }
}
