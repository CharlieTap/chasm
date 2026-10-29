package io.github.charlietap.chasm.compiler.backend.analysis.form

import io.github.charlietap.chasm.type.NumberType

sealed interface NumericForm {

    val resultType: NumberType

    class Constant(
        val type: NumberType,
        val bits: Long,
        val destination: Int,
    ) : NumericForm {
        override val resultType get() = type
    }

    class BitFieldExtract(
        val operand: Operand,
        val shift: Int,
        val mask: Int,
        val destination: Int,
    ) : NumericForm {
        override val resultType get() = NumberType.I32
    }

    class Unary(
        val type: NumberType,
        val operation: UnaryOperation,
        val operand: Operand,
        val destination: Int,
    ) : NumericForm {
        override val resultType get() = type
    }

    class Binary(
        val type: NumberType,
        val operation: BinaryOperation,
        val left: Operand,
        val right: Operand,
        val destination: Int,
    ) : NumericForm {
        override val resultType get() = type
    }

    class Compare(
        val type: NumberType,
        val comparison: Comparison,
        val left: Operand,
        val right: Operand,
        val destination: Int,
    ) : NumericForm {
        override val resultType get() = NumberType.I32
    }

    class Convert(
        val conversion: Conversion,
        val operand: Operand,
        val destination: Int,
    ) : NumericForm {
        override val resultType get() = conversion.result
    }

    class Wide(
        val operation: WideOperation,
        val operands: List<Operand>,
        val low: Int,
        val high: Int,
    ) : NumericForm {
        override val resultType get() = NumberType.I64
    }
}

enum class UnaryOperation {
    Clz,
    Ctz,
    Popcnt,
    Extend8S,
    Extend16S,
    Extend32S,
    Abs,
    Neg,
    Ceil,
    Floor,
    Trunc,
    Nearest,
    Sqrt,
}

enum class BinaryOperation {
    Add,
    Sub,
    Mul,
    Div,
    DivS,
    DivU,
    RemS,
    RemU,
    And,
    Or,
    Xor,
    Shl,
    ShrS,
    ShrU,
    Rotl,
    Rotr,
    Min,
    Max,
    Copysign,
}

enum class Comparison(
    val ordering: Ordering,
    val unsigned: Boolean = false,
) {
    Eq(Ordering.Equal),
    Ne(Ordering.NotEqual),
    LtS(Ordering.Less),
    LtU(Ordering.Less, unsigned = true),
    GtS(Ordering.Greater),
    GtU(Ordering.Greater, unsigned = true),
    LeS(Ordering.LessOrEqual),
    LeU(Ordering.LessOrEqual, unsigned = true),
    GeS(Ordering.GreaterOrEqual),
    GeU(Ordering.GreaterOrEqual, unsigned = true),
    Lt(Ordering.Less),
    Gt(Ordering.Greater),
    Le(Ordering.LessOrEqual),
    Ge(Ordering.GreaterOrEqual),
}

enum class Ordering {
    Equal,
    NotEqual,
    Less,
    Greater,
    LessOrEqual,
    GreaterOrEqual,
}

enum class Conversion(
    val source: NumberType,
    val result: NumberType,
) {
    I32WrapI64(NumberType.I64, NumberType.I32),
    I32TruncF32S(NumberType.F32, NumberType.I32),
    I32TruncF32U(NumberType.F32, NumberType.I32),
    I32TruncF64S(NumberType.F64, NumberType.I32),
    I32TruncF64U(NumberType.F64, NumberType.I32),
    I32TruncSatF32S(NumberType.F32, NumberType.I32),
    I32TruncSatF32U(NumberType.F32, NumberType.I32),
    I32TruncSatF64S(NumberType.F64, NumberType.I32),
    I32TruncSatF64U(NumberType.F64, NumberType.I32),
    I64ExtendI32S(NumberType.I32, NumberType.I64),
    I64ExtendI32U(NumberType.I32, NumberType.I64),
    I64TruncF32S(NumberType.F32, NumberType.I64),
    I64TruncF32U(NumberType.F32, NumberType.I64),
    I64TruncF64S(NumberType.F64, NumberType.I64),
    I64TruncF64U(NumberType.F64, NumberType.I64),
    I64TruncSatF32S(NumberType.F32, NumberType.I64),
    I64TruncSatF32U(NumberType.F32, NumberType.I64),
    I64TruncSatF64S(NumberType.F64, NumberType.I64),
    I64TruncSatF64U(NumberType.F64, NumberType.I64),
    F32ConvertI32S(NumberType.I32, NumberType.F32),
    F32ConvertI32U(NumberType.I32, NumberType.F32),
    F32ConvertI64S(NumberType.I64, NumberType.F32),
    F32ConvertI64U(NumberType.I64, NumberType.F32),
    F32DemoteF64(NumberType.F64, NumberType.F32),
    F64ConvertI32S(NumberType.I32, NumberType.F64),
    F64ConvertI32U(NumberType.I32, NumberType.F64),
    F64ConvertI64S(NumberType.I64, NumberType.F64),
    F64ConvertI64U(NumberType.I64, NumberType.F64),
    F64PromoteF32(NumberType.F32, NumberType.F64),
}

enum class WideOperation {
    Add128,
    Sub128,
    MulWideS,
    MulWideU,
}
