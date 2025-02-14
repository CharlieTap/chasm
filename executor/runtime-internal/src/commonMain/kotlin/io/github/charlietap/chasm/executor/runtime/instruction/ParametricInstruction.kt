package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.ast.type.ValueType
import kotlin.jvm.JvmInline

sealed interface ParametricInstruction : LinkedInstruction {
    data object Drop : ParametricInstruction

    data object Select : ParametricInstruction

    @JvmInline
    value class SelectWithType(val types: List<ValueType>) : ParametricInstruction
}
