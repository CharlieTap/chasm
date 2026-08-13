package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.ast.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.diagnostic.CompilerInstructionObserver
import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.compiler.operand.OperandSource
import io.github.charlietap.chasm.compiler.operand.OperandSourceKind
import io.github.charlietap.chasm.compiler.operand.i32Immediate
import io.github.charlietap.chasm.compiler.operand.i64Immediate
import io.github.charlietap.chasm.compiler.operand.sourceSlot
import io.github.charlietap.chasm.compiler.program.ProgramTarget
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpConditionDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.PopHandlerDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.PushHandlerDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ReturnDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.FunctionReturnDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.ThrowDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.ThrowRefDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64ConstDispatcher
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.CopyOperand
import io.github.charlietap.chasm.runtime.instruction.FusedOperand
import io.github.charlietap.chasm.runtime.instruction.LinkedInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericCondition
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.OperandCopyPlan
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction as RuntimeControlInstruction

internal class SlotCopyPlan private constructor(
    val size: Int,
    private val firstSourceKind: OperandSourceKind,
    private val firstSourceBits: Long,
    private val firstSourceSlot: Int,
    private val firstDestinationSlot: Int,
    private val sourceKinds: ByteArray?,
    private val sourceBits: LongArray?,
    private val sourceSlots: IntArray?,
    private val destinationSlots: IntArray?,
) {

    fun sourceKind(index: Int): OperandSourceKind = if (sourceKinds == null) {
        firstSourceKind
    } else {
        OperandSourceKind.entries[sourceKinds[index].toInt()]
    }

    fun sourceBits(index: Int): Long = sourceBits?.get(index) ?: firstSourceBits

    fun sourceSlot(index: Int): Int = sourceSlots?.get(index) ?: firstSourceSlot

    fun destinationSlot(index: Int): Int = destinationSlots?.get(index) ?: firstDestinationSlot

    fun sourceSlots(): IntArray = checkNotNull(sourceSlots)

    fun destinationSlots(): IntArray = checkNotNull(destinationSlots)

    fun isIdentity(): Boolean {
        for (index in 0 until size) {
            val sourceSlot = when (sourceKind(index)) {
                OperandSourceKind.Local -> sourceBits(index).toInt()
                OperandSourceKind.Frame -> sourceSlot(index)
                else -> return false
            }
            if (sourceSlot != destinationSlot(index)) return false
        }
        return true
    }

    companion object {
        val Empty = SlotCopyPlan(
            size = 0,
            firstSourceKind = OperandSourceKind.Frame,
            firstSourceBits = 0,
            firstSourceSlot = 0,
            firstDestinationSlot = 0,
            sourceKinds = null,
            sourceBits = null,
            sourceSlots = null,
            destinationSlots = null,
        )

        fun create(
            operands: List<Operand>,
            operandStartIndex: Int,
            destinationSlots: IntArray,
        ): SlotCopyPlan {
            check(operandStartIndex >= 0 && operandStartIndex + destinationSlots.size <= operands.size)
            if (destinationSlots.isEmpty()) return Empty
            val first = operands[operandStartIndex]
            if (destinationSlots.size == 1) {
                return SlotCopyPlan(
                    size = 1,
                    firstSourceKind = first.sourceKind,
                    firstSourceBits = first.sourceBits,
                    firstSourceSlot = first.reservedSlot,
                    firstDestinationSlot = destinationSlots[0],
                    sourceKinds = null,
                    sourceBits = null,
                    sourceSlots = null,
                    destinationSlots = null,
                )
            }

            var allFrameSources = true
            val sourceSlots = IntArray(destinationSlots.size)
            for (index in destinationSlots.indices) {
                val operand = operands[operandStartIndex + index]
                if (operand.sourceKind != OperandSourceKind.Frame) allFrameSources = false
                sourceSlots[index] = operand.reservedSlot
            }
            val sourceKinds = if (allFrameSources) null else ByteArray(destinationSlots.size)
            val sourceBits = if (allFrameSources) null else LongArray(destinationSlots.size)
            if (!allFrameSources) {
                val kinds = checkNotNull(sourceKinds)
                val bits = checkNotNull(sourceBits)
                for (index in destinationSlots.indices) {
                    val operand = operands[operandStartIndex + index]
                    kinds[index] = operand.sourceKind.ordinal.toByte()
                    bits[index] = operand.sourceBits
                }
            }
            return SlotCopyPlan(
                size = destinationSlots.size,
                firstSourceKind = first.sourceKind,
                firstSourceBits = first.sourceBits,
                firstSourceSlot = first.reservedSlot,
                firstDestinationSlot = destinationSlots[0],
                sourceKinds = sourceKinds,
                sourceBits = sourceBits,
                sourceSlots = sourceSlots,
                destinationSlots = destinationSlots,
            )
        }
    }
}

