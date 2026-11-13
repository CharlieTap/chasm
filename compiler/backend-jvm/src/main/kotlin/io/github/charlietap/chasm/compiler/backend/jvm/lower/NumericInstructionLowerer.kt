package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.compiler.backend.abi.DIVIDE_BY_ZERO
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.F32_TRUNC
import io.github.charlietap.chasm.compiler.backend.abi.F64_TRUNC
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.I64_MUL_WIDE_HIGH_S
import io.github.charlietap.chasm.compiler.backend.abi.I64_MUL_WIDE_HIGH_U
import io.github.charlietap.chasm.compiler.backend.abi.INTEGER_OVERFLOW
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.INT_BIT_COUNT
import io.github.charlietap.chasm.compiler.backend.abi.INT_DIVIDE_UNSIGNED
import io.github.charlietap.chasm.compiler.backend.abi.INT_LEADING_ZEROS
import io.github.charlietap.chasm.compiler.backend.abi.INT_TRAILING_ZEROS
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BIT_COUNT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_DIVIDE_UNSIGNED
import io.github.charlietap.chasm.compiler.backend.abi.LONG_LEADING_ZEROS
import io.github.charlietap.chasm.compiler.backend.abi.LONG_REMAINDER_UNSIGNED
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
import org.objectweb.asm.Opcodes

internal fun lowerNumericInstruction(
    instruction: NumericInstruction,
    context: LowerContext,
) {
    when (val form = instruction.form()) {
        is NumericForm.Constant -> context.copyToSlot(form.destination, Operand.Immediate(form.bits))
        is NumericForm.BitFieldExtract -> context.bitFieldExtract(form)
        is NumericForm.Unary -> context.unary(form)
        is NumericForm.Binary -> context.binary(form)
        is NumericForm.Compare -> context.compare(form)
        is NumericForm.Convert -> context.convert(form)
        is NumericForm.Wide -> context.wide(form)
    }
}

private fun LowerContext.bitFieldExtract(
    form: NumericForm.BitFieldExtract,
) {
    pushInt(form.operand)
    emit.int(form.shift)
    emit.insn(Opcodes.IUSHR)
    emit.int(form.mask)
    emit.insn(Opcodes.IAND)
    storeInt(form.destination)
}

private fun LowerContext.unary(
    form: NumericForm.Unary,
) {
    val type = form.type
    when (form.operation) {
        UnaryOperation.Abs -> {
            absolute(form)
            return
        }
        UnaryOperation.Neg -> {
            negate(form)
            return
        }
        UnaryOperation.Extend32S -> {
            pushInt(form.operand)
            emit.insn(Opcodes.I2L)
            storeLong(form.destination)
            return
        }
        else -> Unit
    }
    push(type, form.operand)
    when (type) {
        NumberType.I32 -> when (form.operation) {
            UnaryOperation.Clz -> emit.invokeStatic(INT_LEADING_ZEROS)
            UnaryOperation.Ctz -> emit.invokeStatic(INT_TRAILING_ZEROS)
            UnaryOperation.Popcnt -> emit.invokeStatic(INT_BIT_COUNT)
            UnaryOperation.Extend8S -> emit.insn(Opcodes.I2B)
            UnaryOperation.Extend16S -> emit.insn(Opcodes.I2S)
            else -> unsupported(form)
        }
        NumberType.I64 -> when (form.operation) {
            UnaryOperation.Clz -> invokeWidened(LONG_LEADING_ZEROS)
            UnaryOperation.Ctz -> invokeWidened(LONG_TRAILING_ZEROS)
            UnaryOperation.Popcnt -> invokeWidened(LONG_BIT_COUNT)
            UnaryOperation.Extend8S -> narrowed(Opcodes.I2B)
            UnaryOperation.Extend16S -> narrowed(Opcodes.I2S)
            else -> unsupported(form)
        }
        NumberType.F32 -> when (form.operation) {
            UnaryOperation.Ceil -> throughDouble(MATH_CEIL)
            UnaryOperation.Floor -> throughDouble(MATH_FLOOR)
            UnaryOperation.Nearest -> throughDouble(MATH_RINT)
            UnaryOperation.Sqrt -> throughDouble(MATH_SQRT)
            UnaryOperation.Trunc -> emit.invokeStatic(F32_TRUNC)
            else -> unsupported(form)
        }
        NumberType.F64 -> when (form.operation) {
            UnaryOperation.Ceil -> emit.invokeStatic(MATH_CEIL)
            UnaryOperation.Floor -> emit.invokeStatic(MATH_FLOOR)
            UnaryOperation.Nearest -> emit.invokeStatic(MATH_RINT)
            UnaryOperation.Sqrt -> emit.invokeStatic(MATH_SQRT)
            UnaryOperation.Trunc -> emit.invokeStatic(F64_TRUNC)
            else -> unsupported(form)
        }
        else -> unsupported(form)
    }
    store(type, form.destination)
}

