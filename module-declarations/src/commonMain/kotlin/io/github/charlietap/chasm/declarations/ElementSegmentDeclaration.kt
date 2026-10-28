package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.type.ReferenceType

data class ElementSegmentDeclaration(
    val type: ReferenceType,
    val initExpressions: List<ConstantExpression>,
    val mode: Mode,
) {

    sealed interface Mode {

        data object Passive : Mode

        data class Active(
            val tableIndex: Index.TableIndex,
            val offsetExpr: ConstantExpression,
        ) : Mode

        data object Declarative : Mode
    }
}
