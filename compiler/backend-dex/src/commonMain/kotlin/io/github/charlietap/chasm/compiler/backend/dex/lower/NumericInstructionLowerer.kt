package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.compiler.backend.abi.DIVIDE_BY_ZERO
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.F32_COPYSIGN
import io.github.charlietap.chasm.compiler.backend.abi.F32_TRUNC
import io.github.charlietap.chasm.compiler.backend.abi.F64_COPYSIGN
import io.github.charlietap.chasm.compiler.backend.abi.F64_TRUNC
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.I64_MUL_WIDE_HIGH_S
import io.github.charlietap.chasm.compiler.backend.abi.I64_MUL_WIDE_HIGH_U
import io.github.charlietap.chasm.compiler.backend.abi.INTEGER_OVERFLOW
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.INT_BIT_COUNT
import io.github.charlietap.chasm.compiler.backend.abi.INT_DIVIDE_UNSIGNED
import io.github.charlietap.chasm.compiler.backend.abi.INT_LEADING_ZEROS
import io.github.charlietap.chasm.compiler.backend.abi.INT_ROTATE_LEFT
import io.github.charlietap.chasm.compiler.backend.abi.INT_ROTATE_RIGHT
import io.github.charlietap.chasm.compiler.backend.abi.INT_TRAILING_ZEROS
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BIT_COUNT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_DIVIDE_UNSIGNED
import io.github.charlietap.chasm.compiler.backend.abi.LONG_LEADING_ZEROS
import io.github.charlietap.chasm.compiler.backend.abi.LONG_REMAINDER_UNSIGNED
import io.github.charlietap.chasm.compiler.backend.abi.LONG_ROTATE_LEFT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_ROTATE_RIGHT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_TRAILING_ZEROS
import io.github.charlietap.chasm.compiler.backend.abi.MATH_CEIL
import io.github.charlietap.chasm.compiler.backend.abi.MATH_FLOOR
import io.github.charlietap.chasm.compiler.backend.abi.MATH_MAX_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MATH_MAX_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.MATH_MIN_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MATH_MIN_FLOAT
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
import io.github.charlietap.dexter.Cond
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
    when (operation) {
        BinaryOperation.DivS, BinaryOperation.DivU, BinaryOperation.RemS, BinaryOperation.RemU ->
            i32Divide(operation, left, right, destination)
        BinaryOperation.Rotl -> i32Rotate(INT_ROTATE_LEFT, left, right, destination)
        BinaryOperation.Rotr -> i32Rotate(INT_ROTATE_RIGHT, left, right, destination)
        else -> throw Unsupported("i32 $operation")
    }
}

private fun LowerContext.i32Rotate(
    rotate: MethodRef,
    left: Operand,
    right: Operand,
    destination: Int,
) {
    loadInt(Registers.I0, Registers.W0, left)
    loadInt(Registers.I1, Registers.W1, right)
    emit.invoke(rotate, Registers.I0, Registers.I1)
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
    when (operation) {
        BinaryOperation.DivS, BinaryOperation.DivU, BinaryOperation.RemS, BinaryOperation.RemU ->
            i64Divide(operation, left, right, destination)
        BinaryOperation.Rotl -> i64Rotate(LONG_ROTATE_LEFT, left, right, destination)
        BinaryOperation.Rotr -> i64Rotate(LONG_ROTATE_RIGHT, left, right, destination)
        else -> throw Unsupported("i64 $operation")
    }
}