private fun LowerContext.negate(form: NumericForm.Unary) {
    when (form.type) {
        NumberType.F32 -> {
            pushFloat(form.operand)
            emit.insn(Opcodes.FNEG)
            storeFloat(form.destination)
        }
        NumberType.F64 -> {
            pushDouble(form.operand)
            emit.insn(Opcodes.DNEG)
            storeDouble(form.destination)
        }
        else -> unsupported(form)
    }
}

private fun LowerContext.absolute(form: NumericForm.Unary) {
    when (form.type) {
        NumberType.F32 -> {
            pushFloat(form.operand)
            emit.invokeStatic(FLOAT_TO_RAW_INT_BITS)
            emit.int(Int.MAX_VALUE)
            emit.insn(Opcodes.IAND)
            emit.invokeStatic(INT_BITS_TO_FLOAT)
            storeFloat(form.destination)
        }
        NumberType.F64 -> {
            pushDouble(form.operand)
            emit.invokeStatic(DOUBLE_TO_RAW_LONG_BITS)
            emit.long(Long.MAX_VALUE)
            emit.insn(Opcodes.LAND)
            emit.invokeStatic(LONG_BITS_TO_DOUBLE)
            storeDouble(form.destination)
        }
        else -> unsupported(form)
    }
}

private fun LowerContext.invokeWidened(
    method: MethodRef,
) {
    emit.invokeStatic(method)
    emit.insn(Opcodes.I2L)
}

private fun LowerContext.narrowed(
    opcode: Int,
) {
    emit.insn(Opcodes.L2I)
    emit.insn(opcode)
    emit.insn(Opcodes.I2L)
}

private fun LowerContext.throughDouble(
    method: MethodRef,
) {
    emit.insn(Opcodes.F2D)
    emit.invokeStatic(method)
    emit.insn(Opcodes.D2F)
}

