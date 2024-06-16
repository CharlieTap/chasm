package io.github.charlietap.chasm.validator.validator.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.type.TableTypeValidator

internal fun TableValidator(
    context: ValidationContext,
    table: Table,
): Result<Unit, ModuleValidatorError> =
    TableValidator(
        context = context,
        table = table,
        typeValidator = ::TableTypeValidator,
    )

internal fun TableValidator(
    context: ValidationContext,
    table: Table,
    typeValidator: Validator<TableType>,
): Result<Unit, ModuleValidatorError> = binding {
    typeValidator(context, table.type).bind()
}
