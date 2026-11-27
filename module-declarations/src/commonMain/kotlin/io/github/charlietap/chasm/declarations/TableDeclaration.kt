package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.type.TableType

@InternalChasmApi
data class TableDeclaration(
    val type: TableType,
    val initExpression: ConstantExpression,
)
