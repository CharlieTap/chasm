package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Global
import io.github.charlietap.chasm.ast.module.Index.GlobalIndex
import io.github.charlietap.chasm.type.GlobalType
import io.github.charlietap.chasm.type.rolling.substitution.GlobalTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.Substitution
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor
import io.github.charlietap.chasm.ir.instruction.Expression as IRExpression
import io.github.charlietap.chasm.ir.module.Global as IRGlobal
import io.github.charlietap.chasm.ir.module.Index.GlobalIndex as IRGlobalIndex

internal typealias GlobalFactory = (Global, Substitution) -> IRGlobal

internal fun GlobalFactory(
    global: Global,
    substitution: Substitution,
): IRGlobal = GlobalFactory(
    global = global,
    substitution = substitution,
    globalIndexFactory = ::GlobalIndexFactory,
    expressionFactory = ::ExpressionFactory,
    globalTypeSubstitutor = ::GlobalTypeSubstitutor,
)

internal inline fun GlobalFactory(
    global: Global,
    substitution: Substitution,
    globalIndexFactory: IRFactory<GlobalIndex, IRGlobalIndex>,
    expressionFactory: IRFactory<Expression, IRExpression>,
    globalTypeSubstitutor: TypeSubstitutor<GlobalType>,
): IRGlobal {
    return IRGlobal(
        idx = globalIndexFactory(global.idx),
        type = globalTypeSubstitutor(global.type, substitution),
        initExpression = expressionFactory(global.initExpression),
    )
}
