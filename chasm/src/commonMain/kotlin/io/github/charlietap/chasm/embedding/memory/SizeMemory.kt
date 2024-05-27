package io.github.charlietap.chasm.embedding.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.ChasmResult.Error
import io.github.charlietap.chasm.ChasmResult.Success
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.memory.size.MemoryInstanceSizer
import io.github.charlietap.chasm.executor.memory.size.MemoryInstanceSizerImpl
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.store.Store

fun sizeMemory(
    store: Store,
    address: Address.Memory,
): ChasmResult<Int, ChasmError.ExecutionError> =
    sizeMemory(
        store = store,
        address = address,
        sizer = ::MemoryInstanceSizerImpl,
    )
        .mapError(ChasmError::ExecutionError)
        .fold(::Success, ::Error)

internal fun sizeMemory(
    store: Store,
    address: Address.Memory,
    sizer: MemoryInstanceSizer,
): Result<Int, ModuleTrapError> = binding {
    val instance = store.memory(address).bind()
    sizer(instance).bind()
}
