package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.F32_COPYSIGN
import io.github.charlietap.chasm.compiler.backend.abi.F32_MAX
import io.github.charlietap.chasm.compiler.backend.abi.F32_MIN
import io.github.charlietap.chasm.compiler.backend.abi.F32_TRUNC
import io.github.charlietap.chasm.compiler.backend.abi.F64_COPYSIGN
import io.github.charlietap.chasm.compiler.backend.abi.F64_MAX
import io.github.charlietap.chasm.compiler.backend.abi.F64_MIN
import io.github.charlietap.chasm.compiler.backend.abi.F64_TRUNC
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.I32_DIV_S
import io.github.charlietap.chasm.compiler.backend.abi.I32_DIV_U
import io.github.charlietap.chasm.compiler.backend.abi.I32_REM_S
import io.github.charlietap.chasm.compiler.backend.abi.I32_REM_U
import io.github.charlietap.chasm.compiler.backend.abi.I64_DIV_S
import io.github.charlietap.chasm.compiler.backend.abi.I64_DIV_U
import io.github.charlietap.chasm.compiler.backend.abi.I64_MUL_WIDE_HIGH_S
import io.github.charlietap.chasm.compiler.backend.abi.I64_MUL_WIDE_HIGH_U
import io.github.charlietap.chasm.compiler.backend.abi.I64_REM_S
import io.github.charlietap.chasm.compiler.backend.abi.I64_REM_U
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.INT_BIT_COUNT
import io.github.charlietap.chasm.compiler.backend.abi.INT_LEADING_ZEROS
import io.github.charlietap.chasm.compiler.backend.abi.INT_ROTATE_LEFT
import io.github.charlietap.chasm.compiler.backend.abi.INT_ROTATE_RIGHT
import io.github.charlietap.chasm.compiler.backend.abi.INT_TRAILING_ZEROS
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BIT_COUNT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_LEADING_ZEROS
import io.github.charlietap.chasm.compiler.backend.abi.LONG_ROTATE_LEFT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_ROTATE_RIGHT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_TRAILING_ZEROS
import io.github.charlietap.chasm.compiler.backend.abi.MATH_CEIL
import io.github.charlietap.chasm.compiler.backend.abi.MATH_FLOOR
import io.github.charlietap.chasm.compiler.backend.abi.MATH_RINT
import io.github.charlietap.chasm.compiler.backend.abi.MATH_SQRT
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.analysis.form.BinaryOperation
import io.github.charlietap.chasm.compiler.backend.analysis.form.NumericForm
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.UnaryOperation
import io.github.charlietap.chasm.compiler.backend.analysis.form.WideOperation
import io.github.charlietap.chasm.compiler.backend.analysis.form.form
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.CompareOp
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.LiteralOp
import io.github.charlietap.dexter.UnaryOp

internal fun lowerNumericInstruction(
    instruction: NumericInstruction,
    context: LowerContext,
) {
    when (val form = instruction.form()) {
        is NumericForm.Constant -> context.copyToSlot(form.destination, Operand.Immediate(form.bits))
        is NumericForm.BitFieldExtract -> context.bitFieldExtract(form)
        is NumericForm.Unary -> when (form.type) {
            NumberType.I32 -> context.i32Unary(form.operation, form.operand, form.destination)
            NumberType.I64 -> context.i64Unary(form.operation, form.operand, form.destination)
            NumberType.F32 -> context.f32Unary(form.operation, form.operand, form.destination)
            NumberType.F64 -> context.f64Unary(form.operation, form.operand, form.destination)
        }
        is NumericForm.Binary -> when (form.type) {
            NumberType.I32 -> context.i32Binary(form.operation, form.left, form.right, form.destination)
            NumberType.I64 -> context.i64Binary(form.operation, form.left, form.right, form.destination)
            NumberType.F32 -> context.f32Binary(form.operation, form.left, form.right, form.destination)
            NumberType.F64 -> context.f64Binary(form.operation, form.left, form.right, form.destination)
        }
        is NumericForm.Compare -> context.booleanResult(form.destination) { taken ->
            context.branchCompare(form.type, form.comparison, form.left, form.right, taken)
        }
        is NumericForm.Convert -> context.lowerConversion(form.conversion, form.operand, form.destination)
        is NumericForm.Wide -> context.wide(form)
    }
}

