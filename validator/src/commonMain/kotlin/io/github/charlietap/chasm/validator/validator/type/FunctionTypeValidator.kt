package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.validator.CoreTypeValidator
import io.github.charlietap.chasm.validator.context.CoreTypeValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun FunctionTypeValidator(
    context: CoreTypeValidationContext,
    type: FunctionType,
): Result<Unit, ModuleValidatorError> =
    FunctionTypeValidator(
        context = context,
        type = type,
        valueTypeValidator = ::ValueTypeValidator,
    )

internal inline fun FunctionTypeValidator(
    context: CoreTypeValidationContext,
    type: FunctionType,
    crossinline valueTypeValidator: CoreTypeValidator<ValueType>,
): Result<Unit, ModuleValidatorError> {
    var index = 0
    while (index < type.params.types.size) {
        valueTypeValidator(context, type.params.types[index]).getOrElse { error ->
            return Err(error)
        }
        index++
    }
    index = 0
    while (index < type.results.types.size) {
        valueTypeValidator(context, type.results.types[index]).getOrElse { error ->
            return Err(error)
        }
        index++
    }
    return Ok(Unit)
}
