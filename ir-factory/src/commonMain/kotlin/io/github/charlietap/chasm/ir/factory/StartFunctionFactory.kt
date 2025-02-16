package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.module.Index.FunctionIndex
import io.github.charlietap.chasm.ast.module.StartFunction
import io.github.charlietap.chasm.ir.module.Index.FunctionIndex as IRFunctionIndex
import io.github.charlietap.chasm.ir.module.StartFunction as IRStartFunction

internal fun StartFunctionFactory(
    startFunction: StartFunction,
): IRStartFunction = StartFunctionFactory(
    startFunction = startFunction,
    functionIndexFactory = ::FunctionIndexFactory,
)

internal inline fun StartFunctionFactory(
    startFunction: StartFunction,
    functionIndexFactory: IRFactory<FunctionIndex, IRFunctionIndex>,
): IRStartFunction {
    return IRStartFunction(
        idx = functionIndexFactory(startFunction.idx),
    )
}
