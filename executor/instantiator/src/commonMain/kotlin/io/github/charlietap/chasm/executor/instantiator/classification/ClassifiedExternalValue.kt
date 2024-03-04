package io.github.charlietap.chasm.executor.instantiator.classification

import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.type.ExternalType

internal data class ClassifiedExternalValue(
    val type: ExternalType,
    val value: ExternalValue,
)