private fun LowerContext.i64Rotate(
    rotate: MethodRef,
    left: Operand,
    right: Operand,
    destination: Int,
) {
    loadWide(Registers.W0, left)
    loadInt(Registers.I1, Registers.W1, right)
    emit.invoke(rotate, Registers.W0, Registers.W0 + 1, Registers.I1)
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
        BinaryOperation.Min -> MATH_MIN_FLOAT
        BinaryOperation.Max -> MATH_MAX_FLOAT
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
        BinaryOperation.Min -> MATH_MIN_DOUBLE
        BinaryOperation.Max -> MATH_MAX_DOUBLE
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

private fun LowerContext.i32Divide(
    operation: BinaryOperation,
    left: Operand,
    right: Operand,
    destination: Int,
) {

    val immediate = right as? Operand.Immediate
    if (immediate != null && immediate.bits.toInt() == 0) {
        emit.gotoLabel(trapLabel(DIVIDE_BY_ZERO))
        return
    }

    val signed = operation == BinaryOperation.DivS || operation == BinaryOperation.RemS
    val rightRegister = if (signed) {
        narrowSource(right, Registers.I1)
    } else {
        loadInt(Registers.I1, Registers.W1, right)
        Registers.I1
    }
    if (immediate == null) {
        emit.branchZero(Cond.Eq, rightRegister, trapLabel(DIVIDE_BY_ZERO))
    }

    when (operation) {
        BinaryOperation.DivS -> {
            i32OverflowCheck(left, rightRegister, immediate)
            i32Signed(BinaryOp.DivInt, left, rightRegister, destination)
        }
        BinaryOperation.RemS -> i32Signed(BinaryOp.RemInt, left, rightRegister, destination)
        BinaryOperation.DivU -> {
            loadInt(Registers.I0, Registers.W0, left)
            emit.invoke(INT_DIVIDE_UNSIGNED, Registers.I0, Registers.I1)
            emit.moveResult(Registers.I0)
            storeInt(destination, Registers.I0)
        }
        else -> {
            loadInt(Registers.I0, Registers.W0, left)
            emit.convert(Conversion.IntToLong, Registers.W0, Registers.I0)
            emit.convert(Conversion.IntToLong, Registers.W1, Registers.I1)
            emit.constWide(Registers.W2, UNSIGNED_INT_MASK)
            emit.binary(BinaryOp.AndLong, Registers.W0, Registers.W0, Registers.W2)
            emit.binary(BinaryOp.AndLong, Registers.W1, Registers.W1, Registers.W2)
            emit.binary(BinaryOp.RemLong, Registers.W0, Registers.W0, Registers.W1)
            emit.convert(Conversion.LongToInt, Registers.I0, Registers.W0)
            storeInt(destination, Registers.I0)
        }
    }
}

private fun LowerContext.i32OverflowCheck(
    left: Operand,
    rightRegister: Int,
    immediate: Operand.Immediate?,
) {

    if (immediate != null && immediate.bits.toInt() != -1) {
        return
    }

    val proceed = emit.freshLabel()
    if (immediate == null) {
        emit.literal(LiteralOp.AddInt, Registers.I2, rightRegister, 1)
        emit.branchZero(Cond.Ne, Registers.I2, proceed)
    }

    loadInt(Registers.I0, Registers.W0, left)
    emit.const(Registers.I2, Int.MIN_VALUE)
    emit.branchCompare(Cond.Eq, Registers.I0, Registers.I2, trapLabel(INTEGER_OVERFLOW))
    if (immediate == null) {
        emit.place(proceed)
    }
}

private fun LowerContext.i32Signed(
    operation: BinaryOp,
    left: Operand,
    rightRegister: Int,
    destination: Int,
) {
    val leftRegister = narrowSource(left, Registers.I0)
    val target = narrowTarget(destination)
    emit.binary(operation, target, leftRegister, rightRegister)
    finishNarrow(destination, target)
}

private fun LowerContext.i64Divide(
    operation: BinaryOperation,
    left: Operand,
    right: Operand,
    destination: Int,
) {

    val immediate = right as? Operand.Immediate
    if (immediate != null && immediate.bits == 0L) {
        emit.gotoLabel(trapLabel(DIVIDE_BY_ZERO))
        return
    }

    val signed = operation == BinaryOperation.DivS || operation == BinaryOperation.RemS
    val rightRegister = if (signed) {
        wideSource(right, Registers.W1)
    } else {
        loadWide(Registers.W1, right)
        Registers.W1
    }
    if (immediate == null) {
        emit.constWide(Registers.W2, 0L)
        emit.compare(CompareOp.CmpLong, Registers.I2, rightRegister, Registers.W2)
        emit.branchZero(Cond.Eq, Registers.I2, trapLabel(DIVIDE_BY_ZERO))
    }

    when (operation) {
        BinaryOperation.DivS -> {
            i64OverflowCheck(left, rightRegister, immediate)
            i64Signed(BinaryOp.DivLong, left, rightRegister, destination)
        }
        BinaryOperation.RemS -> i64Signed(BinaryOp.RemLong, left, rightRegister, destination)
        else -> {
            loadWide(Registers.W0, left)
            val helper = if (operation == BinaryOperation.DivU) LONG_DIVIDE_UNSIGNED else LONG_REMAINDER_UNSIGNED
            emit.invoke(helper, Registers.W0, Registers.W0 + 1, Registers.W1, Registers.W1 + 1)
            emit.moveResultWide(Registers.W0)
            store(destination, Registers.W0)
        }
    }
}

private fun LowerContext.i64OverflowCheck(
    left: Operand,
    rightRegister: Int,
    immediate: Operand.Immediate?,
) {

    if (immediate != null && immediate.bits != -1L) {
        return
    }

    val proceed = emit.freshLabel()
    if (immediate == null) {
        emit.constWide(Registers.W2, -1L)
        emit.compare(CompareOp.CmpLong, Registers.I2, rightRegister, Registers.W2)
        emit.branchZero(Cond.Ne, Registers.I2, proceed)
    }

    emit.constWide(Registers.W2, Long.MIN_VALUE)
    emit.compare(CompareOp.CmpLong, Registers.I2, wideSource(left, Registers.W0), Registers.W2)
    emit.branchZero(Cond.Eq, Registers.I2, trapLabel(INTEGER_OVERFLOW))
    if (immediate == null) {
        emit.place(proceed)
    }
}

private fun LowerContext.i64Signed(
    operation: BinaryOp,
    left: Operand,
    rightRegister: Int,
    destination: Int,
) {
    val leftRegister = wideSource(left, Registers.W0)
    val target = wideTarget(destination)
    emit.binary(operation, target, leftRegister, rightRegister)
    finishWide(destination, target)
}

private const val UNSIGNED_INT_MASK = 0xFFFF_FFFFL
