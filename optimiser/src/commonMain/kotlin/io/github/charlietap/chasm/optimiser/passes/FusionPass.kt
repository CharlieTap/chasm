package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.optimiser.passes.fusion.ExpressionFuser

internal fun FusionPass(
    context: PassContextt,
    module: Module,
): Module =
    FusionPass(
        context = context,
        module = module,
        expressionFuser = ::ExpressionFuser,
    )

internal inline fun FusionPass(
    context: PassContextt,
    module: Module,
    expressionFuser: ExpressionFuser,
): Module {
    return module.copy(
        functions = module.functions.map { function ->
            function.copy(
                body = expressionFuser(context, function.body),
            )
        },
    )
}
