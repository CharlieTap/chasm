package io.github.charlietap.chasm.validator.validator.export

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ExportValidatorError
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.index.FunctionIndexValidator

internal fun FunctionExportValidator(
    context: ValidationContext,
    descriptor: Export.Descriptor.Function,
): Result<Unit, ModuleValidatorError> =
    FunctionExportValidator(
        context = context,
        descriptor = descriptor,
        functionIndexValidator = ::FunctionIndexValidator,
    )

internal inline fun FunctionExportValidator(
    context: ValidationContext,
    descriptor: Export.Descriptor.Function,
    crossinline functionIndexValidator: Validator<Index.FunctionIndex>,
): Result<Unit, ModuleValidatorError> = binding {
    functionIndexValidator(context, descriptor.functionIndex)
        .mapError {
            ExportValidatorError.UnknownFunction
        }.bind()
}
