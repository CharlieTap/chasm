package io.github.charlietap.chasm.optimiser.passes.controlflow

import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.StackAdjustment
import io.github.charlietap.chasm.ir.module.Function
import io.github.charlietap.chasm.type.ext.asFunctionType

internal typealias FunctionRewriter = (ControlFlowContext, Function) -> Function

internal fun FunctionRewriter(
    context: ControlFlowContext,
    function: Function,
): Function =
    FunctionRewriter(
        context = context,
        function = function,
        expressionRewriter = ::ExpressionRewriter,
    )

internal inline fun FunctionRewriter(
    context: ControlFlowContext,
    function: Function,
    expressionRewriter: ExpressionRewriter,
): Function {

    val functionType = context.module.definedTypes[function.typeIndex.idx].asFunctionType()

    context.ip = 0
    context.sp = functionType.params.types.size + function.locals.size
    context.blocks.clear()

    val stackAdjustment = StackAdjustment(0, functionType.results.types.size)

    val block = Block(stackAdjustment)
    context.blocks.addFirst(block)

    val expression = expressionRewriter(context, function.body)

    val usedBlock = context.blocks.removeFirst()
    updateJumpOffsets(0, expression.instructions.size, usedBlock)

    return function.copy(
        body = Expression(
            instructions = buildList {
                addAll(expression.instructions)
                add(AdminInstruction.ReturnFunction(stackAdjustment))
            },
        ),
    )
}
