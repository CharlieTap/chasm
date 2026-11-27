package io.github.charlietap.chasm.declarations

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.type.GlobalType

@InternalChasmApi
data class GlobalDeclaration(
    val type: GlobalType,
    val initExpression: ConstantExpression,
)
