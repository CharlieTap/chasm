package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.optimiser.passes.controlflow.FunctionRewriter

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
