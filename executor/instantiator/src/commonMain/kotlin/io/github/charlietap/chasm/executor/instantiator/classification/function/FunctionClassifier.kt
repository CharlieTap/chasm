package io.github.charlietap.chasm.executor.instantiator.classification.function

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.ext.function
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.type.ExternalType

internal typealias FunctionClassifier = (Store, ExternalValue.Function) -> Result<ClassifiedExternalValue, ModuleTrapError>

internal fun FunctionClassifier(
    store: Store,
    value: ExternalValue.Function,
): Result<ClassifiedExternalValue, ModuleTrapError> = binding {
    val address = value.address
    val type = store.function(address).bind().type

    ClassifiedExternalValue(ExternalType.Function(type), value)
}
