package io.github.charlietap.chasm.executor.instantiator.classification.memory

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.memory
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.type.ExternalType

internal typealias MemoryClassifier = (Store, ExternalValue.Memory) -> Result<ClassifiedExternalValue, ModuleTrapError>

internal fun MemoryClassifier(
    store: Store,
    value: ExternalValue.Memory,
): Result<ClassifiedExternalValue, ModuleTrapError> = binding {
    val address = value.address
    val type = store.memory(address).bind().type

    ClassifiedExternalValue(ExternalType.Memory(type), value)
}
