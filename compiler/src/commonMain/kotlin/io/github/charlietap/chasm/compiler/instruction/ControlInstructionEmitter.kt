package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.diagnostic.CompilerInstructionObserver
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.compiler.program.ProgramTarget
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpConditionDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpCopiesDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfCopyIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfCopySDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfZeroIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpIfZeroSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnCastFailIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnCastFailSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnCastIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnCastSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnNonNullIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnNonNullSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnNullIDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpOnNullSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpTableSDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.FunctionReturnDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowRefDispatcher
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.instruction.FusedOperand
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericCondition
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

internal enum class BranchOutcome {
    Always,
    Never,
    Dynamic,
}

internal fun FunctionCompilationContext.emitFunctionReturn(transfer: SlotTransfer) {
    val destinationSlotBase = if (transfer.size == 0) 0 else transfer.destinationSlot(0)
    val instruction = ControlInstruction.FunctionReturn(
        results = transfer.toOperandTransfer(destinationSlotBase),
        activationHeaderSlot = layout.activationHeaderSlot,
    )
    emit(instruction, ::FunctionReturnDispatcher)
}

internal fun FunctionCompilationContext.emitJump(
    target: ProgramTarget,
    transfer: SlotTransfer = emptySlotTransfer,
) {
    if (!transfer.isIdentity()) {
        val destinationSlotBase = transfer.destinationSlot(0)
        val operands = transfer.toOperandTransfer(destinationSlotBase)
        append(
            target = target,
            instruction = { targetIp -> AdminInstruction.JumpCopies(operands, destinationSlotBase, targetIp) },
            dispatcher = { jump -> JumpCopiesDispatcher(jump.operands, jump.destinationSlotBase, jump.targetIp) },
        )
        return
    }

    append(target, AdminInstruction::Jump) { jump -> JumpDispatcher(jump.targetIp) }
}

