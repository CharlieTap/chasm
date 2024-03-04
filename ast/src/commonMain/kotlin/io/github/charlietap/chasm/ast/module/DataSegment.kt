package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.Index.DataIndex
import io.github.charlietap.chasm.ast.instruction.Index.MemoryIndex

data class DataSegment(
    val idx: DataIndex,
    val initData: UByteArray,
    val mode: Mode,
) {
    sealed interface Mode {
        data object Passive : Mode

        data class Active(
            val memoryIndex: MemoryIndex,
            val offset: Expression,
        ) : Mode
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataSegment) return false

        if (idx != other.idx) return false
        if (!initData.contentEquals(other.initData)) return false
        if (mode != other.mode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = idx.hashCode()
        result = 31 * result + initData.contentHashCode()
        result = 31 * result + mode.hashCode()
        return result
    }
}
