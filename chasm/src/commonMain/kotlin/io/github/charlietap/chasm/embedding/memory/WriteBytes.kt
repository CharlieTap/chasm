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
import io.github.charlietap.chasm.executor.memory.write.MemoryInstanceBytesWriter
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory

fun writeBytes(
    store: Store,
    memory: Memory,
    pointer: Int,
    bytes: ByteArray,
): ChasmResult<Unit, ChasmError.ExecutionError> =
    writeBytes(
        store = store,
        memory = memory,
        pointer = pointer,
        bytes = bytes,
        bytesWriter = ::MemoryInstanceBytesWriter,
    )
        .mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun writeBytes(
    store: Store,
    memory: Memory,
    pointer: Int,
    bytes: ByteArray,
    bytesWriter: MemoryInstanceBytesWriter,
): Result<Unit, ModuleTrapError> = binding {
    val instance = store.store.memory(memory.reference.address).bind()
    bytesWriter(instance, pointer, bytes).bind()
}