private fun SlotTransfer.toOperandTransfer(
    destinationSlotBase: Int,
): OperandTransfer {
    val operands = Array<TransferSource>(size) { index ->
        check(destinationSlot(index) == destinationSlotBase + index)
        when (sourceKind(index)) {
            OperandSourceKind.I32Immediate,
            OperandSourceKind.I64Immediate,
            OperandSourceKind.F32Immediate,
            OperandSourceKind.F64Immediate,
            -> TransferSource.Immediate(sourceBits(index))
            OperandSourceKind.Local -> TransferSource.Slot(sourceBits(index).toInt())
            OperandSourceKind.Frame -> TransferSource.Slot(sourceSlot(index))
        }
    }
    return selectOperandTransfer(operands, destinationSlotBase)
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
    append(branchTarget) { targetIp, observer ->
        when {
            scalarTransfer && conditionKind.isImmediate -> dispatch(
                observer,
                AdminInstruction.JumpIfCopyI(
                    operand = conditionBits,
                    sourceSlot = transfer.sourceSlot(0),
                    destinationSlot = transfer.destinationSlot(0),
                    targetIp = targetIp,
                ),
            ) { jump -> JumpIfCopyIDispatcher(jump.operand, jump.sourceSlot, jump.destinationSlot, jump.targetIp) }
            scalarTransfer -> dispatch(
                observer,
                AdminInstruction.JumpIfCopyS(
                    operandSlot = conditionBits.toInt(),
                    sourceSlot = transfer.sourceSlot(0),
                    destinationSlot = transfer.destinationSlot(0),
                    targetIp = targetIp,
                ),
            ) { jump -> JumpIfCopySDispatcher(jump.operandSlot, jump.sourceSlot, jump.destinationSlot, jump.targetIp) }
            whenZero && conditionKind.isImmediate -> dispatch(
                observer,
                AdminInstruction.JumpIfZeroI(conditionBits, targetIp),
            ) { jump -> JumpIfZeroIDispatcher(jump.operand, jump.targetIp) }
            whenZero -> dispatch(
                observer,
                AdminInstruction.JumpIfZeroS(conditionBits.toInt(), targetIp),
            ) { jump -> JumpIfZeroSDispatcher(jump.operandSlot, jump.targetIp) }
            conditionKind.isImmediate -> dispatch(
                observer,
                AdminInstruction.JumpIfI(conditionBits, targetIp),
            ) { jump -> JumpIfIDispatcher(jump.operand, jump.targetIp) }
            else -> dispatch(
                observer,
                AdminInstruction.JumpIfS(conditionBits.toInt(), targetIp),
            ) { jump -> JumpIfSDispatcher(jump.operandSlot, jump.targetIp) }
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
    appendDispatched(
        target = branchTarget,
        dispatchableInstruction = { targetIp -> JumpConditionDispatcher(condition, targetIp, branchOnMatch) },
        instruction = { targetIp ->
            if (branchOnMatch) {
                AdminInstruction.JumpIfCondition(condition, targetIp)
            } else {
                AdminInstruction.JumpIfConditionMismatch(condition, targetIp)
            }
        },
    )
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
    append(
        targetIndices = targetIndices,
        instruction = { targetIps -> AdminInstruction.JumpTableS(selectorBits.toInt(), targetIps) },
        dispatcher = { jump -> JumpTableSDispatcher(jump.operandSlot, jump.targetIps) },
    )
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
    append(branchTarget) { targetIp, observer ->
        when {
            onNull && immediate -> dispatch(
                observer,
                AdminInstruction.JumpOnNullI(operandBits, targetIp),
            ) { jump -> JumpOnNullIDispatcher(jump.operand, jump.targetIp) }
            onNull -> dispatch(
                observer,
                AdminInstruction.JumpOnNullS(operandBits.toInt(), targetIp),
            ) { jump -> JumpOnNullSDispatcher(jump.operandSlot, jump.targetIp) }
            immediate -> dispatch(
                observer,
                AdminInstruction.JumpOnNonNullI(operandBits, targetIp),
            ) { jump -> JumpOnNonNullIDispatcher(jump.operand, jump.targetIp) }
            else -> dispatch(
                observer,
                AdminInstruction.JumpOnNonNullS(operandBits.toInt(), targetIp),
            ) { jump -> JumpOnNonNullSDispatcher(jump.operandSlot, jump.targetIp) }
        }
    }
}

internal fun FunctionCompilationContext.emitBranchOnCast(
    operand: OperandSource,
    target: ProgramTarget,
    transfer: SlotTransfer,
    typeTest: ReferenceTypeTest,
    onSuccess: Boolean,
) {
    val branchTarget = prepareBranchTarget(target, transfer)
    val immediate = operand.sourceKind.isImmediate
    val operandBits = operand.sourceBits
    append(branchTarget) { targetIp, observer ->
        when {
            onSuccess && immediate -> dispatch(
                observer,
                AdminInstruction.JumpOnCastI(operandBits, targetIp, typeTest),
            ) { jump -> JumpOnCastIDispatcher(jump.operand, jump.targetIp, jump.typeTest) }
            onSuccess -> dispatch(
                observer,
                AdminInstruction.JumpOnCastS(operandBits.toInt(), targetIp, typeTest),
            ) { jump -> JumpOnCastSDispatcher(jump.operandSlot, jump.targetIp, jump.typeTest) }
            immediate -> dispatch(
                observer,
                AdminInstruction.JumpOnCastFailI(operandBits, targetIp, typeTest),
            ) { jump -> JumpOnCastFailIDispatcher(jump.operand, jump.targetIp, jump.typeTest) }
            else -> dispatch(
                observer,
                AdminInstruction.JumpOnCastFailS(operandBits.toInt(), targetIp, typeTest),
            ) { jump -> JumpOnCastFailSDispatcher(jump.operandSlot, jump.targetIp, jump.typeTest) }
        }
    }
}

internal fun FunctionCompilationContext.emitThrow(
    tagIndex: Index.TagIndex,
    firstPayloadSlot: Int,
) {
    val instruction = ControlInstruction.Throw(
        tagAddress = compiler.instance.tagAddresses[tagIndex.toInt()],
        firstPayloadSlot = firstPayloadSlot,
    )
    emit(instruction, ::ThrowDispatcher)
}

internal fun FunctionCompilationContext.emitThrowRef(exceptionSlot: Int) {
    val instruction = ControlInstruction.ThrowRefS(exceptionSlot)
    emit(instruction, ::ThrowRefDispatcher)
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

private inline fun <T : LinkedInstruction> dispatch(
    observer: CompilerInstructionObserver?,
    instruction: T,
    dispatcher: (T) -> DispatchableInstruction,
): DispatchableInstruction {
    val dispatchableInstruction = dispatcher(instruction)
    observer?.onInstruction(dispatchableInstruction, instruction)
    return dispatchableInstruction
}

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
