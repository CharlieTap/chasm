package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF32BinaryIi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF32BinaryIs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF32BinarySi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF32BinarySs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF32RelopIi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF32RelopIs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF32RelopSi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF32RelopSs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeF64ToF32I
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeF64ToF32S
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeI32ToF32I
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeI32ToF32S
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeI64ToF32I
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeI64ToF32S
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeF32UnaryI
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeF32UnaryS
import io.github.charlietap.chasm.runtime.operations.ext.ceil
import io.github.charlietap.chasm.runtime.operations.ext.convertF32s
import io.github.charlietap.chasm.runtime.operations.ext.convertF32u
import io.github.charlietap.chasm.runtime.operations.ext.copySign
import io.github.charlietap.chasm.runtime.operations.ext.eq
import io.github.charlietap.chasm.runtime.operations.ext.floor
import io.github.charlietap.chasm.runtime.operations.ext.ge
import io.github.charlietap.chasm.runtime.operations.ext.gt
import io.github.charlietap.chasm.runtime.operations.ext.le
import io.github.charlietap.chasm.runtime.operations.ext.lt
import io.github.charlietap.chasm.runtime.operations.ext.max
import io.github.charlietap.chasm.runtime.operations.ext.min
import io.github.charlietap.chasm.runtime.operations.ext.ne
import io.github.charlietap.chasm.runtime.operations.ext.nearest
import io.github.charlietap.chasm.runtime.operations.ext.sqrt
import io.github.charlietap.chasm.runtime.operations.ext.trunc
import kotlin.math.absoluteValue

fun F32AddIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIi(vstack, destinationSlot, left, right) { left, right -> left + right }
}

fun F32AddIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left + right }
}

fun F32AddSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left + right }
}

fun F32AddSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left + right }
}

fun F32SubIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIi(vstack, destinationSlot, left, right) { left, right -> left - right }
}

fun F32SubIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left - right }
}

fun F32SubSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left - right }
}

fun F32SubSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left - right }
}

fun F32MulIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIi(vstack, destinationSlot, left, right) { left, right -> left * right }
}

fun F32MulIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left * right }
}

fun F32MulSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left * right }
}

fun F32MulSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left * right }
}

fun F32DivIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIi(vstack, destinationSlot, left, right) { left, right -> left / right }
}

fun F32DivIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left / right }
}

fun F32DivSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left / right }
}

fun F32DivSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left / right }
}

fun F32MinIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIi(vstack, destinationSlot, left, right, Float::min)
}

fun F32MinIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIs(vstack, destinationSlot, left, rightSlot, Float::min)
}

fun F32MinSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySi(vstack, destinationSlot, leftSlot, right, Float::min)
}

fun F32MinSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySs(vstack, destinationSlot, leftSlot, rightSlot, Float::min)
}

fun F32MaxIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIi(vstack, destinationSlot, left, right, Float::max)
}

fun F32MaxIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIs(vstack, destinationSlot, left, rightSlot, Float::max)
}

fun F32MaxSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySi(vstack, destinationSlot, leftSlot, right, Float::max)
}

fun F32MaxSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySs(vstack, destinationSlot, leftSlot, rightSlot, Float::max)
}

fun F32CopysignIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIi(vstack, destinationSlot, left, right, Float::copySign)
}

fun F32CopysignIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinaryIs(vstack, destinationSlot, left, rightSlot, Float::copySign)
}

fun F32CopysignSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySi(vstack, destinationSlot, leftSlot, right, Float::copySign)
}

fun F32CopysignSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32BinarySs(vstack, destinationSlot, leftSlot, rightSlot, Float::copySign)
}

fun F32AbsIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryI(vstack, destinationSlot, operand) { operand -> operand.absoluteValue }
}

fun F32AbsSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryS(vstack, destinationSlot, operandSlot) { operand -> operand.absoluteValue }
}

fun F32NegIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryI(vstack, destinationSlot, operand) { operand -> -operand }
}

fun F32NegSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryS(vstack, destinationSlot, operandSlot) { operand -> -operand }
}

fun F32CeilIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryI(vstack, destinationSlot, operand, Float::ceil)
}

fun F32CeilSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryS(vstack, destinationSlot, operandSlot, Float::ceil)
}

fun F32FloorIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryI(vstack, destinationSlot, operand, Float::floor)
}

fun F32FloorSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryS(vstack, destinationSlot, operandSlot, Float::floor)
}

