package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.type.limits.LimitsValidator

internal fun TableTypeValidator(
    context: ValidationContext,
    type: TableType,
): Result<Unit, ModuleValidatorError> =
    TableTypeValidator(
        context = context,
        type = type,
        limitsValidator = ::LimitsValidator,
    )

internal inline fun TableTypeValidator(
    context: ValidationContext,
    type: TableType,
    crossinline limitsValidator: Validator<Limits>,
): Result<Unit, ModuleValidatorError> = binding {
    limitsValidator(context, type.limits).bind()
}
