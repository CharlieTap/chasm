package io.github.charlietap.chasm.validator.error

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse

internal class ModuleValidationException(
    val error: ModuleValidatorError,
) : Exception()

internal inline fun <V> Result<V, ModuleValidatorError>.getOrThrowValidation(): V =
    getOrElse { error -> throw ModuleValidationException(error) }
