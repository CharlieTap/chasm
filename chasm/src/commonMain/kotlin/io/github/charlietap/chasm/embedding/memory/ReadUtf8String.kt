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
import io.github.charlietap.chasm.executor.memory.read.StringReader
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.memory

fun readUtf8String(
    store: Store,
    memory: Memory,
    pointer: Int,
    stringLengthInBytes: Int,
): ChasmResult<String, ChasmError.ExecutionError> =
    readUtf8String(
        store = store,
        memory = memory,
        pointer = pointer,
        stringLengthInBytes = stringLengthInBytes,
        stringReader = ::StringReader,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun readUtf8String(
    store: Store,
    memory: Memory,
    pointer: Int,
    stringLengthInBytes: Int,
    stringReader: StringReader,
): Result<String, ModuleTrapError> = runCatching {
    val instance = store.store.memory(memory.reference.address)
    stringReader(instance.data, pointer, stringLengthInBytes)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> InvocationError.MemoryOperationOutOfBounds
    }
}
