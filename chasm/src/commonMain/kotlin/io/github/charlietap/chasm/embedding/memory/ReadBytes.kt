package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceBytesReader
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceBytesReaderImpl
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

fun readBytes(
    store: Store,
    address: Address.Memory,
    pointer: Int,
    numberOfBytes: Int,
): ChasmResult<ByteArray, ChasmError.ExecutionError> =
    readBytes(
        store = store,
        address = address,
        pointer = pointer,
        numberOfBytes = numberOfBytes,
        bytesReader = ::MemoryInstanceBytesReaderImpl,
    )
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun readBytes(
    store: Store,
    address: Address.Memory,
    pointer: Int,
    numberOfBytes: Int,
    bytesReader: MemoryInstanceBytesReader,
): Result<ByteArray, ModuleTrapError> = binding {
    val instance = store.memory(address).bind()
    bytesReader(instance, pointer, numberOfBytes).bind()
}
