package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.memory
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException

fun growMemory(
    store: Store,
    memory: Memory,
    pagesToAdd: Int,
): Int {
    return growMemoryResult(
        store = store,
        memory = memory,
        pagesToAdd = pagesToAdd,
    ).getOrElse(::throwAccessorFailure)
}

internal fun growMemoryResult(
    store: Store,
    memory: Memory,
    pagesToAdd: Int,
): Result<Int, ModuleTrapError> = runCatching {
    store.memory(memory).grow(pagesToAdd)
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> throw e
    }
}
