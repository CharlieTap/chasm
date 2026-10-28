package io.github.charlietap.chasm.compiler.frontend.instruction

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.FusedOperand
import io.github.charlietap.chasm.cir.NumericCondition
import io.github.charlietap.chasm.cir.OperandTransfer
import io.github.charlietap.chasm.cir.TagIndex
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSource
import io.github.charlietap.chasm.compiler.frontend.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.frontend.operand.sourceSlot
import io.github.charlietap.chasm.compiler.frontend.program.ProgramTarget
import io.github.charlietap.chasm.cir.ReferenceType as CirReferenceType

internal enum class BranchOutcome {
    Always,
    Never,
    Dynamic,
}

internal fun FunctionCompilationContext.emitFunctionReturn(transfer: SlotTransfer) {
    val destinationSlotBase = if (transfer.size == 0) 0 else transfer.destinationSlot(0)
    emit(
        ControlInstruction.FunctionReturn(
            results = transfer.toOperandTransfer(destinationSlotBase),
            activationHeaderSlot = layout.activationHeaderSlot,
        ),
    )
}

internal fun FunctionCompilationContext.emitJump(
    target: ProgramTarget,
    transfer: SlotTransfer = emptySlotTransfer,
) {
    if (!transfer.isIdentity()) {
        val destinationSlotBase = transfer.destinationSlot(0)
        val operands = transfer.toOperandTransfer(destinationSlotBase)
        append(target) { targetIndex -> AdminInstruction.JumpCopies(operands, targetIndex) }
        return
    }

    append(target, AdminInstruction::Jump)
}

private fun SlotTransfer.toOperandTransfer(
    destinationSlotBase: Int,
): OperandTransfer {
    val values = LongArray(size)
    val immediates = BooleanArray(size)
    for (index in 0 until size) {
        check(destinationSlot(index) == destinationSlotBase + index)
        when (sourceKind(index)) {
            OperandSourceKind.I32Immediate,
            OperandSourceKind.I64Immediate,
            OperandSourceKind.F32Immediate,
            OperandSourceKind.F64Immediate,
            -> {
                values[index] = sourceBits(index)
                immediates[index] = true
            }
            OperandSourceKind.Local -> values[index] = sourceBits(index).toInt().toLong()
            OperandSourceKind.Frame -> values[index] = sourceSlot(index).toLong()
        }
    }
    return OperandTransfer(values, immediates, destinationSlotBase)
}

internal fun FunctionCompilationContext.emitBranchIf(
    condition: OperandSource,
    target: ProgramTarget,
    transfer: SlotTransfer,
    whenZero: Boolean = false,
): BranchOutcome {
    if (condition.sourceKind == OperandSourceKind.I32Immediate) {
        val branch = (condition.sourceBits == 0L) == whenZero
        if (branch) emitJump(target, transfer)
        return if (branch) BranchOutcome.Always else BranchOutcome.Never
    }
    val scalarTransfer = transfer.size == 1 && transfer.sourceKind(0) == OperandSourceKind.Frame &&
        transfer.sourceSlot(0) != transfer.destinationSlot(0) && !whenZero
    val branchTarget = if (scalarTransfer) target else prepareBranchTarget(target, transfer)
    val conditionKind = condition.sourceKind
    val conditionBits = condition.sourceBits
    append(branchTarget) { target ->
        when {
            scalarTransfer && conditionKind.isImmediate -> AdminInstruction.JumpIfCopyI(
                operand = conditionBits,
                sourceSlot = transfer.sourceSlot(0),
                destinationSlot = transfer.destinationSlot(0),
                target = target,
            )
            scalarTransfer -> AdminInstruction.JumpIfCopyS(
                operandSlot = conditionBits.toInt(),
                sourceSlot = transfer.sourceSlot(0),
                destinationSlot = transfer.destinationSlot(0),
                target = target,
            )
            whenZero && conditionKind.isImmediate -> AdminInstruction.JumpIfZeroI(conditionBits, target)
            whenZero -> AdminInstruction.JumpIfZeroS(conditionBits.toInt(), target)
            conditionKind.isImmediate -> AdminInstruction.JumpIfI(conditionBits, target)
            else -> AdminInstruction.JumpIfS(conditionBits.toInt(), target)
        }
    }
    return BranchOutcome.Dynamic
}

internal fun FunctionCompilationContext.emitBranchIf(
    condition: NumericCondition,
    target: ProgramTarget,
    transfer: SlotTransfer,
    branchOnMatch: Boolean = true,
): BranchOutcome {
    val conditionMatches = condition.evaluateOrNull()
    if (conditionMatches != null) {
        val branch = conditionMatches == branchOnMatch
        if (branch) emitJump(target, transfer)
        return if (branch) BranchOutcome.Always else BranchOutcome.Never
    }
    val branchTarget = prepareBranchTarget(target, transfer)
    append(branchTarget) { target ->
        if (branchOnMatch) {
            AdminInstruction.JumpIfCondition(condition, target)
        } else {
            AdminInstruction.JumpIfConditionMismatch(condition, target)
        }
    }
    return BranchOutcome.Dynamic
}

