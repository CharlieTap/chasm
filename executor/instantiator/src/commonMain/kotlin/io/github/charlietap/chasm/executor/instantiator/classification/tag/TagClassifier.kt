package io.github.charlietap.chasm.executor.instantiator.classification.tag

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.tag
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.type.ExternalType

internal typealias TagClassifier = (Store, ExternalValue.Tag) -> Result<ClassifiedExternalValue, ModuleTrapError>

internal fun TagClassifier(
    store: Store,
    value: ExternalValue.Tag,
): Result<ClassifiedExternalValue, ModuleTrapError> = binding {
    val address = value.address
    val type = store.tag(address).bind().type

    ClassifiedExternalValue(ExternalType.Tag(type), value)
}
