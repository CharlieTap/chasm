package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.type.limits.LimitsValidator

internal fun MemoryTypeValidator(
    context: ValidationContext,
    type: MemoryType,
): Result<Unit, ModuleValidatorError> =
    MemoryTypeValidator(
        context = context,
        type = type,
        limitsValidator = ::LimitsValidator,
    )

internal inline fun MemoryTypeValidator(
    context: ValidationContext,
    type: MemoryType,
    crossinline limitsValidator: Validator<Limits>,
): Result<Unit, ModuleValidatorError> = binding {
    limitsValidator(context, type.limits).bind()
}
