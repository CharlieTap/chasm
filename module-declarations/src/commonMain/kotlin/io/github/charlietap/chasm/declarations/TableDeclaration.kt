package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.type.TableType

data class TableDeclaration(
    val type: TableType,
    val initializer: InitializerExpression,
)
