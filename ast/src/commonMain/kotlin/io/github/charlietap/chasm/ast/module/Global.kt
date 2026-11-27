package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.module.Index.GlobalIndex
import io.github.charlietap.chasm.type.GlobalType

@InternalChasmApi
data class Global(
    val idx: GlobalIndex,
    val type: GlobalType,
    val initExpression: Expression,
)
