package io.github.charlietap.chasm.compiler.backend.jvm.lower

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
        UnaryOperation.Abs, UnaryOperation.Neg -> return signBit(form)
        UnaryOperation.Extend32S -> {
            pushLong(form.operand)
            emit.insn(Opcodes.L2I)
            emit.insn(Opcodes.I2L)
            return storeLong(form.destination)
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

private fun LowerContext.signBit(
    form: NumericForm.Unary,
) {
    val abs = form.operation == UnaryOperation.Abs
    when (form.type) {
        NumberType.F32 -> {
            pushFloat(form.operand)
            emit.invokeStatic(FLOAT_TO_RAW_INT_BITS)
            emit.int(if (abs) Int.MAX_VALUE else Int.MIN_VALUE)
            emit.insn(if (abs) Opcodes.IAND else Opcodes.IXOR)
            emit.invokeStatic(INT_BITS_TO_FLOAT)
            storeFloat(form.destination)
        }
        NumberType.F64 -> {
            pushDouble(form.operand)
            emit.invokeStatic(DOUBLE_TO_RAW_LONG_BITS)
            emit.long(if (abs) Long.MAX_VALUE else Long.MIN_VALUE)
            emit.insn(if (abs) Opcodes.LAND else Opcodes.LXOR)
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
    push(type, form.left)
    if (type == NumberType.I64 && form.operation.takesIntCount()) {
        pushLong(form.right)
        emit.insn(Opcodes.L2I)
    } else {
        push(type, form.right)
    }
    when (type) {
        NumberType.I32 -> when (form.operation) {
            BinaryOperation.Add -> emit.insn(Opcodes.IADD)
            BinaryOperation.Sub -> emit.insn(Opcodes.ISUB)
            BinaryOperation.Mul -> emit.insn(Opcodes.IMUL)
            BinaryOperation.DivS -> emit.invokeStatic(I32_DIV_S)
            BinaryOperation.DivU -> emit.invokeStatic(I32_DIV_U)
            BinaryOperation.RemS -> emit.invokeStatic(I32_REM_S)
            BinaryOperation.RemU -> emit.invokeStatic(I32_REM_U)
            BinaryOperation.And -> emit.insn(Opcodes.IAND)
            BinaryOperation.Or -> emit.insn(Opcodes.IOR)
            BinaryOperation.Xor -> emit.insn(Opcodes.IXOR)
            BinaryOperation.Shl -> emit.insn(Opcodes.ISHL)
            BinaryOperation.ShrS -> emit.insn(Opcodes.ISHR)
            BinaryOperation.ShrU -> emit.insn(Opcodes.IUSHR)
            BinaryOperation.Rotl -> emit.invokeStatic(INT_ROTATE_LEFT)
            BinaryOperation.Rotr -> emit.invokeStatic(INT_ROTATE_RIGHT)
            else -> unsupported(form)
        }
        NumberType.I64 -> when (form.operation) {
            BinaryOperation.Add -> emit.insn(Opcodes.LADD)
            BinaryOperation.Sub -> emit.insn(Opcodes.LSUB)
            BinaryOperation.Mul -> emit.insn(Opcodes.LMUL)
            BinaryOperation.DivS -> emit.invokeStatic(I64_DIV_S)
            BinaryOperation.DivU -> emit.invokeStatic(I64_DIV_U)
            BinaryOperation.RemS -> emit.invokeStatic(I64_REM_S)
            BinaryOperation.RemU -> emit.invokeStatic(I64_REM_U)
            BinaryOperation.And -> emit.insn(Opcodes.LAND)
            BinaryOperation.Or -> emit.insn(Opcodes.LOR)
            BinaryOperation.Xor -> emit.insn(Opcodes.LXOR)
            BinaryOperation.Shl -> emit.insn(Opcodes.LSHL)
            BinaryOperation.ShrS -> emit.insn(Opcodes.LSHR)
            BinaryOperation.ShrU -> emit.insn(Opcodes.LUSHR)
            BinaryOperation.Rotl -> emit.invokeStatic(LONG_ROTATE_LEFT)
            BinaryOperation.Rotr -> emit.invokeStatic(LONG_ROTATE_RIGHT)
            else -> unsupported(form)
        }
        NumberType.F32 -> when (form.operation) {
            BinaryOperation.Add -> emit.insn(Opcodes.FADD)
            BinaryOperation.Sub -> emit.insn(Opcodes.FSUB)
            BinaryOperation.Mul -> emit.insn(Opcodes.FMUL)
            BinaryOperation.Div -> emit.insn(Opcodes.FDIV)
            BinaryOperation.Min -> emit.invokeStatic(F32_MIN)
            BinaryOperation.Max -> emit.invokeStatic(F32_MAX)
            BinaryOperation.Copysign -> emit.invokeStatic(F32_COPYSIGN)
            else -> unsupported(form)
        }
        NumberType.F64 -> when (form.operation) {
            BinaryOperation.Add -> emit.insn(Opcodes.DADD)
            BinaryOperation.Sub -> emit.insn(Opcodes.DSUB)
            BinaryOperation.Mul -> emit.insn(Opcodes.DMUL)
            BinaryOperation.Div -> emit.insn(Opcodes.DDIV)
            BinaryOperation.Min -> emit.invokeStatic(F64_MIN)
            BinaryOperation.Max -> emit.invokeStatic(F64_MAX)
            BinaryOperation.Copysign -> emit.invokeStatic(F64_COPYSIGN)
            else -> unsupported(form)
        }
        else -> unsupported(form)
    }
    store(type, form.destination)
}

private fun BinaryOperation.takesIntCount() = this == BinaryOperation.Shl || this == BinaryOperation.ShrS ||
    this == BinaryOperation.ShrU || this == BinaryOperation.Rotl || this == BinaryOperation.Rotr

private fun LowerContext.compare(
    form: NumericForm.Compare,
) {
    val holds = emit.label()
    val done = emit.label()
    branchCompare(form.type, form.comparison, form.left, form.right, holds, negated = false)
    emit.int(0)
    emit.jump(Opcodes.GOTO, done)
    emit.place(holds)
    emit.int(1)
    emit.place(done, Opcodes.INTEGER)
    storeInt(form.destination)
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

private val MATH_CEIL = MethodRef("Ljava/lang/Math;", "ceil", listOf("D"), "D")
private val MATH_FLOOR = MethodRef("Ljava/lang/Math;", "floor", listOf("D"), "D")
private val MATH_RINT = MethodRef("Ljava/lang/Math;", "rint", listOf("D"), "D")
private val MATH_SQRT = MethodRef("Ljava/lang/Math;", "sqrt", listOf("D"), "D")
