package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.memory
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.memory.write.StringWriter
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException

fun writeUtf8String(
    store: Store,
    memory: Memory,
    pointer: Int,
    string: String,
) {
    writeUtf8String(
        store = store,
        memory = memory,
        pointer = pointer,
        string = string,
        stringWriter = ::StringWriter,
    ).getOrElse(::throwAccessorFailure)
}

internal fun writeUtf8String(
    store: Store,
    memory: Memory,
    pointer: Int,
    string: String,
    stringWriter: StringWriter,
): Result<Unit, ModuleTrapError> = runCatching {
    val instance = store.memory(memory)
    stringWriter(instance.data, pointer, string)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        is IndexOutOfBoundsException -> InvocationError.MemoryOperationOutOfBounds
        else -> throw e
    }
}
