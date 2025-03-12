package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.optimiser.passes.controlflow.FunctionRewriter

internal fun ControlFlowPass(
    module: Module,
): Module =
    ControlFlowPass(
        module = module,
        functionRewriter = ::FunctionRewriter,
    )

internal inline fun ControlFlowPass(
    module: Module,
    functionRewriter: FunctionRewriter,
): Module {
    val context = PassContext(module)
    return module.copy(
        functions = module.functions.map { function ->
            functionRewriter(context, function)
        },
    )
}
