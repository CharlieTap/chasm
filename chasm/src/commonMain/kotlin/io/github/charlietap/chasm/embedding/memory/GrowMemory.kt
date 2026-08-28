package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.instance.MemoryInstance
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.MAX_PAGES

fun growMemory(
    store: Store,
    memory: Memory,
    pagesToAdd: Int,
): ChasmResult<Int, ChasmError.ExecutionError> =
    growMemoryResult(
        store = store,
        memory = memory,
        pagesToAdd = pagesToAdd,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun growMemoryResult(
    store: Store,
    memory: Memory,
    pagesToAdd: Int,
): Result<Int, ModuleTrapError> {

    val instance = store.store.memory(memory.reference.address)
    return Ok(growMemoryInstance(instance, pagesToAdd))
}

internal inline fun growMemoryInstance(
    instance: MemoryInstance,
    pagesToAdd: Int,
): Int {
    val current = instance.type.limits.min.toInt()

    if (pagesToAdd == 0) {
        return current
    }

    val new = current + pagesToAdd
    val max = instance.type.limits.max?.toInt() ?: MAX_PAGES

    if (pagesToAdd < 0 || new < current || new > max) {
        return -1
    }

    val grown = instance.data.grow(pagesToAdd)

    instance.data = grown
    instance.type.limits.min = new.toULong()
    instance.refresh()

    return current
}
