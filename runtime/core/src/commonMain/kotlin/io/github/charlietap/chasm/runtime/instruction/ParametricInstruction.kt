package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.type.ValueType
import kotlin.jvm.JvmInline

sealed interface ParametricInstruction : LinkedInstruction {
    data object Drop : ParametricInstruction

    data object Select : ParametricInstruction

    @JvmInline
    value class SelectWithType(val types: List<ValueType>) : ParametricInstruction
}
