package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.memory
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.memory.write.BytesWriter
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException

fun writeBytes(
    store: Store,
    memory: Memory,
    pointer: Int,
    buffer: ByteArray,
    bufferPointer: Int = 0,
    bytesToWrite: Int = buffer.size - bufferPointer,
) {
    writeBytes(
        store = store,
        memory = memory,
        pointer = pointer,
        buffer = buffer,
        bufferPointer = bufferPointer,
        bytesToWrite = bytesToWrite,
        bytesWriter = ::BytesWriter,
    ).getOrElse(::throwAccessorFailure)
}

internal fun writeBytes(
    store: Store,
    memory: Memory,
    pointer: Int,
    buffer: ByteArray,
    bufferPointer: Int,
    bytesToWrite: Int,
    bytesWriter: BytesWriter,
): Result<Unit, ModuleTrapError> = runCatching {
    val instance = store.memory(memory)
    bytesWriter(instance.data, instance.size, buffer, pointer, bytesToWrite, bufferPointer)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        is IndexOutOfBoundsException -> InvocationError.MemoryOperationOutOfBounds
        else -> throw e
    }
}
