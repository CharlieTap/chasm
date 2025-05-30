package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.memory.write.BytesWriter
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.memory

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
        bytesWriter = ::BytesWriter,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun writeByte(
    store: Store,
    memory: Memory,
    pointer: Int,
    byte: Byte,
    bytesWriter: BytesWriter,
): Result<Unit, ModuleTrapError> = runCatching {
    val instance = store.store.memory(memory.reference.address)
    bytesWriter(instance.data, instance.size, byteArrayOf(byte), pointer, 1, 0)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> InvocationError.MemoryOperationOutOfBounds
    }
}
