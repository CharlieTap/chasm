package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.memory.read.BytesReader
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory

fun readBytes(
    store: Store,
    memory: Memory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int = 0,
): ChasmResult<ByteArray, ChasmError.ExecutionError> =
    readBytes(
        store = store,
        memory = memory,
        buffer = buffer,
        memoryPointer = memoryPointer,
        bytesToRead = bytesToRead,
        bufferPointer = bufferPointer,
        bytesReader = ::BytesReader,
    )
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun readBytes(
    store: Store,
    memory: Memory,
    buffer: ByteArray,
    memoryPointer: Int,
    bytesToRead: Int,
    bufferPointer: Int,
    bytesReader: BytesReader,
): Result<ByteArray, ModuleTrapError> = binding {
    val instance = store.store.memory(memory.reference.address).bind()
    bytesReader(instance.data, buffer, memoryPointer, bytesToRead, bufferPointer).bind()
}
