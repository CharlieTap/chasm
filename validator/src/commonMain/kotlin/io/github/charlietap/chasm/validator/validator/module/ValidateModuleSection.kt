package io.github.charlietap.chasm.validator.validator.module

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.validator.ModuleValidator
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal inline fun <T> validateModuleSection(
    context: ModuleValidationContext,
    entries: List<T>,
    crossinline validator: ModuleValidator<T>,
): Result<Unit, ModuleValidatorError> {
    for (entry in entries) {
        validator(context, entry).getOrElse { error -> return Err(error) }
    }
    return Ok(Unit)
}
