package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun GlobalTypeValidator(
    context: ValidationContext,
    type: GlobalType,
): Result<Unit, ModuleValidatorError> =
    GlobalTypeValidator(
        context = context,
        type = type,
        valueTypeValidator = ::ValueTypeValidator,
    )

internal inline fun GlobalTypeValidator(
    context: ValidationContext,
    type: GlobalType,
    crossinline valueTypeValidator: Validator<ValueType>,
): Result<Unit, ModuleValidatorError> = binding {
    valueTypeValidator(context, type.valueType).bind()
}