internal val emptySlotCopyPlan = SlotCopyPlan.Empty

internal enum class BranchOutcome {
    Always,
    Never,
    Dynamic,
}

internal fun FunctionCompilationContext.emitFunctionReturn(copies: SlotCopyPlan) {
    if (copies.isIdentity()) {
        emit(RuntimeControlInstruction.Return, ::ReturnDispatcher)
        return
    }

    val destinationSlotBase = copies.destinationSlot(0)
    val instruction = ControlSuperInstruction.FunctionReturn(copies.operandCopyPlan(destinationSlotBase))
    emit(instruction, ::FunctionReturnDispatcher)
}

internal fun FunctionCompilationContext.emitJump(
    target: ProgramTarget,
    copies: SlotCopyPlan = emptySlotCopyPlan,
    handlerPopCount: Int = 0,
) {
    check(handlerPopCount >= 0)
    if (handlerPopCount == 0 && copies.size > 0 && !copies.isIdentity()) {
        val destinationSlotBase = copies.destinationSlot(0)
        val operands = copies.operandCopyPlan(destinationSlotBase)
        append(
            target = target,
            instruction = { targetIp -> AdminInstruction.JumpCopies(operands, destinationSlotBase, targetIp) },
            dispatcher = ::JumpDispatcher,
        )
        return
    }

    if (!copies.isIdentity()) emitCopies(copies)
    repeat(handlerPopCount) { emitPopHandler() }
    append(target, AdminInstruction::Jump, ::JumpDispatcher)
}

private fun SlotCopyPlan.operandCopyPlan(
    destinationSlotBase: Int,
): OperandCopyPlan {
    val operands = Array<CopyOperand>(size) { index ->
        check(destinationSlot(index) == destinationSlotBase + index)
        when (sourceKind(index)) {
            OperandSourceKind.I32Immediate,
            OperandSourceKind.I64Immediate,
            OperandSourceKind.F32Immediate,
            OperandSourceKind.F64Immediate,
            -> CopyOperand.Immediate(sourceBits(index))
            OperandSourceKind.Local -> CopyOperand.Slot(sourceBits(index).toInt())
            OperandSourceKind.Frame -> CopyOperand.Slot(sourceSlot(index))
        }
    }
    return operandCopyPlan(operands, destinationSlotBase)
}

internal fun FunctionCompilationContext.emitBranchIf(
    condition: OperandSource,
    target: ProgramTarget,
    copies: SlotCopyPlan,
    whenZero: Boolean = false,
    handlerPopCount: Int = 0,
): BranchOutcome {
    check(handlerPopCount >= 0)
    if (condition.sourceKind == OperandSourceKind.I32Immediate) {
        val branch = (condition.sourceBits == 0L) == whenZero
        if (branch) emitJump(target, copies, handlerPopCount)
        return if (branch) BranchOutcome.Always else BranchOutcome.Never
    }
    val copy = copies.size == 1 && copies.sourceKind(0) == OperandSourceKind.Frame &&
        copies.sourceSlot(0) != copies.destinationSlot(0) && !whenZero && handlerPopCount == 0
    val branchTarget = if (copy || copies.size == 0 && handlerPopCount == 0) {
        target
    } else {
        program.target().also { tailTarget ->
            deferBranchPath(tailTarget, target, copies, handlerPopCount)
        }
    }
    val conditionKind = condition.sourceKind
    val conditionBits = condition.sourceBits
    append(branchTarget) { targetIp, observer ->
        when {
            copy && conditionKind.isImmediate -> dispatch(
                observer,
                AdminInstruction.JumpIfCopyI(
                    operand = conditionBits,
                    sourceSlot = copies.sourceSlot(0),
                    destinationSlot = copies.destinationSlot(0),
                    targetIp = targetIp,
                ),
                ::JumpDispatcher,
            )
            copy -> dispatch(
                observer,
                AdminInstruction.JumpIfCopyS(
                    operandSlot = conditionBits.toInt(),
                    sourceSlot = copies.sourceSlot(0),
                    destinationSlot = copies.destinationSlot(0),
                    targetIp = targetIp,
                ),
                ::JumpDispatcher,
            )
            whenZero && conditionKind.isImmediate -> dispatch(
                observer,
                AdminInstruction.JumpIfZeroI(conditionBits, targetIp),
                ::JumpDispatcher,
            )
            whenZero -> dispatch(
                observer,
                AdminInstruction.JumpIfZeroS(conditionBits.toInt(), targetIp),
                ::JumpDispatcher,
            )
            conditionKind.isImmediate -> dispatch(
                observer,
                AdminInstruction.JumpIfI(conditionBits, targetIp),
                ::JumpDispatcher,
            )
            else -> dispatch(
                observer,
                AdminInstruction.JumpIfS(conditionBits.toInt(), targetIp),
                ::JumpDispatcher,
            )
        }
    }
    return BranchOutcome.Dynamic
}

