package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import com.github.michaelbull.result.mapError
import com.github.michaelbull.result.runCatching
import io.github.charlietap.chasm.embedding.ext.memory
import io.github.charlietap.chasm.embedding.internal.throwAccessorFailure
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.memory.size.MemoryInstanceSizer
import io.github.charlietap.chasm.memory.size.MemoryInstanceSizerImpl
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.exception.InvocationException

fun sizeMemory(
    store: Store,
    memory: Memory,
): Int {
    return sizeMemory(
        store = store,
        memory = memory,
        sizer = ::MemoryInstanceSizerImpl,
    ).getOrElse(::throwAccessorFailure)
}

internal fun sizeMemory(
    store: Store,
    memory: Memory,
    sizer: MemoryInstanceSizer,
): Result<Int, ModuleTrapError> = runCatching {
    sizer(store.memory(memory))
}.mapError { e ->
    when (e) {
        is InvocationException -> e.error
        else -> throw e
    }
}
