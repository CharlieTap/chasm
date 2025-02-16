package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index.GlobalIndex
import io.github.charlietap.chasm.ast.type.GlobalType
import io.github.charlietap.chasm.ir.instruction.Expression as IRExpression
import io.github.charlietap.chasm.ir.module.Global as IRGlobal
import io.github.charlietap.chasm.ir.module.Index.GlobalIndex as IRGlobalIndex
import io.github.charlietap.chasm.ir.type.GlobalType as IRGlobalType

internal fun GlobalFactory(
    global: Global,
): IRGlobal = GlobalFactory(
    global = global,
    globalIndexFactory = ::GlobalIndexFactory,
    globalTypeFactory = ::GlobalTypeFactory,
    expressionFactory = ::ExpressionFactory,
)

internal inline fun GlobalFactory(
    global: Global,
    globalIndexFactory: IRFactory<GlobalIndex, IRGlobalIndex>,
    globalTypeFactory: IRFactory<GlobalType, IRGlobalType>,
    expressionFactory: IRFactory<Expression, IRExpression>,
): IRGlobal {
    return IRGlobal(
        idx = globalIndexFactory(global.idx),
        type = globalTypeFactory(global.type),
        initExpression = expressionFactory(global.initExpression),
    )
}