private fun LowerContext.binary(
    form: NumericForm.Binary,
) {
    val type = form.type
    when (form.operation) {
        BinaryOperation.DivS, BinaryOperation.DivU, BinaryOperation.RemS, BinaryOperation.RemU -> {
            val divisor = form.constantDivisor()
            if (divisor == 0L) {
                emit.jump(Opcodes.GOTO, trapLabel(DIVIDE_BY_ZERO))
            } else {
                divide(form, divisor)
            }
            return
        }
        BinaryOperation.Rotl, BinaryOperation.Rotr -> {
            rotate(form)
            return
        }
        else -> Unit
    }
    push(type, form.left)
    if (type == NumberType.I64 && form.operation.takesIntCount()) {
        pushInt(form.right)
    } else {
        push(type, form.right)
    }
    when (type) {
        NumberType.I32 -> when (form.operation) {
            BinaryOperation.Add -> emit.insn(Opcodes.IADD)
            BinaryOperation.Sub -> emit.insn(Opcodes.ISUB)
            BinaryOperation.Mul -> emit.insn(Opcodes.IMUL)
            BinaryOperation.And -> emit.insn(Opcodes.IAND)
            BinaryOperation.Or -> emit.insn(Opcodes.IOR)
            BinaryOperation.Xor -> emit.insn(Opcodes.IXOR)
            BinaryOperation.Shl -> emit.insn(Opcodes.ISHL)
            BinaryOperation.ShrS -> emit.insn(Opcodes.ISHR)
            BinaryOperation.ShrU -> emit.insn(Opcodes.IUSHR)
            else -> unsupported(form)
        }
        NumberType.I64 -> when (form.operation) {
            BinaryOperation.Add -> emit.insn(Opcodes.LADD)
            BinaryOperation.Sub -> emit.insn(Opcodes.LSUB)
            BinaryOperation.Mul -> emit.insn(Opcodes.LMUL)
            BinaryOperation.And -> emit.insn(Opcodes.LAND)
            BinaryOperation.Or -> emit.insn(Opcodes.LOR)
            BinaryOperation.Xor -> emit.insn(Opcodes.LXOR)
            BinaryOperation.Shl -> emit.insn(Opcodes.LSHL)
            BinaryOperation.ShrS -> emit.insn(Opcodes.LSHR)
            BinaryOperation.ShrU -> emit.insn(Opcodes.LUSHR)
            else -> unsupported(form)
        }
        NumberType.F32 -> when (form.operation) {
            BinaryOperation.Add -> emit.insn(Opcodes.FADD)
            BinaryOperation.Sub -> emit.insn(Opcodes.FSUB)
            BinaryOperation.Mul -> emit.insn(Opcodes.FMUL)
            BinaryOperation.Div -> emit.insn(Opcodes.FDIV)
            BinaryOperation.Min -> emit.invokeStatic(MATH_MIN_FLOAT)
            BinaryOperation.Max -> emit.invokeStatic(MATH_MAX_FLOAT)
            BinaryOperation.Copysign -> copySignFloat()
            else -> unsupported(form)
        }
        NumberType.F64 -> when (form.operation) {
            BinaryOperation.Add -> emit.insn(Opcodes.DADD)
            BinaryOperation.Sub -> emit.insn(Opcodes.DSUB)
            BinaryOperation.Mul -> emit.insn(Opcodes.DMUL)
            BinaryOperation.Div -> emit.insn(Opcodes.DDIV)
            BinaryOperation.Min -> emit.invokeStatic(MATH_MIN_DOUBLE)
            BinaryOperation.Max -> emit.invokeStatic(MATH_MAX_DOUBLE)
            BinaryOperation.Copysign -> copySignDouble()
            else -> unsupported(form)
        }
        else -> unsupported(form)
    }
    store(type, form.destination)
}

private fun NumericForm.Binary.constantDivisor(): Long? {
    return (right as? Operand.Immediate)?.bits?.let { bits -> if (type == NumberType.I64) bits else bits.toInt().toLong() }
}

private fun LowerContext.divide(
    form: NumericForm.Binary,
    divisor: Long?,
) {

    val operation = form.operation
    val type = form.type
    val wide = type == NumberType.I64

    if (divisor == null) {
        trapOnZeroDivisor(type, form.right)
    }
    if (operation == BinaryOperation.DivS && (divisor == null || divisor == -1L)) {
        trapOnDivisionOverflow(form, divisor)
    }
    if (!wide && operation == BinaryOperation.RemU) {
        unsignedIntRemainder(form)
        return
    }

    push(type, form.left)
    push(type, form.right)
    when (operation) {
        BinaryOperation.DivS -> emit.insn(if (wide) Opcodes.LDIV else Opcodes.IDIV)
        BinaryOperation.RemS -> emit.insn(if (wide) Opcodes.LREM else Opcodes.IREM)
        BinaryOperation.DivU -> emit.invokeStatic(if (wide) LONG_DIVIDE_UNSIGNED else INT_DIVIDE_UNSIGNED)
        else -> emit.invokeStatic(LONG_REMAINDER_UNSIGNED)
    }
    store(type, form.destination)
}

