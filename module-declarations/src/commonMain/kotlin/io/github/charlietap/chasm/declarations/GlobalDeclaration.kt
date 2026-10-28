package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.type.GlobalType

data class GlobalDeclaration(
    val type: GlobalType,
    val initializer: InitializerExpression,
)
