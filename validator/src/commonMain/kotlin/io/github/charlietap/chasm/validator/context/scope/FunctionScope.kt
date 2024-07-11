package io.github.charlietap.chasm.validator.context.scope

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.stack.stackOf
import io.github.charlietap.chasm.validator.context.FunctionContextImpl
import io.github.charlietap.chasm.validator.context.Label
import io.github.charlietap.chasm.validator.context.ValidationContext
import io.github.charlietap.chasm.validator.error.ModuleValidatorError
import io.github.charlietap.chasm.validator.ext.functionType

internal fun FunctionScope(
    context: ValidationContext,
    function: Function,
): Result<ValidationContext, ModuleValidatorError> = binding {

    val functionType = context.functionType(function.typeIndex).bind()
    val label = Label(
        instruction = null,
        inputs = functionType.params,
        outputs = functionType.results,
        operandsDepth = context.operands.depth(),
        unreachable = false,
    )

    context.copy(
        functionContext = FunctionContextImpl(
            labels = stackOf(label),
            locals = (functionType.params.types + function.locals.map(Local::type)).toMutableList(),
            result = functionType.results,
        ),
    )
}
