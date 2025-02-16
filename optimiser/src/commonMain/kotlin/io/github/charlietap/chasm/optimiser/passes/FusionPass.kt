package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.optimiser.passes.fusion.ExpressionFuser

internal fun FusionPass(
    module: Module,
): Module =
    FusionPass(
        module = module,
        expressionFuser = ::ExpressionFuser,
    )

internal inline fun FusionPass(
    module: Module,
    expressionFuser: ExpressionFuser,
): Module = module.copy(
    functions = module.functions.map { function ->
        function.copy(
            body = expressionFuser(function.body),
        )
    },
)
