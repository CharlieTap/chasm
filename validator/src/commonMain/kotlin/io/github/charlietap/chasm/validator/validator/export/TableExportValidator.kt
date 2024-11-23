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
import io.github.charlietap.chasm.validator.validator.index.TableIndexValidator

internal fun TableExportValidator(
    context: ValidationContext,
    descriptor: Export.Descriptor.Table,
): Result<Unit, ModuleValidatorError> =
    TableExportValidator(
        context = context,
        descriptor = descriptor,
        tableIndexValidator = ::TableIndexValidator,
    )

internal inline fun TableExportValidator(
    context: ValidationContext,
    descriptor: Export.Descriptor.Table,
    crossinline tableIndexValidator: Validator<Index.TableIndex>,
): Result<Unit, ModuleValidatorError> = binding {
    tableIndexValidator(context, descriptor.tableIndex).mapError {
        ExportValidatorError.UnknownTable
    }.bind()
}
