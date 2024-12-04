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
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceByteWriter
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory

fun writeByte(
    store: Store,
    memory: Memory,
    pointer: Int,
    byte: Byte,
): ChasmResult<Unit, ChasmError.ExecutionError> =
    writeByte(
        store = store,
        memory = memory,
        pointer = pointer,
        byte = byte,
        byteWriter = ::MemoryInstanceByteWriter,
    )
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun writeByte(
    store: Store,
    memory: Memory,
    pointer: Int,
    byte: Byte,
    byteWriter: MemoryInstanceByteWriter,
): Result<Unit, ModuleTrapError> = binding {
    val instance = store.store.memory(memory.reference.address).bind()
    byteWriter(instance, pointer, byte).bind()
}