internal fun FunctionCompilationContext.emitBranchIf(
    condition: NumericCondition,
    target: ProgramTarget,
    copies: SlotCopyPlan,
    branchOnMatch: Boolean = true,
    handlerPopCount: Int = 0,
): BranchOutcome {
    check(handlerPopCount >= 0)
    val conditionMatches = condition.evaluateOrNull()
    if (conditionMatches != null) {
        val branch = conditionMatches == branchOnMatch
        if (branch) emitJump(target, copies, handlerPopCount)
        return if (branch) BranchOutcome.Always else BranchOutcome.Never
    }
    val branchTarget = prepareBranchTarget(target, copies, handlerPopCount)
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
        dispatcher = ::JumpDispatcher,
    )
}

internal fun FunctionCompilationContext.emitBranchOnNull(
    operand: OperandSource,
    target: ProgramTarget,
    copies: SlotCopyPlan,
    onNull: Boolean,
    handlerPopCount: Int,
) {
    val branchTarget = prepareBranchTarget(target, copies, handlerPopCount)
    val immediate = operand.sourceKind.isImmediate
    val operandBits = operand.sourceBits
    append(branchTarget) { targetIp, observer ->
        when {
            onNull && immediate -> dispatch(
                observer,
                AdminInstruction.JumpOnNullI(operandBits, targetIp),
                ::JumpDispatcher,
            )
            onNull -> dispatch(
                observer,
                AdminInstruction.JumpOnNullS(operandBits.toInt(), targetIp),
                ::JumpDispatcher,
            )
            immediate -> dispatch(
                observer,
                AdminInstruction.JumpOnNonNullI(operandBits, targetIp),
                ::JumpDispatcher,
            )
            else -> dispatch(
                observer,
                AdminInstruction.JumpOnNonNullS(operandBits.toInt(), targetIp),
                ::JumpDispatcher,
            )
        }
    }
}

internal fun FunctionCompilationContext.emitBranchOnCast(
    operand: OperandSource,
    target: ProgramTarget,
    copies: SlotCopyPlan,
    typeTest: ReferenceTypeTest,
    onSuccess: Boolean,
    handlerPopCount: Int,
) {
    val branchTarget = prepareBranchTarget(target, copies, handlerPopCount)
    val immediate = operand.sourceKind.isImmediate
    val operandBits = operand.sourceBits
    append(branchTarget) { targetIp, observer ->
        when {
            onSuccess && immediate -> dispatch(
                observer,
                AdminInstruction.JumpOnCastI(operandBits, targetIp, typeTest),
                ::JumpDispatcher,
            )
            onSuccess -> dispatch(
                observer,
                AdminInstruction.JumpOnCastS(operandBits.toInt(), targetIp, typeTest),
                ::JumpDispatcher,
            )
            immediate -> dispatch(
                observer,
                AdminInstruction.JumpOnCastFailI(operandBits, targetIp, typeTest),
                ::JumpDispatcher,
            )
            else -> dispatch(
                observer,
                AdminInstruction.JumpOnCastFailS(operandBits.toInt(), targetIp, typeTest),
                ::JumpDispatcher,
            )
        }
    }
}

internal fun FunctionCompilationContext.emitPushHandler(
    handlers: List<CatchHandler>,
    targetIndices: IntArray,
    payloadDestinationSlots: List<IntArray>,
) {
    append(
        targetIndices = targetIndices,
        instruction = { continuationIps -> AdminInstruction.PushHandler(handlers, continuationIps, payloadDestinationSlots) },
        dispatcher = ::PushHandlerDispatcher,
    )
}

internal fun FunctionCompilationContext.emitPopHandler() {
    emit(AdminInstruction.PopHandler, ::PopHandlerDispatcher)
}

internal fun FunctionCompilationContext.emitThrow(
    tagIndex: Index.TagIndex,
    payloadSlots: IntArray,
) {
    val instruction = ControlSuperInstruction.Throw(tagIndex, payloadSlots)
    emit(instruction, ::ThrowDispatcher)
}

internal fun FunctionCompilationContext.emitThrowRef(exceptionSlot: Int) {
    val instruction = ControlSuperInstruction.ThrowRefS(exceptionSlot)
    emit(instruction, ::ThrowRefDispatcher)
}

