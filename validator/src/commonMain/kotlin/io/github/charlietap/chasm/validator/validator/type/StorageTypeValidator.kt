package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.StorageType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun StorageTypeValidator(
    context: ValidationContext,
    type: StorageType,
): Result<Unit, ModuleValidatorError> =
    StorageTypeValidator(
        context = context,
        type = type,
        valueTypeValidator = ::ValueTypeValidator,
    )

internal inline fun StorageTypeValidator(
    context: ValidationContext,
    type: StorageType,
    crossinline valueTypeValidator: Validator<ValueType>,
): Result<Unit, ModuleValidatorError> = binding {
    when (type) {
        is StorageType.Packed -> Unit
        is StorageType.Value -> valueTypeValidator(context, type.type).bind()
    }
}
