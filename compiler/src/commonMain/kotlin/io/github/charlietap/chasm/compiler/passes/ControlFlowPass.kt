package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.compiler.passes.controlflow.FunctionRewriter
import io.github.charlietap.chasm.ir.module.Module

internal fun ControlFlowPass(
    context: PassContext,
    module: Module,
): Module =
    ControlFlowPass(
        context = context,
        module = module,
        functionRewriter = ::FunctionRewriter,
    )

internal inline fun ControlFlowPass(
    context: PassContext,
    module: Module,
    functionRewriter: FunctionRewriter,
): Module {
    return module.copy(
        functions = module.functions.map { function ->
            functionRewriter(context, function)
        },
    )
}
