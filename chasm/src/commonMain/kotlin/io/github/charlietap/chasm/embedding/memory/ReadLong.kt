package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.memory.ext.toLongLittleEndian
import io.github.charlietap.chasm.memory.read.BytesReader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.memory

fun readLong(
    store: Store,
    memory: Memory,
    memoryPointer: Int,
): ChasmResult<Long, ChasmError.ExecutionError> =
    readLong(
        store = store,
        memory = memory,
        memoryPointer = memoryPointer,
        bytesReader = ::BytesReader,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun readLong(
    store: Store,
    memory: Memory,
    memoryPointer: Int,
    bytesReader: BytesReader,
): Result<Long, ModuleTrapError> = runCatching {
    val instance = store.store.memory(memory.reference.address)
    val bytes = bytesReader(instance.data, ByteArray(8), memoryPointer, 8, 0)
    bytes.toLongLittleEndian()
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> InvocationError.MemoryOperationOutOfBounds
    }
}
