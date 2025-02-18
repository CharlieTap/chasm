package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index
import kotlin.jvm.JvmInline

sealed interface FusedOperand {
    @JvmInline
    value class I32Const(val const: Int) : FusedOperand

    @JvmInline
    value class I64Const(val const: Long) : FusedOperand

    @JvmInline
    value class F32Const(val const: Float) : FusedOperand

    @JvmInline
    value class F64Const(val const: Double) : FusedOperand

    @JvmInline
    value class LocalGet(val index: Index.LocalIndex) : FusedOperand

    @JvmInline
    value class GlobalGet(val index: Index.GlobalIndex) : FusedOperand

    data object ValueStack : FusedOperand
}
