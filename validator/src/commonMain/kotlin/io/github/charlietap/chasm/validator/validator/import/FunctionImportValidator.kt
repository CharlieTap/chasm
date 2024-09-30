package io.github.charlietap.chasm.validator.validator.import

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Import
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun FunctionImportValidator(
    context: ValidationContext,
    descriptor: Import.Descriptor.Function,
): Result<Unit, ModuleValidatorError> = binding {
}
