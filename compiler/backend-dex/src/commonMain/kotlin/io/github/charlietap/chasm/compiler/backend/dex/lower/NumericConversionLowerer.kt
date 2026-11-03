package io.github.charlietap.chasm.compiler.backend.dex.lower

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
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.dexter.BinaryOp
import io.github.charlietap.dexter.CompareOp
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion as DexConversion

internal fun LowerContext.lowerConversion(
    conversion: Conversion,
    operand: Operand,
    destination: Int,
) {
    when (conversion) {
        Conversion.I64ExtendI32S -> return copyToSlot(destination, operand)
        Conversion.I64ExtendI32U -> {
            val source = wideSource(operand, Registers.W0)
            emit.constWide(Registers.W1, 0xFFFF_FFFFL)
            val target = wideTarget(destination)
            emit.binary(BinaryOp.AndLong, target, source, Registers.W1)
            return finishWide(destination, target)
        }
        Conversion.I32WrapI64 -> {
            loadWide(Registers.W0, operand)
            emit.convert(DexConversion.LongToInt, Registers.I0, Registers.W0)
            return storeInt(destination, Registers.I0)
        }
        else -> Unit
    }
    when (conversion.source) {
        NumberType.I32 -> loadInt(Registers.I0, Registers.W0, operand)
        NumberType.F32 -> loadFloat(Registers.I0, Registers.W0, operand)
        NumberType.I64 -> loadWide(Registers.W0, operand)
        NumberType.F64 -> loadDouble(Registers.W0, operand)
    }
    val narrowOperand = conversion.source == NumberType.I32 || conversion.source == NumberType.F32
    val source = if (narrowOperand) intArrayOf(Registers.I0) else intArrayOf(Registers.W0, Registers.W0 + 1)
    val wideResult = conversion.result == NumberType.I64 || conversion.result == NumberType.F64
    val result = if (wideResult) Registers.W2 else Registers.I2
    val direct = directConversion(conversion)
    val truncation = truncation(conversion)
    when {
        direct != null -> emit.convert(direct, result, source[0])
        conversion == Conversion.F32ConvertI32U || conversion == Conversion.F64ConvertI32U -> {
            emit.convert(DexConversion.IntToLong, Registers.W1, Registers.I0)
            emit.constWide(Registers.W3, 0xFFFF_FFFFL)
            emit.binary(BinaryOp.AndLong, Registers.W1, Registers.W1, Registers.W3)
            val convert = if (conversion == Conversion.F32ConvertI32U) DexConversion.LongToFloat else DexConversion.LongToDouble
            emit.convert(convert, result, Registers.W1)
        }
        truncation != null -> truncate(truncation, conversionHelper(conversion), source, result, wideResult)
        else -> {
            emit.invoke(conversionHelper(conversion), *source)
            if (wideResult) emit.moveResultWide(result) else emit.moveResult(result)
        }
    }
    when (conversion.result) {
        NumberType.I32 -> storeInt(destination, result)
        NumberType.I64 -> store(destination, result)
        NumberType.F32 -> storeFloat(destination, result)
        NumberType.F64 -> storeDouble(destination, result)
    }
}

private class Truncation(
    val low: Double,
    val inclusive: Boolean,
    val high: Double,
    val double: Boolean,
    val convert: DexConversion,
    val narrow: Boolean = false,
)

private fun truncation(
    conversion: Conversion,
): Truncation? = when (conversion) {
    Conversion.I32TruncF32S -> Truncation(-TWO_31, inclusive = true, TWO_31, double = false, DexConversion.FloatToInt)
    Conversion.I32TruncF32U -> Truncation(-1.0, inclusive = false, TWO_32, double = false, DexConversion.FloatToLong, narrow = true)
    Conversion.I32TruncF64S -> Truncation(-TWO_31 - 1, inclusive = false, TWO_31, double = true, DexConversion.DoubleToInt)
    Conversion.I32TruncF64U -> Truncation(-1.0, inclusive = false, TWO_32, double = true, DexConversion.DoubleToLong, narrow = true)
    Conversion.I64TruncF32S -> Truncation(-TWO_63, inclusive = true, TWO_63, double = false, DexConversion.FloatToLong)
    Conversion.I64TruncF64S -> Truncation(-TWO_63, inclusive = true, TWO_63, double = true, DexConversion.DoubleToLong)
    else -> null
}

