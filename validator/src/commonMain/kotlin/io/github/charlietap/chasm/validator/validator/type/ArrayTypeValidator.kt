package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.type.ArrayType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ArrayTypeValidator(
    context: ValidationContext,
    type: ArrayType,
): Result<Unit, ModuleValidatorError> =
    ArrayTypeValidator(
        context = context,
        type = type,
        fieldTypeValidator = ::FieldTypeValidator,
    )

internal inline fun ArrayTypeValidator(
    context: ValidationContext,
    type: ArrayType,
    crossinline fieldTypeValidator: Validator<FieldType>,
): Result<Unit, ModuleValidatorError> = binding {
    fieldTypeValidator(context, type.fieldType).bind()
}
