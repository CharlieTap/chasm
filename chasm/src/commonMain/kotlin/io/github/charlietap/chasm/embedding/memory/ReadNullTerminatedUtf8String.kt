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
import io.github.charlietap.chasm.executor.memory.read.NullTerminatedStringReader
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.ext.memory

fun readNullTerminatedUtf8String(
    store: Store,
    memory: Memory,
    pointer: Int,
): ChasmResult<String, ChasmError.ExecutionError> =
    readNullTerminatedUtf8String(
        store = store,
        memory = memory,
        pointer = pointer,
        stringReader = ::NullTerminatedStringReader,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun readNullTerminatedUtf8String(
    store: Store,
    memory: Memory,
    pointer: Int,
    stringReader: NullTerminatedStringReader,
): Result<String, ModuleTrapError> = runCatching {
    val instance = store.store.memory(memory.reference.address)
    stringReader(instance.data, pointer)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> InvocationError.MemoryOperationOutOfBounds
    }
}
