package io.github.charlietap.chasm.fixture.module

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index
import io.github.charlietap.chasm.ast.module.ElementSegment
import io.github.charlietap.chasm.ast.type.ReferenceType

fun elementSegment(
    idx: Index.ElementIndex = Index.ElementIndex(0u),
    type: ReferenceType = ReferenceType.Funcref,
    initExpressions: List<Expression> = emptyList(),
    mode: ElementSegment.Mode = ElementSegment.Mode.Passive,
) = ElementSegment(
    idx = idx,
    type = type,
    initExpressions = initExpressions,
    mode = mode,
)
