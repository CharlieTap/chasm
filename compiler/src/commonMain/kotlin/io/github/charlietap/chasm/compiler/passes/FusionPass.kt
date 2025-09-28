package io.github.charlietap.chasm.compiler.passes

import io.github.charlietap.chasm.compiler.passes.fusion.ExpressionFuser
import io.github.charlietap.chasm.ir.module.Module

internal fun FusionPass(
    context: PassContext,
    module: Module,
): Module =
    FusionPass(
        context = context,
        module = module,
        expressionFuser = ::ExpressionFuser,
    )

internal inline fun FusionPass(
    context: PassContext,
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
