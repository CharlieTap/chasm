package io.github.charlietap.chasm.ir.module

import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.module.Index.ElementIndex
import io.github.charlietap.chasm.ir.module.Index.TableIndex
import io.github.charlietap.chasm.type.ReferenceType

data class ElementSegment(
    val idx: ElementIndex,
    val type: ReferenceType,
    val initExpressions: List<Expression>,
    val mode: Mode,
) {
    sealed interface Mode {
        data object Passive : Mode

        data class Active(
            val tableIndex: TableIndex,
            val offsetExpr: Expression,
        ) : Mode

        data object Declarative : Mode
    }
}
