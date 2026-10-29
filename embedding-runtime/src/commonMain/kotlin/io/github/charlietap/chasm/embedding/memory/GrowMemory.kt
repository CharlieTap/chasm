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
    return Ok(instance.grow(pagesToAdd))
}
