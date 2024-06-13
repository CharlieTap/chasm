package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.stack.stackOf
import io.github.charlietap.chasm.validator.context.FunctionContextImpl
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError

internal fun ExpressionScope(
    context: ValidationContext,
    expression: Expression,
): Result<ValidationContext, ModuleValidatorError> = binding {
    context.copy(
        functionContext = FunctionContextImpl(
            labels = stackOf(),
            locals = mutableListOf(),
            result = null,
            operands = stackOf(),
        ),
    )
}
