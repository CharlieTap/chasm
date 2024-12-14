package io.github.charlietap.chasm.fixture.ast.module

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.fixture.ast.instruction.expression

fun elementSegment(
    idx: Index.ElementIndex = Index.ElementIndex(0u),
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
