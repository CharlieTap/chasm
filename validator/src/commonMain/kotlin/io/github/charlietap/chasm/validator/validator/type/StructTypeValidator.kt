package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun StructTypeValidator(
    context: ValidationContext,
    type: StructType,
): Result<Unit, ModuleValidatorError> =
    StructTypeValidator(
        context = context,
        type = type,
        fieldTypeValidator = ::FieldTypeValidator,
    )

internal inline fun StructTypeValidator(
    context: ValidationContext,
    type: StructType,
    crossinline fieldTypeValidator: Validator<FieldType>,
): Result<Unit, ModuleValidatorError> = binding {
    type.fields.forEach { field ->
        fieldTypeValidator(context, field).bind()
    }
}
