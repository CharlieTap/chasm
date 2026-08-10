package io.github.charlietap.chasm.compiler.operand

internal val OperandSource.i32Immediate: Int
    get() = sourceBits.toInt()

internal val OperandSource.i64Immediate: Long
    get() = sourceBits

internal val OperandSource.f32Immediate: Float
    get() = Float.fromBits(sourceBits.toInt())

internal val OperandSource.f64Immediate: Double
    get() = Double.fromBits(sourceBits)

internal val OperandSource.sourceSlot: Int
    get() = sourceBits.toInt()

internal val OperandSource.isImmediate: Boolean
    get() = sourceKind.isImmediate

internal val OperandSourceKind.isImmediate: Boolean
    get() = when (this) {
        OperandSourceKind.I32Immediate,
        OperandSourceKind.I64Immediate,
        OperandSourceKind.F32Immediate,
        OperandSourceKind.F64Immediate,
        -> true
        OperandSourceKind.Local,
        OperandSourceKind.Frame,
        -> false
    }