private fun LowerContext.bitFieldExtract(
    form: NumericForm.BitFieldExtract,
) {
    loadInt(Registers.I0, Registers.W0, form.operand)
    emit.const(Registers.I1, form.shift)
    emit.binary(BinaryOp.UshrInt, Registers.I0, Registers.I0, Registers.I1)
    emit.const(Registers.I1, form.mask)
    emit.binary(BinaryOp.AndInt, Registers.I0, Registers.I0, Registers.I1)
    storeInt(form.destination, Registers.I0)
}

private fun LowerContext.i32Binary(
    operation: BinaryOperation,
    left: Operand,
    right: Operand,
    destination: Int,
) {
    val arithmetic = i32Instruction(operation)
    if (arithmetic != null) {
        val source = narrowSource(left, Registers.I0)
        val target = narrowTarget(destination)
        val literal = i32Literal(operation)
        val constant = (right as? Operand.Immediate)?.bits?.toInt()
        val fitsLiteral = constant != null && constant in Byte.MIN_VALUE..Byte.MAX_VALUE &&
            !(operation == BinaryOperation.Sub && constant == Byte.MIN_VALUE.toInt())
        if (literal != null && constant != null && fitsLiteral) {
            emit.literal(literal, target, source, constant)
        } else {
            emit.binary(arithmetic, target, source, narrowSource(right, Registers.I1))
        }
        return finishNarrow(destination, target)
    }
    val helper = when (operation) {
        BinaryOperation.DivS -> I32_DIV_S
        BinaryOperation.DivU -> I32_DIV_U
        BinaryOperation.RemS -> I32_REM_S
        BinaryOperation.RemU -> I32_REM_U
        BinaryOperation.Rotl -> INT_ROTATE_LEFT
        BinaryOperation.Rotr -> INT_ROTATE_RIGHT
        else -> throw Unsupported("i32 $operation")
    }
    loadInt(Registers.I0, Registers.W0, left)
    loadInt(Registers.I1, Registers.W1, right)
    emit.invoke(helper, Registers.I0, Registers.I1)
    emit.moveResult(Registers.I0)
    storeInt(destination, Registers.I0)
}

private fun i32Instruction(
    operation: BinaryOperation,
): BinaryOp? = when (operation) {
    BinaryOperation.Add -> BinaryOp.AddInt
    BinaryOperation.Sub -> BinaryOp.SubInt
    BinaryOperation.Mul -> BinaryOp.MulInt
    BinaryOperation.And -> BinaryOp.AndInt
    BinaryOperation.Or -> BinaryOp.OrInt
    BinaryOperation.Xor -> BinaryOp.XorInt
    BinaryOperation.Shl -> BinaryOp.ShlInt
    BinaryOperation.ShrS -> BinaryOp.ShrInt
    BinaryOperation.ShrU -> BinaryOp.UshrInt
    else -> null
}

private fun i32Literal(
    operation: BinaryOperation,
): LiteralOp? = when (operation) {
    BinaryOperation.Add -> LiteralOp.AddInt
    BinaryOperation.Sub -> LiteralOp.SubInt
    BinaryOperation.Mul -> LiteralOp.MulInt
    BinaryOperation.And -> LiteralOp.AndInt
    BinaryOperation.Or -> LiteralOp.OrInt
    BinaryOperation.Xor -> LiteralOp.XorInt
    BinaryOperation.Shl -> LiteralOp.ShlInt
    BinaryOperation.ShrS -> LiteralOp.ShrInt
    BinaryOperation.ShrU -> LiteralOp.UshrInt
    else -> null
}

private fun LowerContext.i32Unary(
    operation: UnaryOperation,
    operand: Operand,
    destination: Int,
) {
    loadInt(Registers.I0, Registers.W0, operand)
    when (operation) {
        UnaryOperation.Clz, UnaryOperation.Ctz, UnaryOperation.Popcnt -> {
            emit.invoke(intBitCount(operation), Registers.I0)
            emit.moveResult(Registers.I0)
        }
        UnaryOperation.Extend8S -> emit.convert(Conversion.IntToByte, Registers.I0, Registers.I0)
        UnaryOperation.Extend16S -> emit.convert(Conversion.IntToShort, Registers.I0, Registers.I0)
        else -> throw Unsupported("i32 $operation")
    }
    storeInt(destination, Registers.I0)
}

