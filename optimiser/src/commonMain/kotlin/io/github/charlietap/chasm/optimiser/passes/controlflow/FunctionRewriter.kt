package io.github.charlietap.chasm.optimiser.passes.controlflow

import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.optimiser.passes.PassContext

internal typealias FunctionRewriter = (PassContext, Function) -> Function

internal fun FunctionRewriter(
    context: PassContext,
    function: Function,
): Function =
    FunctionRewriter(
        context = context,
        function = function,
        expressionRewriter = ::ExpressionRewriter,
    )

internal inline fun FunctionRewriter(
    context: PassContext,
    function: Function,
    expressionRewriter: ExpressionRewriter,
): Function {
    val expression = expressionRewriter(context, function.body)
    return function.copy(
        body = Expression(
            instructions = buildList {
                addAll(expression.instructions)
                add(AdminInstruction.EndBlock)
                add(AdminInstruction.EndFunction)
            },
        ),
    )
}
