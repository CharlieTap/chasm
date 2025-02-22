package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.optimiser.passes.fusion.ExpressionFuser
import io.github.charlietap.chasm.optimiser.passes.fusion.PassContext

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
): Module {
    val context = PassContext(module)
    return module.copy(
        functions = module.functions.map { function ->
            function.copy(
                body = expressionFuser(context, function.body),
            )
        },
    )
}
