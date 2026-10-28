package io.github.charlietap.chasm.executor.invoker.dispatch.numeric

import io.github.charlietap.chasm.executor.invoker.dispatch.dispatchInstruction
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF64BinaryIi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF64BinaryIs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF64BinarySi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF64BinarySs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF64RelopIi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF64RelopIs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF64RelopSi
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.binop.executeF64RelopSs
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeF32ToF64I
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeF32ToF64S
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeI32ToF64I
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeI32ToF64S
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeI64ToF64I
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.cvtop.executeI64ToF64S
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeF64UnaryI
import io.github.charlietap.chasm.executor.invoker.instruction.numeric.unop.executeF64UnaryS
import io.github.charlietap.chasm.runtime.operations.ext.ceil
import io.github.charlietap.chasm.runtime.operations.ext.convertF64s
import io.github.charlietap.chasm.runtime.operations.ext.convertF64u
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

fun F64AddIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left + right }
}

fun F64AddIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left + right }
}

fun F64AddSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left + right }
}

fun F64AddSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left + right }
}

fun F64SubIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left - right }
}

fun F64SubIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left - right }
}

fun F64SubSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left - right }
}

fun F64SubSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left - right }
}

fun F64MulIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left * right }
}

fun F64MulIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left * right }
}

fun F64MulSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left * right }
}

fun F64MulSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left * right }
}

fun F64DivIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIi(vstack, destinationSlot, left, right) { left, right -> left / right }
}

fun F64DivIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIs(vstack, destinationSlot, left, rightSlot) { left, right -> left / right }
}

fun F64DivSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySi(vstack, destinationSlot, leftSlot, right) { left, right -> left / right }
}

fun F64DivSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySs(vstack, destinationSlot, leftSlot, rightSlot) { left, right -> left / right }
}

fun F64MinIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIi(vstack, destinationSlot, left, right, Double::min)
}

fun F64MinIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIs(vstack, destinationSlot, left, rightSlot, Double::min)
}

fun F64MinSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySi(vstack, destinationSlot, leftSlot, right, Double::min)
}

fun F64MinSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySs(vstack, destinationSlot, leftSlot, rightSlot, Double::min)
}

fun F64MaxIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIi(vstack, destinationSlot, left, right, Double::max)
}

fun F64MaxIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIs(vstack, destinationSlot, left, rightSlot, Double::max)
}

fun F64MaxSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySi(vstack, destinationSlot, leftSlot, right, Double::max)
}

fun F64MaxSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySs(vstack, destinationSlot, leftSlot, rightSlot, Double::max)
}

fun F64CopysignIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIi(vstack, destinationSlot, left, right, Double::copySign)
}

fun F64CopysignIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinaryIs(vstack, destinationSlot, left, rightSlot, Double::copySign)
}

fun F64CopysignSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySi(vstack, destinationSlot, leftSlot, right, Double::copySign)
}

fun F64CopysignSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64BinarySs(vstack, destinationSlot, leftSlot, rightSlot, Double::copySign)
}

fun F64AbsIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryI(vstack, destinationSlot, operand) { operand -> operand.absoluteValue }
}

fun F64AbsSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryS(vstack, destinationSlot, operandSlot) { operand -> operand.absoluteValue }
}

fun F64NegIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryI(vstack, destinationSlot, operand) { operand -> -operand }
}

fun F64NegSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryS(vstack, destinationSlot, operandSlot) { operand -> -operand }
}

fun F64CeilIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryI(vstack, destinationSlot, operand, Double::ceil)
}

fun F64CeilSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryS(vstack, destinationSlot, operandSlot, Double::ceil)
}

fun F64FloorIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryI(vstack, destinationSlot, operand, Double::floor)
}

fun F64FloorSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryS(vstack, destinationSlot, operandSlot, Double::floor)
}

fun F64TruncIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryI(vstack, destinationSlot, operand, Double::trunc)
}

fun F64TruncSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryS(vstack, destinationSlot, operandSlot, Double::trunc)
}

fun F64NearestIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryI(vstack, destinationSlot, operand, Double::nearest)
}

fun F64NearestSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryS(vstack, destinationSlot, operandSlot, Double::nearest)
}

