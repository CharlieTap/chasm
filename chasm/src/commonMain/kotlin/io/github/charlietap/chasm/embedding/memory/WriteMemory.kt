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
import io.github.charlietap.chasm.executor.runtime.error.ModuleRuntimeError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

fun writeMemory(
    store: Store,
    address: Address.Memory,
    byteOffsetInMemory: Int,
    value: Byte,
): ChasmResult<Unit, ChasmError.ExecutionError> =
    writeMemory(
        store = store,
        address = address,
        byteOffsetInMemory = byteOffsetInMemory,
        value = value,
        byteWriter = ::MemoryInstanceByteWriterImpl,
    )
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun writeMemory(
    store: Store,
    address: Address.Memory,
    byteOffsetInMemory: Int,
    value: Byte,
    byteWriter: MemoryInstanceByteWriter,
): Result<Unit, ModuleRuntimeError> = binding {
    val instance = store.memory(address).bind()
    byteWriter(instance, value, byteOffsetInMemory).bind()
}
