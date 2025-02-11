package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ast.type.ValueType
import kotlin.jvm.JvmInline

sealed interface ParametricInstruction : Instruction {
    data object Drop : ParametricInstruction

    data object Select : ParametricInstruction

    @JvmInline
    value class SelectWithType(val types: List<ValueType>) : ParametricInstruction
}
