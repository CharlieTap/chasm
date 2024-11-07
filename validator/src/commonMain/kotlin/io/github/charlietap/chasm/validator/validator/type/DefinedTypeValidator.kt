package io.github.charlietap.chasm.validator.validator.type

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun DefinedTypeValidator(
    context: ValidationContext,
    type: DefinedType,
): Result<Unit, ModuleValidatorError> = binding {
}
