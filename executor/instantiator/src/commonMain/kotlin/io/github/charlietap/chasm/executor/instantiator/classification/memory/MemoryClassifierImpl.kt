package io.github.charlietap.chasm.executor.instantiator.classification.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.runtime.error.ModuleRuntimeError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.type.ExternalType

internal fun MemoryClassifierImpl(
    store: Store,
    value: ExternalValue.Memory,
): Result<ClassifiedExternalValue, ModuleRuntimeError> = binding {
    val address = value.address
    val type = store.memory(address).bind().type

    ClassifiedExternalValue(ExternalType.Memory(type), value)
}
