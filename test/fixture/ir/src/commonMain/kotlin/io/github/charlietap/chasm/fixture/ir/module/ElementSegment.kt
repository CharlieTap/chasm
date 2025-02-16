package io.github.charlietap.chasm.fixture.ir.module

import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.ElementSegment
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.ir.type.AbstractHeapType
import io.github.charlietap.chasm.ir.type.ReferenceType

fun elementSegment(
    idx: Index.ElementIndex = elementIndex(),
    type: ReferenceType = ReferenceType.RefNull(AbstractHeapType.Func),
    initExpressions: List<Expression> = emptyList(),
    mode: ElementSegment.Mode = elementSegmentMode(),
) = ElementSegment(
    idx = idx,
    type = type,
    initExpressions = initExpressions,
    mode = mode,
)

fun elementSegmentMode(): ElementSegment.Mode = passiveElementSegmentMode()

fun activeElementSegmentMode(
    tableIndex: Index.TableIndex = tableIndex(),
    offsetExpr: Expression = expression(),
): ElementSegment.Mode.Active = ElementSegment.Mode.Active(
    tableIndex = tableIndex,
    offsetExpr = offsetExpr,
)

fun declarativeElementSegmentMode() = ElementSegment.Mode.Declarative

fun passiveElementSegmentMode() = ElementSegment.Mode.Passive
