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
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

fun readByte(
    store: Store,
    address: Address.Memory,
    pointer: Int,
): ChasmResult<Byte, ChasmError.ExecutionError> =
    readByte(
        store = store,
        address = address,
        pointer = pointer,
        byteReader = ::MemoryInstanceByteReaderImpl,
    )
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun readByte(
    store: Store,
    address: Address.Memory,
    pointer: Int,
    byteReader: MemoryInstanceByteReader,
): Result<Byte, ModuleTrapError> = binding {
    val instance = store.memory(address).bind()
    byteReader(instance, pointer).bind()
}
