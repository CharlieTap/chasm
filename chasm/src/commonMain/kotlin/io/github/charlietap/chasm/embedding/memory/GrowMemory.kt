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
import io.github.charlietap.chasm.memory.grow.LinearMemoryGrower
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.memory.LinearMemory.Companion.MAX_PAGES

fun growMemory(
    store: Store,
    memory: Memory,
    pagesToAdd: Int,
): ChasmResult<Int, ChasmError.ExecutionError> =
    growMemory(
        store = store,
        memory = memory,
        pagesToAdd = pagesToAdd,
        grower = ::LinearMemoryGrower,
    ).mapError(ModuleTrapError::toString)
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun growMemory(
    store: Store,
    memory: Memory,
    pagesToAdd: Int,
    grower: LinearMemoryGrower,
): Result<Int, ModuleTrapError> {

    val instance = store.store.memory(memory.reference.address)
    val current = instance.type.limits.min.toInt()

    if (pagesToAdd == 0) {
        return Ok(current)
    }

    val new = current + pagesToAdd
    val max = instance.type.limits.max?.toInt() ?: MAX_PAGES

    if (pagesToAdd < 0 || new < current || new > max) {
        return Ok(-1)
    }

    val grown = grower(instance.data, pagesToAdd)

    instance.data = grown
    instance.type.limits.min = new.toULong()
    instance.refresh()

    return Ok(current)
}