fun F64SqrtIDispatcher(
    operand: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryI(vstack, destinationSlot, operand, Double::sqrt)
}

fun F64SqrtSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64UnaryS(vstack, destinationSlot, operandSlot, Double::sqrt)
}

fun F64EqIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopIi(vstack, destinationSlot, left, right, Double::eq)
}

fun F64EqIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopIs(vstack, destinationSlot, left, rightSlot, Double::eq)
}

fun F64EqSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopSi(vstack, destinationSlot, leftSlot, right, Double::eq)
}

fun F64EqSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopSs(vstack, destinationSlot, leftSlot, rightSlot, Double::eq)
}

fun F64NeIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopIi(vstack, destinationSlot, left, right, Double::ne)
}

fun F64NeIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopIs(vstack, destinationSlot, left, rightSlot, Double::ne)
}

fun F64NeSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopSi(vstack, destinationSlot, leftSlot, right, Double::ne)
}

fun F64NeSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopSs(vstack, destinationSlot, leftSlot, rightSlot, Double::ne)
}

fun F64LtIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopIi(vstack, destinationSlot, left, right, Double::lt)
}

fun F64LtIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopIs(vstack, destinationSlot, left, rightSlot, Double::lt)
}

fun F64LtSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopSi(vstack, destinationSlot, leftSlot, right, Double::lt)
}

fun F64LtSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopSs(vstack, destinationSlot, leftSlot, rightSlot, Double::lt)
}

fun F64GtIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopIi(vstack, destinationSlot, left, right, Double::gt)
}

fun F64GtIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopIs(vstack, destinationSlot, left, rightSlot, Double::gt)
}

fun F64GtSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopSi(vstack, destinationSlot, leftSlot, right, Double::gt)
}

fun F64GtSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopSs(vstack, destinationSlot, leftSlot, rightSlot, Double::gt)
}

fun F64LeIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopIi(vstack, destinationSlot, left, right, Double::le)
}

fun F64LeIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopIs(vstack, destinationSlot, left, rightSlot, Double::le)
}

fun F64LeSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopSi(vstack, destinationSlot, leftSlot, right, Double::le)
}

fun F64LeSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopSs(vstack, destinationSlot, leftSlot, rightSlot, Double::le)
}

fun F64GeIiDispatcher(
    left: Double,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopIi(vstack, destinationSlot, left, right, Double::ge)
}

fun F64GeIsDispatcher(
    left: Double,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopIs(vstack, destinationSlot, left, rightSlot, Double::ge)
}

fun F64GeSiDispatcher(
    leftSlot: Int,
    right: Double,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopSi(vstack, destinationSlot, leftSlot, right, Double::ge)
}

fun F64GeSsDispatcher(
    leftSlot: Int,
    rightSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF64RelopSs(vstack, destinationSlot, leftSlot, rightSlot, Double::ge)
}

fun F64ConvertI32SIDispatcher(
    operand: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32ToF64I(vstack, destinationSlot, operand, Int::convertF64s)
}

fun F64ConvertI32SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32ToF64S(vstack, destinationSlot, operandSlot, Int::convertF64s)
}

fun F64ConvertI32UIDispatcher(
    operand: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32ToF64I(vstack, destinationSlot, operand, Int::convertF64u)
}

fun F64ConvertI32USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI32ToF64S(vstack, destinationSlot, operandSlot, Int::convertF64u)
}

fun F64ConvertI64SIDispatcher(
    operand: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64ToF64I(vstack, destinationSlot, operand, Long::convertF64s)
}

fun F64ConvertI64SSDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64ToF64S(vstack, destinationSlot, operandSlot, Long::convertF64s)
}

fun F64ConvertI64UIDispatcher(
    operand: Long,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64ToF64I(vstack, destinationSlot, operand, Long::convertF64u)
}

fun F64ConvertI64USDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeI64ToF64S(vstack, destinationSlot, operandSlot, Long::convertF64u)
}

fun F64PromoteF32IDispatcher(
    operand: Float,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToF64I(vstack, destinationSlot, operand) { operand -> operand.toDouble() }
}

fun F64PromoteF32SDispatcher(
    operandSlot: Int,
    destinationSlot: Int,
) = dispatchInstruction { vstack, context ->
    executeF32ToF64S(vstack, destinationSlot, operandSlot) { operand -> operand.toDouble() }
}