private fun LowerContext.trapOnZeroDivisor(
    type: NumberType,
    divisor: Operand,
) {
    push(type, divisor)
    if (type == NumberType.I64) {
        emit.insn(Opcodes.LCONST_0)
        emit.insn(Opcodes.LCMP)
    }
    emit.jump(Opcodes.IFEQ, trapLabel(DIVIDE_BY_ZERO))
}

private fun LowerContext.trapOnDivisionOverflow(
    form: NumericForm.Binary,
    divisor: Long?,
) {

    val type = form.type
    val wide = type == NumberType.I64
    val proceed = emit.label()

    if (divisor == null) {
        push(type, form.right)
        if (wide) {
            emit.long(-1L)
            emit.insn(Opcodes.LCMP)
            emit.jump(Opcodes.IFNE, proceed)
        } else {
            emit.int(-1)
            emit.jump(Opcodes.IF_ICMPNE, proceed)
        }
    }

    push(type, form.left)
    if (wide) {
        emit.long(Long.MIN_VALUE)
        emit.insn(Opcodes.LCMP)
        emit.jump(Opcodes.IFEQ, trapLabel(INTEGER_OVERFLOW))
    } else {
        emit.int(Int.MIN_VALUE)
        emit.jump(Opcodes.IF_ICMPEQ, trapLabel(INTEGER_OVERFLOW))
    }

    if (divisor == null) {
        emit.place(proceed)
    }
}

private fun LowerContext.unsignedIntRemainder(form: NumericForm.Binary) {

    pushInt(form.left)
    emit.insn(Opcodes.I2L)
    emit.long(UNSIGNED_INT_MASK)
    emit.insn(Opcodes.LAND)

    pushInt(form.right)
    emit.insn(Opcodes.I2L)
    emit.long(UNSIGNED_INT_MASK)
    emit.insn(Opcodes.LAND)

    emit.insn(Opcodes.LREM)
    emit.insn(Opcodes.L2I)
    storeInt(form.destination)
}

private fun LowerContext.rotate(form: NumericForm.Binary) {

    val operation = form.operation
    val type = form.type
    val wide = type == NumberType.I64
    val bits = if (wide) Long.SIZE_BITS else Int.SIZE_BITS
    val first = if (operation == BinaryOperation.Rotl) shiftLeftOpcode(wide) else unsignedShiftRightOpcode(wide)
    val second = if (operation == BinaryOperation.Rotl) unsignedShiftRightOpcode(wide) else shiftLeftOpcode(wide)

    val count = form.right
    if (count is Operand.Immediate) {
        val amount = count.bits.mod(bits)
        push(type, form.left)
        if (amount != 0) {
            emit.int(amount)
            emit.insn(first)
            push(type, form.left)
            emit.int(bits - amount)
            emit.insn(second)
            emit.insn(if (wide) Opcodes.LOR else Opcodes.IOR)
        }
    } else {
        push(type, form.left)
        pushInt(count)
        emit.insn(first)
        push(type, form.left)
        pushInt(count)
        emit.insn(Opcodes.INEG)
        emit.insn(second)
        emit.insn(if (wide) Opcodes.LOR else Opcodes.IOR)
    }

    store(type, form.destination)
}

private fun shiftLeftOpcode(wide: Boolean): Int {
    return if (wide) Opcodes.LSHL else Opcodes.ISHL
}

private fun unsignedShiftRightOpcode(wide: Boolean): Int {
    return if (wide) Opcodes.LUSHR else Opcodes.IUSHR
}

private fun LowerContext.copySignFloat() {
    emit.invokeStatic(FLOAT_TO_RAW_INT_BITS)
    emit.int(Int.MIN_VALUE)
    emit.insn(Opcodes.IAND)
    emit.insn(Opcodes.SWAP)
    emit.invokeStatic(FLOAT_TO_RAW_INT_BITS)
    emit.int(Int.MAX_VALUE)
    emit.insn(Opcodes.IAND)
    emit.insn(Opcodes.IOR)
    emit.invokeStatic(INT_BITS_TO_FLOAT)
}

