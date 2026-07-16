package io.github.charlietap.chasm.executor.instantiator.component

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.runtime.component.error.ComponentPreparationError
import io.github.charlietap.chasm.runtime.component.error.UnsupportedComponentFeature

internal fun unsupported(
    feature: UnsupportedComponentFeature,
): Result<Nothing, ComponentPreparationError> = Err(ComponentPreparationError.UnsupportedFeature(feature))

internal fun invalidPreparation(reason: String): ComponentPreparationError =
    ComponentPreparationError.InvalidPreparedComponent(reason)

internal fun invalidCanonicalOptions(reason: String): ComponentPreparationError =
    ComponentPreparationError.InvalidCanonicalOptions(reason)