private fun NumericCondition.evaluateOrNull(): Boolean? = when (this) {
    is NumericCondition.I32Eqz -> (operand as? FusedOperand.I32Const)?.const?.let { it == 0 }
    is NumericCondition.I32And -> i32Condition(left, right) { a, b -> (a and b) != 0 }
    is NumericCondition.I64Eqz -> (operand as? FusedOperand.I64Const)?.const?.let { it == 0L }
    is NumericCondition.I32Eq -> i32Condition(left, right) { a, b -> a == b }
    is NumericCondition.I32Ne -> i32Condition(left, right) { a, b -> a != b }
    is NumericCondition.I32LtS -> i32Condition(left, right) { a, b -> a < b }
    is NumericCondition.I32LtU -> i32Condition(left, right) { a, b -> a.toUInt() < b.toUInt() }
    is NumericCondition.I32GtS -> i32Condition(left, right) { a, b -> a > b }
    is NumericCondition.I32GtU -> i32Condition(left, right) { a, b -> a.toUInt() > b.toUInt() }
    is NumericCondition.I32LeS -> i32Condition(left, right) { a, b -> a <= b }
    is NumericCondition.I32LeU -> i32Condition(left, right) { a, b -> a.toUInt() <= b.toUInt() }
    is NumericCondition.I32GeS -> i32Condition(left, right) { a, b -> a >= b }
    is NumericCondition.I32GeU -> i32Condition(left, right) { a, b -> a.toUInt() >= b.toUInt() }
    is NumericCondition.I64Eq -> i64Condition(left, right) { a, b -> a == b }
    is NumericCondition.I64Ne -> i64Condition(left, right) { a, b -> a != b }
    is NumericCondition.I64LtS -> i64Condition(left, right) { a, b -> a < b }
    is NumericCondition.I64LtU -> i64Condition(left, right) { a, b -> a.toULong() < b.toULong() }
    is NumericCondition.I64GtS -> i64Condition(left, right) { a, b -> a > b }
    is NumericCondition.I64GtU -> i64Condition(left, right) { a, b -> a.toULong() > b.toULong() }
    is NumericCondition.I64LeS -> i64Condition(left, right) { a, b -> a <= b }
    is NumericCondition.I64LeU -> i64Condition(left, right) { a, b -> a.toULong() <= b.toULong() }
    is NumericCondition.I64GeS -> i64Condition(left, right) { a, b -> a >= b }
    is NumericCondition.I64GeU -> i64Condition(left, right) { a, b -> a.toULong() >= b.toULong() }
    is NumericCondition.F32Eq -> f32Condition(left, right) { a, b -> a == b }
    is NumericCondition.F32Ne -> f32Condition(left, right) { a, b -> a != b }
    is NumericCondition.F32Lt -> f32Condition(left, right) { a, b -> a < b }
    is NumericCondition.F32Gt -> f32Condition(left, right) { a, b -> a > b }
    is NumericCondition.F32Le -> f32Condition(left, right) { a, b -> a <= b }
    is NumericCondition.F32Ge -> f32Condition(left, right) { a, b -> a >= b }
    is NumericCondition.F64Eq -> f64Condition(left, right) { a, b -> a == b }
    is NumericCondition.F64Ne -> f64Condition(left, right) { a, b -> a != b }
    is NumericCondition.F64Lt -> f64Condition(left, right) { a, b -> a < b }
    is NumericCondition.F64Gt -> f64Condition(left, right) { a, b -> a > b }
    is NumericCondition.F64Le -> f64Condition(left, right) { a, b -> a <= b }
    is NumericCondition.F64Ge -> f64Condition(left, right) { a, b -> a >= b }
}

private inline fun i32Condition(left: FusedOperand, right: FusedOperand, condition: (Int, Int) -> Boolean): Boolean? {
    val leftValue = (left as? FusedOperand.I32Const)?.const ?: return null
    val rightValue = (right as? FusedOperand.I32Const)?.const ?: return null
    return condition(leftValue, rightValue)
}

private inline fun i64Condition(left: FusedOperand, right: FusedOperand, condition: (Long, Long) -> Boolean): Boolean? {
    val leftValue = (left as? FusedOperand.I64Const)?.const ?: return null
    val rightValue = (right as? FusedOperand.I64Const)?.const ?: return null
    return condition(leftValue, rightValue)
}

private inline fun f32Condition(left: FusedOperand, right: FusedOperand, condition: (Float, Float) -> Boolean): Boolean? {
    val leftValue = (left as? FusedOperand.F32Const)?.const ?: return null
    val rightValue = (right as? FusedOperand.F32Const)?.const ?: return null
    return condition(leftValue, rightValue)
}

private inline fun f64Condition(left: FusedOperand, right: FusedOperand, condition: (Double, Double) -> Boolean): Boolean? {
    val leftValue = (left as? FusedOperand.F64Const)?.const ?: return null
    val rightValue = (right as? FusedOperand.F64Const)?.const ?: return null
    return condition(leftValue, rightValue)
}

