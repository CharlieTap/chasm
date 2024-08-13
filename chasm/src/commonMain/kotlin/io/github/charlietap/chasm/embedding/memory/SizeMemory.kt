package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.memory.size.MemoryInstanceSizer
import io.github.charlietap.chasm.executor.memory.size.MemoryInstanceSizerImpl
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory

fun sizeMemory(
    store: Store,
    memory: Memory,
): ChasmResult<Int, ChasmError.ExecutionError> =
    sizeMemory(
        store = store,
        memory = memory,
        sizer = ::MemoryInstanceSizerImpl,
    )
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun sizeMemory(
    store: Store,
    memory: Memory,
    sizer: MemoryInstanceSizer,
): Result<Int, ModuleTrapError> = binding {
    val instance = store.store.memory(memory.reference.address).bind()
    sizer(instance).bind()
}
