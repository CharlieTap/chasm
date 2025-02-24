package io.github.charlietap.chasm.ir.module

import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.type.GlobalType

data class Global(
    val idx: Index.GlobalIndex,
    val type: GlobalType,
    val initExpression: Expression,
)