private fun intBitCount(
    operation: UnaryOperation,
): MethodRef = when (operation) {
    UnaryOperation.Clz -> INT_LEADING_ZEROS
    UnaryOperation.Ctz -> INT_TRAILING_ZEROS
    else -> INT_BIT_COUNT
}

private fun LowerContext.i64Binary(
    operation: BinaryOperation,
    left: Operand,
    right: Operand,
    destination: Int,
) {
    val arithmetic = when (operation) {
        BinaryOperation.Add -> BinaryOp.AddLong
        BinaryOperation.Sub -> BinaryOp.SubLong
        BinaryOperation.Mul -> BinaryOp.MulLong
        BinaryOperation.And -> BinaryOp.AndLong
        BinaryOperation.Or -> BinaryOp.OrLong
        BinaryOperation.Xor -> BinaryOp.XorLong
        else -> null
    }
    if (arithmetic != null) {
        val l = wideSource(left, Registers.W0)
        val r = wideSource(right, Registers.W1)
        val target = wideTarget(destination)
        emit.binary(arithmetic, target, l, r)
        return finishWide(destination, target)
    }
    val shift = when (operation) {
        BinaryOperation.Shl -> BinaryOp.ShlLong
        BinaryOperation.ShrS -> BinaryOp.ShrLong
        BinaryOperation.ShrU -> BinaryOp.UshrLong
        else -> null
    }
    if (shift != null) {
        loadInt(Registers.I1, Registers.W1, right)
        val l = wideSource(left, Registers.W0)
        val target = wideTarget(destination)
        emit.binary(shift, target, l, Registers.I1)
        return finishWide(destination, target)
    }
    loadWide(Registers.W0, left)
    when (operation) {
        BinaryOperation.Rotl, BinaryOperation.Rotr -> {
            loadInt(Registers.I1, Registers.W1, right)
            val rotate = if (operation == BinaryOperation.Rotl) LONG_ROTATE_LEFT else LONG_ROTATE_RIGHT
            emit.invoke(rotate, Registers.W0, Registers.W0 + 1, Registers.I1)
        }
        else -> {
            val helper = when (operation) {
                BinaryOperation.DivS -> I64_DIV_S
                BinaryOperation.DivU -> I64_DIV_U
                BinaryOperation.RemS -> I64_REM_S
                BinaryOperation.RemU -> I64_REM_U
                else -> throw Unsupported("i64 $operation")
            }
            loadWide(Registers.W1, right)
            emit.invoke(helper, Registers.W0, Registers.W0 + 1, Registers.W1, Registers.W1 + 1)
        }
    }
    emit.moveResultWide(Registers.W0)
    store(destination, Registers.W0)
}

private fun LowerContext.i64Unary(
    operation: UnaryOperation,
    operand: Operand,
    destination: Int,
) {
    loadWide(Registers.W0, operand)
    when (operation) {
        UnaryOperation.Clz, UnaryOperation.Ctz, UnaryOperation.Popcnt -> {
            val count = when (operation) {
                UnaryOperation.Clz -> LONG_LEADING_ZEROS
                UnaryOperation.Ctz -> LONG_TRAILING_ZEROS
                else -> LONG_BIT_COUNT
            }
            emit.invoke(count, Registers.W0, Registers.W0 + 1)
            emit.moveResult(Registers.I0)
        }
        UnaryOperation.Extend8S, UnaryOperation.Extend16S, UnaryOperation.Extend32S -> {
            emit.convert(Conversion.LongToInt, Registers.I0, Registers.W0)
            if (operation == UnaryOperation.Extend8S) emit.convert(Conversion.IntToByte, Registers.I0, Registers.I0)
            if (operation == UnaryOperation.Extend16S) emit.convert(Conversion.IntToShort, Registers.I0, Registers.I0)
        }
        else -> throw Unsupported("i64 $operation")
    }
    storeInt(destination, Registers.I0)
}