internal fun FunctionCompilationContext.emitBranchTable(
    selector: OperandSource,
    targetIndices: IntArray,
) {
    val selectorKind = selector.sourceKind
    val selectorBits = selector.sourceBits
    check(selectorKind != OperandSourceKind.I32Immediate)
    append(targetIndices) { targets -> AdminInstruction.JumpTableS(selectorBits.toInt(), targets) }
}

internal fun FunctionCompilationContext.emitBranchOnNull(
    operand: OperandSource,
    target: ProgramTarget,
    transfer: SlotTransfer,
    onNull: Boolean,
) {
    val branchTarget = prepareBranchTarget(target, transfer)
    val immediate = operand.sourceKind.isImmediate
    val operandBits = operand.sourceBits
    append(branchTarget) { target ->
        when {
            onNull && immediate -> AdminInstruction.JumpOnNullI(operandBits, target)
            onNull -> AdminInstruction.JumpOnNullS(operandBits.toInt(), target)
            immediate -> AdminInstruction.JumpOnNonNullI(operandBits, target)
            else -> AdminInstruction.JumpOnNonNullS(operandBits.toInt(), target)
        }
    }
}

internal fun FunctionCompilationContext.emitBranchOnCast(
    operand: OperandSource,
    target: ProgramTarget,
    transfer: SlotTransfer,
    typeTest: CirReferenceType,
    onSuccess: Boolean,
) {
    val branchTarget = prepareBranchTarget(target, transfer)
    val immediate = operand.sourceKind.isImmediate
    val operandBits = operand.sourceBits
    append(branchTarget) { target ->
        when {
            onSuccess && immediate -> AdminInstruction.JumpOnCastI(operandBits, target, typeTest)
            onSuccess -> AdminInstruction.JumpOnCastS(operandBits.toInt(), target, typeTest)
            immediate -> AdminInstruction.JumpOnCastFailI(operandBits, target, typeTest)
            else -> AdminInstruction.JumpOnCastFailS(operandBits.toInt(), target, typeTest)
        }
    }
}

internal fun FunctionCompilationContext.emitThrow(
    tagIndex: Index.TagIndex,
    firstPayloadSlot: Int,
) {
    emit(ControlInstruction.Throw(exceptionTag = TagIndex(tagIndex.toInt()), firstPayloadSlot = firstPayloadSlot))
}

internal fun FunctionCompilationContext.emitThrowRef(exceptionSlot: Int) {
    emit(ControlInstruction.ThrowRefS(exceptionSlot))
}

internal fun FunctionCompilationContext.prepareBranchTarget(
    target: ProgramTarget,
    transfer: SlotTransfer,
): ProgramTarget {
    if (transfer.isIdentity()) return target
    return program.target().also { tail ->
        deferBranchPath(tail, target, transfer)
    }
}

private fun FunctionCompilationContext.deferBranchPath(
    tail: ProgramTarget,
    destination: ProgramTarget,
    transfer: SlotTransfer,
) {
    val paths = deferredBranchPaths ?: DeferredBranchPaths().also { deferredBranchPaths = it }
    paths.add(tail, destination, transfer)
}

internal fun FunctionCompilationContext.emitDeferredBranchPaths() {
    deferredBranchPaths?.emit(this)
    deferredBranchPaths = null
}

internal class DeferredBranchPaths {

    private var tailTargetIndices = IntArray(INITIAL_CAPACITY)
    private var destinationTargetIndices = IntArray(INITIAL_CAPACITY)
    private val deferredTransfers = ArrayList<SlotTransfer>()

    fun add(
        tail: ProgramTarget,
        destination: ProgramTarget,
        transfer: SlotTransfer,
    ) {
        val index = deferredTransfers.size
        if (index == tailTargetIndices.size) {
            val capacity = tailTargetIndices.size * 2
            tailTargetIndices = tailTargetIndices.copyOf(capacity)
            destinationTargetIndices = destinationTargetIndices.copyOf(capacity)
        }
        tailTargetIndices[index] = tail.index
        destinationTargetIndices[index] = destination.index
        deferredTransfers.add(transfer)
    }

    fun emit(context: FunctionCompilationContext) {
        for (index in deferredTransfers.indices) {
            context.bind(ProgramTarget(tailTargetIndices[index]))
            context.emitJump(ProgramTarget(destinationTargetIndices[index]), deferredTransfers[index])
        }
    }

    private companion object {
        const val INITIAL_CAPACITY = 4
    }
}

internal fun FunctionCompilationContext.slotTransferTo(
    destinationSlots: IntArray,
    excludedTrailingOperandCount: Int = 0,
): SlotTransfer = SlotTransfer.create(
    operands = operands,
    operandStartIndex = operands.size - excludedTrailingOperandCount - destinationSlots.size,
    destinationSlots = destinationSlots,
)

private val OperandSourceKind.isImmediate: Boolean
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
