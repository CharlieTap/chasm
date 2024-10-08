package io.github.charlietap.chasm.executor.instantiator.classification.global

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.global
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.type.ExternalType

internal typealias GlobalClassifier = (Store, ExternalValue.Global) -> Result<ClassifiedExternalValue, ModuleTrapError>

internal fun GlobalClassifier(
    store: Store,
    value: ExternalValue.Global,
): Result<ClassifiedExternalValue, ModuleTrapError> = binding {
    val address = value.address
    val type = store.global(address).bind().type

    ClassifiedExternalValue(ExternalType.Global(type), value)
}
