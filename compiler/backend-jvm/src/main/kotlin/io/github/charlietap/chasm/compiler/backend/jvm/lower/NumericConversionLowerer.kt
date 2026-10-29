package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.F32_CONVERT_I64_U
import io.github.charlietap.chasm.compiler.backend.abi.F64_CONVERT_I64_U
import io.github.charlietap.chasm.compiler.backend.abi.I32_TRUNC_F32_S
import io.github.charlietap.chasm.compiler.backend.abi.I32_TRUNC_F32_U
import io.github.charlietap.chasm.compiler.backend.abi.I32_TRUNC_F64_S
import io.github.charlietap.chasm.compiler.backend.abi.I32_TRUNC_F64_U
import io.github.charlietap.chasm.compiler.backend.abi.I32_TRUNC_SAT_F32_U
import io.github.charlietap.chasm.compiler.backend.abi.I32_TRUNC_SAT_F64_U
import io.github.charlietap.chasm.compiler.backend.abi.I64_TRUNC_F32_S
import io.github.charlietap.chasm.compiler.backend.abi.I64_TRUNC_F32_U
import io.github.charlietap.chasm.compiler.backend.abi.I64_TRUNC_F64_S
import io.github.charlietap.chasm.compiler.backend.abi.I64_TRUNC_F64_U
import io.github.charlietap.chasm.compiler.backend.abi.I64_TRUNC_SAT_F32_U
import io.github.charlietap.chasm.compiler.backend.abi.I64_TRUNC_SAT_F64_U
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.analysis.form.Conversion
import io.github.charlietap.chasm.compiler.backend.analysis.form.NumericForm
import org.objectweb.asm.Opcodes

internal fun LowerContext.convert(
    form: NumericForm.Convert,
) {
    val conversion = form.conversion
    push(conversion.source, form.operand)
    when (conversion) {
        Conversion.I32WrapI64 -> emit.insn(Opcodes.L2I)
        Conversion.I64ExtendI32S -> emit.insn(Opcodes.I2L)
        Conversion.I64ExtendI32U -> zeroExtend()
        Conversion.I32TruncSatF32S -> emit.insn(Opcodes.F2I)
        Conversion.I32TruncSatF64S -> emit.insn(Opcodes.D2I)
        Conversion.I64TruncSatF32S -> emit.insn(Opcodes.F2L)
        Conversion.I64TruncSatF64S -> emit.insn(Opcodes.D2L)
        Conversion.I32TruncSatF32U -> emit.invokeStatic(I32_TRUNC_SAT_F32_U)
        Conversion.I32TruncSatF64U -> emit.invokeStatic(I32_TRUNC_SAT_F64_U)
        Conversion.I64TruncSatF32U -> emit.invokeStatic(I64_TRUNC_SAT_F32_U)
        Conversion.I64TruncSatF64U -> emit.invokeStatic(I64_TRUNC_SAT_F64_U)
        Conversion.I32TruncF32S -> trunc(Range(-TWO_31, inclusive = true, TWO_31), double = false, Opcodes.F2I, I32_TRUNC_F32_S)
        Conversion.I32TruncF32U -> trunc(Range(-1.0, inclusive = false, TWO_32), double = false, Opcodes.F2L, I32_TRUNC_F32_U, Opcodes.L2I)
        Conversion.I32TruncF64S -> trunc(Range(-TWO_31 - 1, inclusive = false, TWO_31), double = true, Opcodes.D2I, I32_TRUNC_F64_S)
        Conversion.I32TruncF64U -> trunc(Range(-1.0, inclusive = false, TWO_32), double = true, Opcodes.D2L, I32_TRUNC_F64_U, Opcodes.L2I)
        Conversion.I64TruncF32S -> trunc(Range(-TWO_63, inclusive = true, TWO_63), double = false, Opcodes.F2L, I64_TRUNC_F32_S)
        Conversion.I64TruncF32U -> emit.invokeStatic(I64_TRUNC_F32_U)
        Conversion.I64TruncF64S -> trunc(Range(-TWO_63, inclusive = true, TWO_63), double = true, Opcodes.D2L, I64_TRUNC_F64_S)
        Conversion.I64TruncF64U -> emit.invokeStatic(I64_TRUNC_F64_U)
        Conversion.F32ConvertI32S -> emit.insn(Opcodes.I2F)
        Conversion.F64ConvertI32S -> emit.insn(Opcodes.I2D)
        Conversion.F32ConvertI64S -> emit.insn(Opcodes.L2F)
        Conversion.F64ConvertI64S -> emit.insn(Opcodes.L2D)
        Conversion.F32ConvertI32U -> {
            zeroExtend()
            emit.insn(Opcodes.L2F)
        }
        Conversion.F64ConvertI32U -> {
            zeroExtend()
            emit.insn(Opcodes.L2D)
        }
        Conversion.F32ConvertI64U -> emit.invokeStatic(F32_CONVERT_I64_U)
        Conversion.F64ConvertI64U -> emit.invokeStatic(F64_CONVERT_I64_U)
        Conversion.F32DemoteF64 -> emit.insn(Opcodes.D2F)
        Conversion.F64PromoteF32 -> emit.insn(Opcodes.F2D)
    }
    store(conversion.result, form.destination)
}

private class Range(
    val low: Double,
    val inclusive: Boolean,
    val high: Double,
)

private fun LowerContext.trunc(
    range: Range,
    double: Boolean,
    convert: Int,
    helper: MethodRef,
    narrow: Int? = null,
) {
    val trap = emit.label()
    val done = emit.label()
    val dup = if (double) Opcodes.DUP2 else Opcodes.DUP
    emit.insn(dup)
    constant(range.low, double)
    emit.insn(if (double) Opcodes.DCMPL else Opcodes.FCMPL)
    emit.jump(if (range.inclusive) Opcodes.IFLT else Opcodes.IFLE, trap)
    emit.insn(dup)
    constant(range.high, double)
    emit.insn(if (double) Opcodes.DCMPG else Opcodes.FCMPG)
    emit.jump(Opcodes.IFGE, trap)
    emit.insn(convert)
    if (narrow != null) emit.insn(narrow)
    val result = if (helper.returns == "J") Opcodes.LONG else Opcodes.INTEGER
    emit.place(done, result)
    outOfLine += {
        emit.place(trap, if (double) Opcodes.DOUBLE else Opcodes.FLOAT)
        emit.invokeStatic(helper)
        emit.jump(Opcodes.GOTO, done)
    }
}

private fun LowerContext.constant(
    value: Double,
    double: Boolean,
) = if (double) emit.double(value.toRawBits()) else emit.float(value.toFloat().toRawBits())

private const val TWO_31 = 2147483648.0
private const val TWO_32 = 4294967296.0
private const val TWO_63 = 9.223372036854775808E18

private fun LowerContext.zeroExtend() {
    emit.insn(Opcodes.I2L)
    emit.long(0xFFFF_FFFFL)
    emit.insn(Opcodes.LAND)
}