fun F32TruncIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryI(vstack, destinationSlot, operand, Float::trunc)
}

fun F32TruncSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryS(vstack, destinationSlot, operandSlot, Float::trunc)
}

fun F32NearestIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryI(vstack, destinationSlot, operand, Float::nearest)
}

fun F32NearestSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryS(vstack, destinationSlot, operandSlot, Float::nearest)
}

fun F32SqrtIDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryI(vstack, destinationSlot, operand, Float::sqrt)
}

fun F32SqrtSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32UnaryS(vstack, destinationSlot, operandSlot, Float::sqrt)
}

fun F32EqIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopIi(vstack, destinationSlot, left, right, Float::eq)
}

fun F32EqIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopIs(vstack, destinationSlot, left, rightSlot, Float::eq)
}

fun F32EqSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopSi(vstack, destinationSlot, leftSlot, right, Float::eq)
}

fun F32EqSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopSs(vstack, destinationSlot, leftSlot, rightSlot, Float::eq)
}

fun F32NeIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopIi(vstack, destinationSlot, left, right, Float::ne)
}

fun F32NeIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopIs(vstack, destinationSlot, left, rightSlot, Float::ne)
}

fun F32NeSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopSi(vstack, destinationSlot, leftSlot, right, Float::ne)
}

fun F32NeSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopSs(vstack, destinationSlot, leftSlot, rightSlot, Float::ne)
}

fun F32LtIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopIi(vstack, destinationSlot, left, right, Float::lt)
}

fun F32LtIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopIs(vstack, destinationSlot, left, rightSlot, Float::lt)
}

fun F32LtSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopSi(vstack, destinationSlot, leftSlot, right, Float::lt)
}

fun F32LtSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopSs(vstack, destinationSlot, leftSlot, rightSlot, Float::lt)
}

fun F32GtIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopIi(vstack, destinationSlot, left, right, Float::gt)
}

fun F32GtIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopIs(vstack, destinationSlot, left, rightSlot, Float::gt)
}

fun F32GtSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopSi(vstack, destinationSlot, leftSlot, right, Float::gt)
}

fun F32GtSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopSs(vstack, destinationSlot, leftSlot, rightSlot, Float::gt)
}

fun F32LeIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopIi(vstack, destinationSlot, left, right, Float::le)
}

fun F32LeIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopIs(vstack, destinationSlot, left, rightSlot, Float::le)
}

fun F32LeSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopSi(vstack, destinationSlot, leftSlot, right, Float::le)
}

fun F32LeSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopSs(vstack, destinationSlot, leftSlot, rightSlot, Float::le)
}

fun F32GeIiDispatcher(
    left: Float,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopIi(vstack, destinationSlot, left, right, Float::ge)
}

fun F32GeIsDispatcher(
    left: Float,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopIs(vstack, destinationSlot, left, rightSlot, Float::ge)
}

fun F32GeSiDispatcher(
    leftSlot: Int,
    right: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopSi(vstack, destinationSlot, leftSlot, right, Float::ge)
}

fun F32GeSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32RelopSs(vstack, destinationSlot, leftSlot, rightSlot, Float::ge)
}

fun F32ConvertI32SIDispatcher(
    operand: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32ToF32I(vstack, destinationSlot, operand, Int::convertF32s)
}

fun F32ConvertI32SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32ToF32S(vstack, destinationSlot, operandSlot, Int::convertF32s)
}

fun F32ConvertI32UIDispatcher(
    operand: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32ToF32I(vstack, destinationSlot, operand, Int::convertF32u)
}

fun F32ConvertI32USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32ToF32S(vstack, destinationSlot, operandSlot, Int::convertF32u)
}

fun F32ConvertI64SIDispatcher(
    operand: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64ToF32I(vstack, destinationSlot, operand, Long::convertF32s)
}

fun F32ConvertI64SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64ToF32S(vstack, destinationSlot, operandSlot, Long::convertF32s)
}

fun F32ConvertI64UIDispatcher(
    operand: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64ToF32I(vstack, destinationSlot, operand, Long::convertF32u)
}

fun F32ConvertI64USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64ToF32S(vstack, destinationSlot, operandSlot, Long::convertF32u)
}

fun F32DemoteF64IDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToF32I(vstack, destinationSlot, operand) { operand -> operand.toFloat() }
}

fun F32DemoteF64SDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64ToF32S(vstack, destinationSlot, operandSlot) { operand -> operand.toFloat() }
}
