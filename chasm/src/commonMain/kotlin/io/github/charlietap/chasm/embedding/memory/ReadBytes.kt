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
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceBytesReader
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceBytesReaderImpl
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory

fun readBytes(
    store: Store,
    memory: Memory,
    pointer: Int,
    numberOfBytes: Int,
): ChasmResult<ByteArray, ChasmError.ExecutionError> =
    readBytes(
        store = store,
        memory = memory,
        pointer = pointer,
        numberOfBytes = numberOfBytes,
        bytesReader = ::MemoryInstanceBytesReaderImpl,
    )
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun readBytes(
    store: Store,
    memory: Memory,
    pointer: Int,
    numberOfBytes: Int,
    bytesReader: MemoryInstanceBytesReader,
): Result<ByteArray, ModuleTrapError> = binding {
    val instance = store.store.memory(memory.reference.address).bind()
    bytesReader(instance, pointer, numberOfBytes).bind()
}
