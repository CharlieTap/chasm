package io.github.charlietap.chasm.ast.instruction

import io.github.charlietap.chasm.ast.type.ValueType
import kotlin.jvm.JvmInline

interface ParametricInstruction : Instruction {
    object Drop : ParametricInstruction

    object Select : ParametricInstruction

    @JvmInline
    value class SelectWithType(val types: List<ValueType>) : ParametricInstruction
}