internal fun FunctionCompilationContext.prepareBranchTarget(
    target: ProgramTarget,
    copies: SlotCopyPlan,
    handlerPopCount: Int,
): ProgramTarget {
    check(handlerPopCount >= 0)
    if (copies.size == 0 && handlerPopCount == 0) return target
    return program.target().also { tail ->
        deferBranchPath(tail, target, copies, handlerPopCount)
    }
}

private fun FunctionCompilationContext.deferBranchPath(
    tail: ProgramTarget,
    destination: ProgramTarget,
    copies: SlotCopyPlan,
    handlerPopCount: Int,
) {
    val paths = deferredBranchPaths ?: DeferredBranchPaths().also { deferredBranchPaths = it }
    paths.add(tail, destination, copies, handlerPopCount)
}

internal fun FunctionCompilationContext.emitDeferredBranchPaths() {
    deferredBranchPaths?.emit(this)
    deferredBranchPaths = null
}

internal class DeferredBranchPaths {

    private var tailTargetIndices = IntArray(INITIAL_CAPACITY)
    private var destinationTargetIndices = IntArray(INITIAL_CAPACITY)
    private var handlerPopCounts = IntArray(INITIAL_CAPACITY)
    private val copyPlans = ArrayList<SlotCopyPlan>()

    fun add(
        tail: ProgramTarget,
        destination: ProgramTarget,
        copies: SlotCopyPlan,
        handlerPopCount: Int,
    ) {
        val index = copyPlans.size
        if (index == tailTargetIndices.size) {
            val capacity = tailTargetIndices.size * 2
            tailTargetIndices = tailTargetIndices.copyOf(capacity)
            destinationTargetIndices = destinationTargetIndices.copyOf(capacity)
            handlerPopCounts = handlerPopCounts.copyOf(capacity)
        }
        tailTargetIndices[index] = tail.index
        destinationTargetIndices[index] = destination.index
        handlerPopCounts[index] = handlerPopCount
        copyPlans.add(copies)
    }

    fun emit(context: FunctionCompilationContext) {
        for (index in copyPlans.indices) {
            context.bind(ProgramTarget(tailTargetIndices[index]))
            context.emitCopies(copyPlans[index])
            repeat(handlerPopCounts[index]) {
                context.emitPopHandler()
            }
            val destination = ProgramTarget(destinationTargetIndices[index])
            context.append(destination, AdminInstruction::Jump, ::JumpDispatcher)
        }
    }

    private companion object {
        const val INITIAL_CAPACITY = 4
    }
}

internal fun FunctionCompilationContext.planCopies(
    destinationSlots: IntArray,
    excludedTopCount: Int = 0,
): SlotCopyPlan = SlotCopyPlan.create(
    operands = operands,
    operandStartIndex = operands.size - excludedTopCount - destinationSlots.size,
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

private fun FunctionCompilationContext.emitCopies(copies: SlotCopyPlan) {
    for (index in 0 until copies.size) {
        val sourceKind = copies.sourceKind(index)
        val sourceBits = copies.sourceBits(index)
        val sourceSlot = copies.sourceSlot(index)
        when (sourceKind) {
            OperandSourceKind.I32Immediate -> {
                val instruction = NumericSuperInstruction.I32ConstS(sourceBits.toInt(), sourceSlot)
                emit(instruction, ::I32ConstDispatcher)
            }
            OperandSourceKind.I64Immediate -> {
                val instruction = NumericSuperInstruction.I64ConstS(sourceBits, sourceSlot)
                emit(instruction, ::I64ConstDispatcher)
            }
            OperandSourceKind.F32Immediate -> {
                val instruction = NumericSuperInstruction.F32ConstS(sourceBits.toInt(), sourceSlot)
                emit(instruction, ::F32ConstDispatcher)
            }
            OperandSourceKind.F64Immediate -> {
                val instruction = NumericSuperInstruction.F64ConstS(sourceBits, sourceSlot)
                emit(instruction, ::F64ConstDispatcher)
            }
            OperandSourceKind.Local -> emitCopy(sourceBits.toInt(), sourceSlot)
            OperandSourceKind.Frame -> Unit
        }
    }
    if (copies.size == 1) {
        emitCopy(copies.sourceSlot(0), copies.destinationSlot(0))
    } else if (copies.size > 1) {
        val sourceSlots = copies.sourceSlots()
        val destinationSlots = copies.destinationSlots()
        if (sourceSlots.contentEquals(destinationSlots)) return
        val instruction = AdminInstruction.CopySlots(sourceSlots, destinationSlots)
        emit(instruction, ::CopySlotsDispatcher)
    }
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
