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
import io.github.charlietap.chasm.executor.memory.write.StringWriter
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.ext.memory

fun writeUtf8String(
    store: Store,
    memory: Memory,
    pointer: Int,
    string: String,
): ChasmResult<Unit, ChasmError.ExecutionError> =
    writeUtf8String(
        store = store,
        memory = memory,
        pointer = pointer,
        string = string,
        stringWriter = ::StringWriter,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun writeUtf8String(
    store: Store,
    memory: Memory,
    pointer: Int,
    string: String,
    stringWriter: StringWriter,
): Result<Unit, ModuleTrapError> = runCatching {
    val instance = store.store.memory(memory.reference.address)
    stringWriter(instance.data, pointer, string)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> InvocationError.MemoryOperationOutOfBounds
    }
}
