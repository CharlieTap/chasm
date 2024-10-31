package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.RecursiveType
import io.github.charlietap.chasm.ast.type.SubType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun RecursiveTypeValidator(
    context: ValidationContext,
    type: RecursiveType,
): Result<Unit, ModuleValidatorError> =
    RecursiveTypeValidator(
        context = context,
        type = type,
        subTypeValidator = ::SubTypeValidator,
    )

internal inline fun RecursiveTypeValidator(
    context: ValidationContext,
    type: RecursiveType,
    crossinline subTypeValidator: Validator<SubType>,
): Result<Unit, ModuleValidatorError> = binding {
    type.subTypes.forEach { subType ->
        subTypeValidator(context, subType).bind()
    }
}
