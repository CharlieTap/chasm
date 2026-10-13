package io.github.charlietap.chasm.validator.validator.module

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.validator.context.ModuleValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.import.ImportValidator
import io.github.charlietap.chasm.validator.validator.type.TypeValidator

/** Validates the types and imports, which must be valid before any function can be validated. */
internal fun ModulePreludeValidator(
    context: ModuleValidationContext,
    module: Module,
): Result<Unit, ModuleValidatorError> {
    validateModuleSection(context, module.types, ::TypeValidator).getOrElse { error ->
        return Err(error)
    }
    return validateModuleSection(context, module.imports, ::ImportValidator)
}