private fun LowerContext.f32Binary(
    operation: BinaryOperation,
    left: Operand,
    right: Operand,
    destination: Int,
) {
    val arithmetic = when (operation) {
        BinaryOperation.Add -> BinaryOp.AddFloat
        BinaryOperation.Sub -> BinaryOp.SubFloat
        BinaryOperation.Mul -> BinaryOp.MulFloat
        BinaryOperation.Div -> BinaryOp.DivFloat
        else -> null
    }
    if (arithmetic != null) {
        val l = floatSource(left, Registers.I0)
        val r = floatSource(right, Registers.I1)
        if (isFloat(destination)) {
            checkNarrowWrite(destination)
            return emit.binary(arithmetic, registers.home(destination), l, r)
        }
        emit.binary(arithmetic, Registers.I0, l, r)
        return storeFloat(destination, Registers.I0)
    }
    val helper = when (operation) {
        BinaryOperation.Min -> F32_MIN
        BinaryOperation.Max -> F32_MAX
        BinaryOperation.Copysign -> F32_COPYSIGN
        else -> throw Unsupported("f32 $operation")
    }
    loadFloat(Registers.I0, Registers.W0, left)
    loadFloat(Registers.I1, Registers.W1, right)
    emit.invoke(helper, Registers.I0, Registers.I1)
    emit.moveResult(Registers.I0)
    storeFloat(destination, Registers.I0)
}

private fun LowerContext.f32Unary(
    operation: UnaryOperation,
    operand: Operand,
    destination: Int,
) {
    loadFloat(Registers.I0, Registers.W0, operand)
    when (operation) {
        UnaryOperation.Neg -> emit.unary(UnaryOp.NegFloat, Registers.I0, Registers.I0)
        UnaryOperation.Abs -> {
            emit.invoke(FLOAT_TO_RAW_INT_BITS, Registers.I0)
            emit.moveResult(Registers.I0)
            emit.const(Registers.I1, Int.MAX_VALUE)
            emit.binary(BinaryOp.AndInt, Registers.I0, Registers.I0, Registers.I1)
            emit.invoke(INT_BITS_TO_FLOAT, Registers.I0)
            emit.moveResult(Registers.I0)
        }
        UnaryOperation.Trunc -> {
            emit.invoke(F32_TRUNC, Registers.I0)
            emit.moveResult(Registers.I0)
        }
        else -> {
            emit.convert(Conversion.FloatToDouble, Registers.W1, Registers.I0)
            emit.invoke(mathMethod(operation, "f32"), Registers.W1, Registers.W1 + 1)
            emit.moveResultWide(Registers.W1)
            emit.convert(Conversion.DoubleToFloat, Registers.I0, Registers.W1)
        }
    }
    storeFloat(destination, Registers.I0)
}

private fun LowerContext.f64Binary(
    operation: BinaryOperation,
    left: Operand,
    right: Operand,
    destination: Int,
) {
    val arithmetic = when (operation) {
        BinaryOperation.Add -> BinaryOp.AddDouble
        BinaryOperation.Sub -> BinaryOp.SubDouble
        BinaryOperation.Mul -> BinaryOp.MulDouble
        BinaryOperation.Div -> BinaryOp.DivDouble
        else -> null
    }
    if (arithmetic != null) {
        val l = doubleSource(left, Registers.W0)
        val r = doubleSource(right, Registers.W1)
        if (isDouble(destination)) return emit.binary(arithmetic, registers.home(destination), l, r)
        emit.binary(arithmetic, Registers.W0, l, r)
        return storeDouble(destination, Registers.W0)
    }
    val helper = when (operation) {
        BinaryOperation.Min -> F64_MIN
        BinaryOperation.Max -> F64_MAX
        BinaryOperation.Copysign -> F64_COPYSIGN
        else -> throw Unsupported("f64 $operation")
    }
    loadDouble(Registers.W0, left)
    loadDouble(Registers.W1, right)
    emit.invoke(helper, Registers.W0, Registers.W0 + 1, Registers.W1, Registers.W1 + 1)
    emit.moveResultWide(Registers.W0)
    storeDouble(destination, Registers.W0)
}

