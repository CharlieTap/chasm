package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun FieldTypeValidator(
    context: ValidationContext,
    type: FieldType,
): Result<Unit, ModuleValidatorError> =
    FieldTypeValidator(
        context = context,
        type = type,
        storageTypeValidator = ::StorageTypeValidator,
    )

internal inline fun FieldTypeValidator(
    context: ValidationContext,
    type: FieldType,
    crossinline storageTypeValidator: Validator<StorageType>,
): Result<Unit, ModuleValidatorError> = binding {
    storageTypeValidator(context, type.storageType).bind()
}