private fun LowerContext.truncate(
    truncation: Truncation,
    helper: MethodRef,
    source: IntArray,
    result: Int,
    wideResult: Boolean,
) {
    val trap = emit.freshLabel()
    val done = emit.freshLabel()
    bound(truncation, truncation.low, if (truncation.double) CompareOp.CmplDouble else CompareOp.CmplFloat)
    emit.branchZero(if (truncation.inclusive) Cond.Lt else Cond.Le, Registers.I3, trap)
    bound(truncation, truncation.high, if (truncation.double) CompareOp.CmpgDouble else CompareOp.CmpgFloat)
    emit.branchZero(Cond.Ge, Registers.I3, trap)
    if (truncation.narrow) {
        emit.convert(truncation.convert, Registers.W2, source[0])
        emit.convert(DexConversion.LongToInt, result, Registers.W2)
    } else {
        emit.convert(truncation.convert, result, source[0])
    }
    emit.place(done)
    val origin = position
    outOfLine += {
        emit.place(trap)
        if (module.options.stacktraces) emit.line(line(origin))
        emit.invoke(helper, *source)
        if (wideResult) emit.moveResultWide(result) else emit.moveResult(result)
        emit.gotoLabel(done)
    }
}

private fun LowerContext.bound(
    truncation: Truncation,
    value: Double,
    compare: CompareOp,
) {
    if (truncation.double) {
        emit.constWide(Registers.W1, value.toRawBits())
        emit.compare(compare, Registers.I3, Registers.W0, Registers.W1)
    } else {
        emit.const(Registers.I1, value.toFloat().toRawBits())
        emit.compare(compare, Registers.I3, Registers.I0, Registers.I1)
    }
}

private const val TWO_31 = 2147483648.0
private const val TWO_32 = 4294967296.0
private const val TWO_63 = 9.223372036854775808E18

private fun directConversion(
    conversion: Conversion,
): DexConversion? = when (conversion) {
    Conversion.I32TruncSatF32S -> DexConversion.FloatToInt
    Conversion.I32TruncSatF64S -> DexConversion.DoubleToInt
    Conversion.I64TruncSatF32S -> DexConversion.FloatToLong
    Conversion.I64TruncSatF64S -> DexConversion.DoubleToLong
    Conversion.F32ConvertI32S -> DexConversion.IntToFloat
    Conversion.F32ConvertI64S -> DexConversion.LongToFloat
    Conversion.F64ConvertI32S -> DexConversion.IntToDouble
    Conversion.F64ConvertI64S -> DexConversion.LongToDouble
    Conversion.F64PromoteF32 -> DexConversion.FloatToDouble
    Conversion.F32DemoteF64 -> DexConversion.DoubleToFloat
    else -> null
}

private fun conversionHelper(
    conversion: Conversion,
): MethodRef = when (conversion) {
    Conversion.I32TruncF32S -> I32_TRUNC_F32_S
    Conversion.I32TruncF32U -> I32_TRUNC_F32_U
    Conversion.I32TruncF64S -> I32_TRUNC_F64_S
    Conversion.I32TruncF64U -> I32_TRUNC_F64_U
    Conversion.I32TruncSatF32U -> I32_TRUNC_SAT_F32_U
    Conversion.I32TruncSatF64U -> I32_TRUNC_SAT_F64_U
    Conversion.I64TruncF32S -> I64_TRUNC_F32_S
    Conversion.I64TruncF32U -> I64_TRUNC_F32_U
    Conversion.I64TruncF64S -> I64_TRUNC_F64_S
    Conversion.I64TruncF64U -> I64_TRUNC_F64_U
    Conversion.I64TruncSatF32U -> I64_TRUNC_SAT_F32_U
    Conversion.I64TruncSatF64U -> I64_TRUNC_SAT_F64_U
    Conversion.F32ConvertI64U -> F32_CONVERT_I64_U
    Conversion.F64ConvertI64U -> F64_CONVERT_I64_U
    else -> throw Unsupported("conversion $conversion")
}
