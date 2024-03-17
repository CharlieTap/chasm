package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceByteReader
import io.github.charlietap.chasm.executor.memory.read.MemoryInstanceByteReaderImpl
import io.github.charlietap.chasm.executor.runtime.error.ModuleRuntimeError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

fun readMemory(
    store: Store,
    address: Address.Memory,
    byteOffsetInMemory: Int,
): ChasmResult<Byte, ChasmError.ExecutionError> =
    readMemory(
        store = store,
        address = address,
        byteOffsetInMemory = byteOffsetInMemory,
        byteReader = ::MemoryInstanceByteReaderImpl,
    )
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun readMemory(
    store: Store,
    address: Address.Memory,
    byteOffsetInMemory: Int,
    byteReader: MemoryInstanceByteReader,
): Result<Byte, ModuleRuntimeError> = binding {
    val instance = store.memory(address).bind()
    byteReader(instance, byteOffsetInMemory).bind()
}
