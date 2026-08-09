package io.github.charlietap.chasm.compiler.passes.controlflow

import io.github.charlietap.chasm.compiler.passes.PassContext
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.Function

internal typealias FunctionRewriter = (PassContext, Function) -> Function

internal fun FunctionRewriter(
    context: PassContext,
    function: Function,
): Function = FunctionRewriter(function)

internal fun FunctionRewriter(
    function: Function,
): Function {
    return function.copy(
        body = Expression(
            instructions = buildList {
                addAll(function.body.instructions)
                add(AdminInstruction.EndFunction)
            },
        ),
    )
}
