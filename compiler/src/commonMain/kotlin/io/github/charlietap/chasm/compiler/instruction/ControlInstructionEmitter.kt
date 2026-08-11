package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.ast.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
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
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.ThrowDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.controlfused.ThrowRefDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64ConstDispatcher
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericCondition
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction
import io.github.charlietap.chasm.type.ReferenceType

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

internal fun FunctionCompilationContext.emitJump(
    target: ProgramTarget,
    copies: SlotCopyPlan = emptySlotCopyPlan,
    handlerPopCount: Int = 0,
) {
    check(handlerPopCount >= 0)
    emitCopies(copies)
    repeat(handlerPopCount) { emitPopHandler() }
    program.append(target) { targetIp ->
        val instruction = AdminInstruction.Jump(targetIp)
        dispatch(instruction, ::JumpDispatcher)
    }
}

internal fun FunctionCompilationContext.emitBranchIf(
    condition: OperandSource,
    target: ProgramTarget,
    copies: SlotCopyPlan,
    whenZero: Boolean = false,
    handlerPopCount: Int = 0,
) {
    check(handlerPopCount >= 0)
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
    program.append(branchTarget) { targetIp ->
        when {
            copy && conditionKind.isImmediate -> dispatch(
                AdminInstruction.JumpIfCopyI(
                    operand = conditionBits,
                    sourceSlot = copies.sourceSlot(0),
                    destinationSlot = copies.destinationSlot(0),
                    targetIp = targetIp,
                ),
                ::JumpDispatcher,
            )
            copy -> dispatch(
                AdminInstruction.JumpIfCopyS(
                    operandSlot = conditionBits.toInt(),
                    sourceSlot = copies.sourceSlot(0),
                    destinationSlot = copies.destinationSlot(0),
                    targetIp = targetIp,
                ),
                ::JumpDispatcher,
            )
            whenZero && conditionKind.isImmediate -> dispatch(
                AdminInstruction.JumpIfZeroI(conditionBits, targetIp),
                ::JumpDispatcher,
            )
            whenZero -> dispatch(
                AdminInstruction.JumpIfZeroS(conditionBits.toInt(), targetIp),
                ::JumpDispatcher,
            )
            conditionKind.isImmediate -> dispatch(
                AdminInstruction.JumpIfI(conditionBits, targetIp),
                ::JumpDispatcher,
            )
            else -> dispatch(
                AdminInstruction.JumpIfS(conditionBits.toInt(), targetIp),
                ::JumpDispatcher,
            )
        }
    }
}

internal fun FunctionCompilationContext.emitBranchIf(
    condition: NumericCondition,
    target: ProgramTarget,
    copies: SlotCopyPlan,
    branchOnMatch: Boolean = true,
    handlerPopCount: Int = 0,
) {
    check(handlerPopCount >= 0)
    val branchTarget = prepareBranchTarget(target, copies, handlerPopCount)
    program.append(branchTarget) { targetIp ->
        dispatch(JumpConditionDispatcher(condition, targetIp, branchOnMatch)) {
            if (branchOnMatch) {
                AdminInstruction.JumpIfCondition(condition, targetIp)
            } else {
                AdminInstruction.JumpIfConditionMismatch(condition, targetIp)
            }
        }
    }
}

internal fun FunctionCompilationContext.emitBranchTable(
    selector: OperandSource,
    targetIndices: IntArray,
) {
    val selectorKind = selector.sourceKind
    val selectorBits = selector.sourceBits
    program.append(targetIndices) { targetIps ->
        if (selectorKind == OperandSourceKind.I32Immediate) {
            val instruction = AdminInstruction.JumpTableI(selectorBits.toInt(), targetIps)
            dispatch(instruction, ::JumpDispatcher)
        } else {
            val instruction = AdminInstruction.JumpTableS(selectorBits.toInt(), targetIps)
            dispatch(instruction, ::JumpDispatcher)
        }
    }
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
    program.append(branchTarget) { targetIp ->
        when {
            onNull && immediate -> dispatch(AdminInstruction.JumpOnNullI(operandBits, targetIp), ::JumpDispatcher)
            onNull -> dispatch(AdminInstruction.JumpOnNullS(operandBits.toInt(), targetIp), ::JumpDispatcher)
            immediate -> dispatch(AdminInstruction.JumpOnNonNullI(operandBits, targetIp), ::JumpDispatcher)
            else -> dispatch(AdminInstruction.JumpOnNonNullS(operandBits.toInt(), targetIp), ::JumpDispatcher)
        }
    }
}

internal fun FunctionCompilationContext.emitBranchOnCast(
    operand: OperandSource,
    target: ProgramTarget,
    copies: SlotCopyPlan,
    sourceType: ReferenceType,
    destinationType: ReferenceType,
    onSuccess: Boolean,
    handlerPopCount: Int,
) {
    val branchTarget = prepareBranchTarget(target, copies, handlerPopCount)
    val immediate = operand.sourceKind.isImmediate
    val operandBits = operand.sourceBits
    program.append(branchTarget) { targetIp ->
        when {
            onSuccess && immediate -> dispatch(
                AdminInstruction.JumpOnCastI(operandBits, targetIp, sourceType, destinationType),
                ::JumpDispatcher,
            )
            onSuccess -> dispatch(
                AdminInstruction.JumpOnCastS(operandBits.toInt(), targetIp, sourceType, destinationType),
                ::JumpDispatcher,
            )
            immediate -> dispatch(
                AdminInstruction.JumpOnCastFailI(operandBits, targetIp, sourceType, destinationType),
                ::JumpDispatcher,
            )
            else -> dispatch(
                AdminInstruction.JumpOnCastFailS(
                    operandBits.toInt(),
                    targetIp,
                    sourceType,
                    destinationType,
                ),
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
    program.append(targetIndices) { continuationIps ->
        val instruction = AdminInstruction.PushHandler(handlers, continuationIps, payloadDestinationSlots)
        dispatch(instruction, ::PushHandlerDispatcher)
    }
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
        val program = context.program
        for (index in copyPlans.indices) {
            program.bind(ProgramTarget(tailTargetIndices[index]))
            context.emitCopies(copyPlans[index])
            repeat(handlerPopCounts[index]) {
                context.emitPopHandler()
            }
            val destination = ProgramTarget(destinationTargetIndices[index])
            program.append(destination) { targetIp ->
                val instruction = AdminInstruction.Jump(targetIp)
                context.dispatch(instruction, ::JumpDispatcher)
            }
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
