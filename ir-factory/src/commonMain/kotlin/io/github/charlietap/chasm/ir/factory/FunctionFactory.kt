package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Function
import io.github.charlietap.chasm.ast.module.Index.FunctionIndex
import io.github.charlietap.chasm.ast.module.Index.TypeIndex
import io.github.charlietap.chasm.ast.module.Local
import io.github.charlietap.chasm.ir.instruction.Expression as IRExpression
import io.github.charlietap.chasm.ir.module.Function as IRFunction
import io.github.charlietap.chasm.ir.module.Index.FunctionIndex as IRFunctionIndex
import io.github.charlietap.chasm.ir.module.Index.TypeIndex as IRTypeIndex
import io.github.charlietap.chasm.ir.module.Local as IRLocal

internal fun FunctionFactory(
    function: Function,
): IRFunction = FunctionFactory(
    function = function,
    functionIndexFactory = ::FunctionIndexFactory,
    typeIndexFactory = ::TypeIndexFactory,
    localFactory = ::LocalFactory,
    expressionFactory = ::ExpressionFactory,
)

internal inline fun FunctionFactory(
    function: Function,
    functionIndexFactory: IRFactory<FunctionIndex, IRFunctionIndex>,
    typeIndexFactory: IRFactory<TypeIndex, IRTypeIndex>,
    localFactory: IRFactory<Local, IRLocal>,
    expressionFactory: IRFactory<Expression, IRExpression>,
): IRFunction {
    return IRFunction(
        idx = functionIndexFactory(function.idx),
        typeIndex = typeIndexFactory(function.typeIndex),
        locals = function.locals.map(localFactory),
        body = expressionFactory(function.body),
    )
}
