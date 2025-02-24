package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun FunctionTypeValidator(
    context: ValidationContext,
    type: FunctionType,
): Result<Unit, ModuleValidatorError> =
    FunctionTypeValidator(
        context = context,
        type = type,
        valueTypeValidator = ::ValueTypeValidator,
    )

internal inline fun FunctionTypeValidator(
    context: ValidationContext,
    type: FunctionType,
    crossinline valueTypeValidator: Validator<ValueType>,
): Result<Unit, ModuleValidatorError> = binding {
    type.params.types.forEach { param ->
        valueTypeValidator(context, param).bind()
    }
    type.results.types.forEach { result ->
        valueTypeValidator(context, result).bind()
    }
}
