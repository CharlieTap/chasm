package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Index.ElementIndex
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.rolling.substitution.ReferenceTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.Substitution
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor
import io.github.charlietap.chasm.ir.instruction.Expression as IRExpression
import io.github.charlietap.chasm.ir.module.ElementSegment as IRElementSegment
import io.github.charlietap.chasm.ir.module.Index.ElementIndex as IRElementIndex

internal typealias ElementSegmentFactory = (ElementSegment, Substitution) -> IRElementSegment

internal fun ElementSegmentFactory(
    elementSegment: ElementSegment,
    substitution: Substitution,
): IRElementSegment = ElementSegmentFactory(
    elementSegment = elementSegment,
    substitution = substitution,
    elementIndexFactory = ::ElementIndexFactory,
    expressionFactory = ::ExpressionFactory,
    modeFactory = ::ElementSegmentModeFactory,
    referenceTypeSubstitutor = ::ReferenceTypeSubstitutor,
)

internal inline fun ElementSegmentFactory(
    elementSegment: ElementSegment,
    substitution: Substitution,
    elementIndexFactory: IRFactory<ElementIndex, IRElementIndex>,
    expressionFactory: IRFactory<Expression, IRExpression>,
    modeFactory: IRFactory<ElementSegment.Mode, IRElementSegment.Mode>,
    referenceTypeSubstitutor: TypeSubstitutor<ReferenceType>,
): IRElementSegment {
    return IRElementSegment(
        idx = elementIndexFactory(elementSegment.idx),
        type = referenceTypeSubstitutor(elementSegment.type, substitution),
        initExpressions = elementSegment.initExpressions.map(expressionFactory),
        mode = modeFactory(elementSegment.mode),
    )
}

internal fun ElementSegmentModeFactory(
    mode: ElementSegment.Mode,
): IRElementSegment.Mode {
    return when (mode) {
        is ElementSegment.Mode.Active -> IRElementSegment.Mode.Active(
            tableIndex = TableIndexFactory(mode.tableIndex),
            offsetExpr = ExpressionFactory(mode.offsetExpr),
        )
        ElementSegment.Mode.Passive -> IRElementSegment.Mode.Passive
        ElementSegment.Mode.Declarative -> IRElementSegment.Mode.Declarative
    }
}
