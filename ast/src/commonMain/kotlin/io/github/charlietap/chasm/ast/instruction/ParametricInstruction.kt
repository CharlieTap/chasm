package io.github.charlietap.chasm.ast.instruction

import io.github.charlietap.chasm.type.ValueType
import kotlin.jvm.JvmInline

sealed interface ParametricInstruction : Instruction {
    data object Drop : ParametricInstruction

    data object Select : ParametricInstruction

    @JvmInline
    value class SelectWithType(val types: List<ValueType>) : ParametricInstruction
}
