package io.github.charlietap.chasm.compiler.operand

import io.github.charlietap.chasm.type.ValueType

internal class Operand {
    var stackIndex: Int = 0
        internal set
    var type: ValueType? = null
        private set
    var reservedSlot: Int = 0
        private set
    var sourceKind: OperandSourceKind = OperandSourceKind.Frame
        private set
    var sourceBits: Long = 0
        private set
    var sourceLocalIndex: Int = NO_LOCAL_INDEX
        private set
    var previousLocalAlias: Operand? = null
    var nextLocalAlias: Operand? = null
    var tracksLocal: Boolean = false

    fun reset(
        type: ValueType?,
        reservedSlot: Int,
        sourceKind: OperandSourceKind,
        sourceBits: Long,
        sourceLocalIndex: Int = NO_LOCAL_INDEX,
    ) {
        this.type = type
        this.reservedSlot = reservedSlot
        this.sourceKind = sourceKind
        this.sourceBits = sourceBits
        this.sourceLocalIndex = sourceLocalIndex
        previousLocalAlias = null
        nextLocalAlias = null
        tracksLocal = false
    }

    fun materialize(sourceSlot: Int = reservedSlot) {
        sourceKind = OperandSourceKind.Frame
        sourceBits = sourceSlot.toLong()
        sourceLocalIndex = NO_LOCAL_INDEX
    }

    companion object {
        const val NO_LOCAL_INDEX = -1
    }
}

internal typealias OperandSource = Operand

internal enum class OperandSourceKind {
    I32Immediate,
    I64Immediate,
    F32Immediate,
    F64Immediate,
    Local,
    Frame,
}
