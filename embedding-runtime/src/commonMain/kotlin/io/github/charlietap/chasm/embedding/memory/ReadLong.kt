package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.memory
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.memory.ext.toLongLittleEndian
import io.github.charlietap.chasm.memory.read.BytesReader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException

fun readLong(
    store: Store,
    memory: Memory,
    memoryPointer: Int,
): Long {
    return readLong(
        store = store,
        memory = memory,
        memoryPointer = memoryPointer,
        bytesReader = ::BytesReader,
    ).getOrElse(::throwAccessorFailure)
}

internal fun readLong(
    store: Store,
    memory: Memory,
    memoryPointer: Int,
    bytesReader: BytesReader,
): Result<Long, ModuleTrapError> = runCatching {
    val instance = store.memory(memory)
    val bytes = bytesReader(instance.data, ByteArray(8), memoryPointer, 8, 0)
    bytes.toLongLittleEndian()
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        is IndexOutOfBoundsException -> InvocationError.MemoryOperationOutOfBounds
        else -> throw e
    }
}
