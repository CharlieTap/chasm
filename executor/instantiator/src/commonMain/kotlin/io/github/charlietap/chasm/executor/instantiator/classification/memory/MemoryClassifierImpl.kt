package io.github.charlietap.chasm.executor.instantiator.classification.memory

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.type.ExternalType

internal fun MemoryClassifierImpl(
    store: Store,
    value: ExternalValue.Memory,
): Result<ClassifiedExternalValue, InstantiationError.ImportLookupFailed> {
    val address = value.address
    val type = store.memory(address)?.type ?: return Err(
        InstantiationError.ImportLookupFailed(address.address),
    )

    return Ok(ClassifiedExternalValue(ExternalType.Memory(type), value))
}
