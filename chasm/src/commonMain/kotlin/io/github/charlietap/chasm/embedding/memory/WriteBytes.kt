package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceBytesWriter
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceBytesWriterImpl
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

fun writeBytes(
    store: Store,
    address: Address.Memory,
    pointer: Int,
    bytes: ByteArray,
): ChasmResult<Unit, ChasmError.ExecutionError> =
    writeBytes(
        store = store,
        address = address,
        pointer = pointer,
        bytes = bytes,
        bytesWriter = ::MemoryInstanceBytesWriterImpl,
    )
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun writeBytes(
    store: Store,
    address: Address.Memory,
    pointer: Int,
    bytes: ByteArray,
    bytesWriter: MemoryInstanceBytesWriter,
): Result<Unit, ModuleTrapError> = binding {
    val instance = store.memory(address).bind()
    bytesWriter(instance, pointer, bytes).bind()
}