private fun LowerContext.copySignDouble() {
    emit.invokeStatic(DOUBLE_TO_RAW_LONG_BITS)
    emit.long(Long.MIN_VALUE)
    emit.insn(Opcodes.LAND)
    emit.store(Opcodes.LSTORE, locals.scratchLong[0])
    emit.invokeStatic(DOUBLE_TO_RAW_LONG_BITS)
    emit.long(Long.MAX_VALUE)
    emit.insn(Opcodes.LAND)
    emit.load(Opcodes.LLOAD, locals.scratchLong[0])
    emit.insn(Opcodes.LOR)
    emit.invokeStatic(LONG_BITS_TO_DOUBLE)
}

private const val UNSIGNED_INT_MASK = 0xFFFF_FFFFL

private fun BinaryOperation.takesIntCount(): Boolean {
    return this == BinaryOperation.Shl || this == BinaryOperation.ShrS || this == BinaryOperation.ShrU
}

private fun LowerContext.compare(form: NumericForm.Compare) {
    booleanResult(form.destination) { holds ->
        branchCompare(form.type, form.comparison, form.left, form.right, holds, negated = false)
    }
}

private fun LowerContext.wide(
    form: NumericForm.Wide,
) {
    val (a, b, c, d) = locals.scratchLong
    when (form.operation) {
        WideOperation.Add128, WideOperation.Sub128 -> {
            val add = form.operation == WideOperation.Add128
            pushLong(form.operands[0])
            emit.store(Opcodes.LSTORE, a)
            pushLong(form.operands[2])
            emit.store(Opcodes.LSTORE, b)
            pushLong(form.operands[1])
            pushLong(form.operands[3])
            emit.insn(if (add) Opcodes.LADD else Opcodes.LSUB)
            emit.store(Opcodes.LSTORE, c)
            emit.load(Opcodes.LLOAD, a)
            emit.load(Opcodes.LLOAD, b)
            emit.insn(if (add) Opcodes.LADD else Opcodes.LSUB)
            emit.store(Opcodes.LSTORE, d)
            emit.load(Opcodes.LLOAD, c)
            if (add) {
                flippedLong { emit.load(Opcodes.LLOAD, d) }
                flippedLong { emit.load(Opcodes.LLOAD, a) }
            } else {
                flippedLong { emit.load(Opcodes.LLOAD, a) }
                flippedLong { emit.load(Opcodes.LLOAD, b) }
            }
            emit.insn(Opcodes.LCMP)
            emit.int(31)
            emit.insn(Opcodes.IUSHR)
            emit.insn(Opcodes.I2L)
            emit.insn(if (add) Opcodes.LADD else Opcodes.LSUB)
            emit.store(Opcodes.LSTORE, c)
            emit.load(Opcodes.LLOAD, d)
            storeLong(form.low)
            emit.load(Opcodes.LLOAD, c)
            storeLong(form.high)
        }
        WideOperation.MulWideS, WideOperation.MulWideU -> {
            pushLong(form.operands[0])
            emit.store(Opcodes.LSTORE, a)
            pushLong(form.operands[1])
            emit.store(Opcodes.LSTORE, b)
            emit.load(Opcodes.LLOAD, a)
            emit.load(Opcodes.LLOAD, b)
            emit.invokeStatic(if (form.operation == WideOperation.MulWideS) I64_MUL_WIDE_HIGH_S else I64_MUL_WIDE_HIGH_U)
            emit.store(Opcodes.LSTORE, c)
            emit.load(Opcodes.LLOAD, a)
            emit.load(Opcodes.LLOAD, b)
            emit.insn(Opcodes.LMUL)
            storeLong(form.low)
            emit.load(Opcodes.LLOAD, c)
            storeLong(form.high)
        }
    }
}

internal fun unsupported(
    form: Any,
): Nothing = throw Unsupported("${form::class.simpleName}")
