package io.github.charlietap.chasm.validator.validator.type.limits

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.error.TableValidatorError

internal fun LimitsValidator(
    context: ValidationContext,
    limits: Limits,
): Result<Unit, ModuleValidatorError> = binding {
    if (limits.min > (limits.max ?: UInt.MAX_VALUE)) {
        Err(TableValidatorError.IncorrectLimits).bind<Unit>()
    }
}
