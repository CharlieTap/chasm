package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.type.ReferenceType

data class ElementSegmentDeclaration(
    val type: ReferenceType,
    val initializers: List<InitializerExpression>,
    val mode: Mode,
) {

    sealed interface Mode {

        data object Passive : Mode

        data class Active(
            val tableIndex: Int,
            val offset: InitializerExpression,
        ) : Mode

        data object Declarative : Mode
    }
}
