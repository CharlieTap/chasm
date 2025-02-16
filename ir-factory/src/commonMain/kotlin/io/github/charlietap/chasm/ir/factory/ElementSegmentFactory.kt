package io.github.charlietap.chasm.ir.factory

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Index.ElementIndex
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ir.instruction.Expression as IRExpression
import io.github.charlietap.chasm.ir.module.ElementSegment as IRElementSegment
import io.github.charlietap.chasm.ir.module.Index.ElementIndex as IRElementIndex
import io.github.charlietap.chasm.ir.type.ReferenceType as IRReferenceType

internal fun ElementSegmentFactory(
    elementSegment: ElementSegment,
): IRElementSegment = ElementSegmentFactory(
    elementSegment = elementSegment,
    elementIndexFactory = ::ElementIndexFactory,
    referenceTypeFactory = ::ReferenceTypeFactory,
    expressionFactory = ::ExpressionFactory,
    modeFactory = ::ElementSegmentModeFactory,
)

internal inline fun ElementSegmentFactory(
    elementSegment: ElementSegment,
    elementIndexFactory: IRFactory<ElementIndex, IRElementIndex>,
    referenceTypeFactory: IRFactory<ReferenceType, IRReferenceType>,
    expressionFactory: IRFactory<Expression, IRExpression>,
    modeFactory: IRFactory<ElementSegment.Mode, IRElementSegment.Mode>,
): IRElementSegment {
    return IRElementSegment(
        idx = elementIndexFactory(elementSegment.idx),
        type = referenceTypeFactory(elementSegment.type),
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
