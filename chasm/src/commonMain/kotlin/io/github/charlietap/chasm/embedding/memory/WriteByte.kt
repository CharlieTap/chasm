package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceByteWriter
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceByteWriterImpl
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

fun writeByte(
    store: Store,
    address: Address.Memory,
    pointer: Int,
    byte: Byte,
): ChasmResult<Unit, ChasmError.ExecutionError> =
    writeByte(
        store = store,
        address = address,
        pointer = pointer,
        byte = byte,
        byteWriter = ::MemoryInstanceByteWriterImpl,
    )
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun writeByte(
    store: Store,
    address: Address.Memory,
    pointer: Int,
    byte: Byte,
    byteWriter: MemoryInstanceByteWriter,
): Result<Unit, ModuleTrapError> = binding {
    val instance = store.memory(address).bind()
    byteWriter(instance, pointer, byte).bind()
}
