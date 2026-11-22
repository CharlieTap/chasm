package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.memory
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.memory.write.BytesWriter
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException

fun writeByte(
    store: Store,
    memory: Memory,
    pointer: Int,
    byte: Byte,
) {
    writeByte(
        store = store,
        memory = memory,
        pointer = pointer,
        byte = byte,
        bytesWriter = ::BytesWriter,
    ).getOrElse(::throwAccessorFailure)
}

internal fun writeByte(
    store: Store,
    memory: Memory,
    pointer: Int,
    byte: Byte,
    bytesWriter: BytesWriter,
): Result<Unit, ModuleTrapError> = runCatching {
    val instance = store.memory(memory)
    bytesWriter(instance.data, instance.size, byteArrayOf(byte), pointer, 1, 0)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        is IndexOutOfBoundsException -> InvocationError.MemoryOperationOutOfBounds
        else -> throw e
    }
}