private fun LowerContext.f64Unary(
    operation: UnaryOperation,
    operand: Operand,
    destination: Int,
) {
    loadDouble(Registers.W0, operand)
    when (operation) {
        UnaryOperation.Neg -> emit.unary(UnaryOp.NegDouble, Registers.W0, Registers.W0)
        UnaryOperation.Abs -> {
            emit.invoke(DOUBLE_TO_RAW_LONG_BITS, Registers.W0, Registers.W0 + 1)
            emit.moveResultWide(Registers.W0)
            emit.constWide(Registers.W1, Long.MAX_VALUE)
            emit.binary(BinaryOp.AndLong, Registers.W0, Registers.W0, Registers.W1)
            emit.invoke(LONG_BITS_TO_DOUBLE, Registers.W0, Registers.W0 + 1)
            emit.moveResultWide(Registers.W0)
        }
        UnaryOperation.Trunc -> {
            emit.invoke(F64_TRUNC, Registers.W0, Registers.W0 + 1)
            emit.moveResultWide(Registers.W0)
        }
        else -> {
            emit.invoke(mathMethod(operation, "f64"), Registers.W0, Registers.W0 + 1)
            emit.moveResultWide(Registers.W0)
        }
    }
    storeDouble(destination, Registers.W0)
}

private fun mathMethod(
    operation: UnaryOperation,
    type: String,
): MethodRef = when (operation) {
    UnaryOperation.Ceil -> MATH_CEIL
    UnaryOperation.Floor -> MATH_FLOOR
    UnaryOperation.Nearest -> MATH_RINT
    UnaryOperation.Sqrt -> MATH_SQRT
    else -> throw Unsupported("$type $operation")
}

private fun LowerContext.wide(
    form: NumericForm.Wide,
) {
    val operands = form.operands
    when (form.operation) {
        WideOperation.Add128, WideOperation.Sub128 -> {
            val add = form.operation == WideOperation.Add128
            val arithmetic = if (add) BinaryOp.AddLong else BinaryOp.SubLong
            loadWide(Registers.W0, operands[0])
            loadWide(Registers.W1, operands[2])
            emit.binary(arithmetic, Registers.W2, Registers.W0, Registers.W1)
            emit.constWide(Registers.W3, Long.MIN_VALUE)
            if (add) {
                emit.binary(BinaryOp.XorLong, Registers.W1, Registers.W2, Registers.W3)
                emit.binary(BinaryOp.XorLong, Registers.W0, Registers.W0, Registers.W3)
            } else {
                emit.binary(BinaryOp.XorLong, Registers.W0, Registers.W0, Registers.W3)
                emit.binary(BinaryOp.XorLong, Registers.W1, Registers.W1, Registers.W3)
                emit.moveWide(Registers.W3, Registers.W0)
                emit.moveWide(Registers.W0, Registers.W1)
                emit.moveWide(Registers.W1, Registers.W3)
            }
            emit.compare(CompareOp.CmpLong, Registers.I0, Registers.W1, Registers.W0)
            emit.literal(LiteralOp.UshrInt, Registers.I0, Registers.I0, 31)
            emit.convert(Conversion.IntToLong, Registers.W1, Registers.I0)
            loadWide(Registers.W0, operands[1])
            loadWide(Registers.W3, operands[3])
            emit.binary(arithmetic, Registers.W0, Registers.W0, Registers.W3)
            emit.binary(arithmetic, Registers.W0, Registers.W0, Registers.W1)
            store(form.low, Registers.W2)
            store(form.high, Registers.W0)
        }
        WideOperation.MulWideS, WideOperation.MulWideU -> {
            loadWide(Registers.W0, operands[0])
            loadWide(Registers.W1, operands[1])
            emit.binary(BinaryOp.MulLong, Registers.W2, Registers.W0, Registers.W1)
            val high = if (form.operation == WideOperation.MulWideS) I64_MUL_WIDE_HIGH_S else I64_MUL_WIDE_HIGH_U
            emit.invoke(high, Registers.W0, Registers.W0 + 1, Registers.W1, Registers.W1 + 1)
            emit.moveResultWide(Registers.W0)
            store(form.low, Registers.W2)
            store(form.high, Registers.W0)
        }
    }
}
