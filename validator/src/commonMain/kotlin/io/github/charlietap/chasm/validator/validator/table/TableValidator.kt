package io.github.charlietap.chasm.validator.validator.table

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Table
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.TableType
import io.github.charlietap.chasm.validator.Validator
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.context.scope.Scope
import io.github.charlietap.chasm.validator.context.scope.TableScope
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.validator.instruction.ExpressionValidator
import io.github.charlietap.chasm.validator.validator.type.ReferenceTypeValidator
import io.github.charlietap.chasm.validator.validator.type.TableTypeValidator

internal fun TableValidator(
    context: ValidationContext,
    table: Table,
): Result<Unit, ModuleValidatorError> =
    TableValidator(
        context = context,
        table = table,
        scope = ::TableScope,
        expressionValidator = ::ExpressionValidator,
        referenceTypeValidator = ::ReferenceTypeValidator,
        typeValidator = ::TableTypeValidator,
    )

internal inline fun TableValidator(
    context: ValidationContext,
    table: Table,
    crossinline scope: Scope<Table>,
    crossinline expressionValidator: Validator<Expression>,
    crossinline referenceTypeValidator: Validator<ReferenceType>,
    crossinline typeValidator: Validator<TableType>,
): Result<Unit, ModuleValidatorError> = binding {
    typeValidator(context, table.type).bind()

    val scopedContext = scope(context, table).bind()
    expressionValidator(scopedContext, table.initExpression).bind()
    referenceTypeValidator(scopedContext, table.type.referenceType).bind()
}
