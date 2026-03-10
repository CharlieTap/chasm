package io.github.charlietap.chasm.runtime.function

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.InterpretationStyle

class Expression(
    val instructions: Array<DispatchableInstruction> = emptyArray(),
    val interpretationStyle: InterpretationStyle = InterpretationStyle.INSTRUCTION_STACK,
    val fusedIpBody: FusedIpBody? = null,
) {
    companion object {
        val EMPTY = Expression()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Expression

        if (!(instructions contentEquals other.instructions)) return false
        if (interpretationStyle != other.interpretationStyle) return false
        if (fusedIpBody != other.fusedIpBody) return false

        return true
    }

    override fun hashCode(): Int {
        var result = instructions.contentHashCode()
        result = 31 * result + interpretationStyle.hashCode()
        result = 31 * result + (fusedIpBody?.hashCode() ?: 0)
        return result
    }
}
