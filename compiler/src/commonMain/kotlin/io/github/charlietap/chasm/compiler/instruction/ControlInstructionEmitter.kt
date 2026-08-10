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
import io.github.charlietap.chasm.compiler.program.ProgramBuilder
import io.github.charlietap.chasm.compiler.program.ProgramTarget
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotDispatcher
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
    program.appendCopies(copies)
    repeat(handlerPopCount) { emitPopHandler() }
    program.append(target) { targetIp -> JumpDispatcher(AdminInstruction.Jump(targetIp)) }
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
            copy && conditionKind.isImmediate -> JumpDispatcher(
                AdminInstruction.JumpIfCopyI(
                    operand = conditionBits,
                    sourceSlot = copies.sourceSlot(0),
                    destinationSlot = copies.destinationSlot(0),
                    targetIp = targetIp,
                ),
            )
            copy -> JumpDispatcher(
                AdminInstruction.JumpIfCopyS(
                    operandSlot = conditionBits.toInt(),
                    sourceSlot = copies.sourceSlot(0),
                    destinationSlot = copies.destinationSlot(0),
                    targetIp = targetIp,
                ),
            )
            whenZero && conditionKind.isImmediate -> JumpDispatcher(AdminInstruction.JumpIfZeroI(conditionBits, targetIp))
            whenZero -> JumpDispatcher(AdminInstruction.JumpIfZeroS(conditionBits.toInt(), targetIp))
            conditionKind.isImmediate -> JumpDispatcher(AdminInstruction.JumpIfI(conditionBits, targetIp))
            else -> JumpDispatcher(AdminInstruction.JumpIfS(conditionBits.toInt(), targetIp))
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
        JumpConditionDispatcher(condition, targetIp, branchOnMatch)
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
            JumpDispatcher(AdminInstruction.JumpTableI(selectorBits.toInt(), targetIps))
        } else {
            JumpDispatcher(AdminInstruction.JumpTableS(selectorBits.toInt(), targetIps))
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
            onNull && immediate -> JumpDispatcher(AdminInstruction.JumpOnNullI(operandBits, targetIp))
            onNull -> JumpDispatcher(AdminInstruction.JumpOnNullS(operandBits.toInt(), targetIp))
            immediate -> JumpDispatcher(AdminInstruction.JumpOnNonNullI(operandBits, targetIp))
            else -> JumpDispatcher(AdminInstruction.JumpOnNonNullS(operandBits.toInt(), targetIp))
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
            onSuccess && immediate -> JumpDispatcher(
                AdminInstruction.JumpOnCastI(operandBits, targetIp, sourceType, destinationType),
            )
            onSuccess -> JumpDispatcher(
                AdminInstruction.JumpOnCastS(operandBits.toInt(), targetIp, sourceType, destinationType),
            )
            immediate -> JumpDispatcher(
                AdminInstruction.JumpOnCastFailI(operandBits, targetIp, sourceType, destinationType),
            )
            else -> JumpDispatcher(
                AdminInstruction.JumpOnCastFailS(operandBits.toInt(), targetIp, sourceType, destinationType),
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
        PushHandlerDispatcher(AdminInstruction.PushHandler(handlers, continuationIps, payloadDestinationSlots))
    }
}

internal fun FunctionCompilationContext.emitPopHandler() {
    program.append(PopHandlerDispatcher(AdminInstruction.PopHandler))
}

internal fun FunctionCompilationContext.emitThrow(
    tagIndex: Index.TagIndex,
    payloadSlots: IntArray,
) {
    program.append(ThrowDispatcher(ControlSuperInstruction.Throw(tagIndex, payloadSlots)))
}

internal fun FunctionCompilationContext.emitThrowRef(exceptionSlot: Int) {
    program.append(ThrowRefDispatcher(ControlSuperInstruction.ThrowRefS(exceptionSlot)))
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
    deferredBranchPaths?.emit(program)
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

    fun emit(program: ProgramBuilder) {
        for (index in copyPlans.indices) {
            program.bind(ProgramTarget(tailTargetIndices[index]))
            program.appendCopies(copyPlans[index])
            repeat(handlerPopCounts[index]) {
                program.append(PopHandlerDispatcher(AdminInstruction.PopHandler))
            }
            val destination = ProgramTarget(destinationTargetIndices[index])
            program.append(destination) { targetIp -> JumpDispatcher(AdminInstruction.Jump(targetIp)) }
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

internal fun ProgramBuilder.appendCopies(copies: SlotCopyPlan) {
    for (index in 0 until copies.size) {
        val sourceKind = copies.sourceKind(index)
        val sourceBits = copies.sourceBits(index)
        val sourceSlot = copies.sourceSlot(index)
        when (sourceKind) {
            OperandSourceKind.I32Immediate -> append(
                I32ConstDispatcher(
                    NumericSuperInstruction.I32ConstS(sourceBits.toInt(), sourceSlot),
                ),
            )
            OperandSourceKind.I64Immediate -> append(
                I64ConstDispatcher(
                    NumericSuperInstruction.I64ConstS(sourceBits, sourceSlot),
                ),
            )
            OperandSourceKind.F32Immediate -> append(
                F32ConstDispatcher(
                    NumericSuperInstruction.F32ConstS(sourceBits.toInt(), sourceSlot),
                ),
            )
            OperandSourceKind.F64Immediate -> append(
                F64ConstDispatcher(
                    NumericSuperInstruction.F64ConstS(sourceBits, sourceSlot),
                ),
            )
            OperandSourceKind.Local -> appendCopy(sourceBits.toInt(), sourceSlot)
            OperandSourceKind.Frame -> Unit
        }
    }
    if (copies.size == 1) {
        appendCopy(copies.sourceSlot(0), copies.destinationSlot(0))
    } else if (copies.size > 1) {
        val sourceSlots = copies.sourceSlots()
        val destinationSlots = copies.destinationSlots()
        if (sourceSlots.contentEquals(destinationSlots)) return
        append(
            CopySlotsDispatcher(
                AdminInstruction.CopySlots(sourceSlots, destinationSlots),
            ),
        )
    }
}

private fun ProgramBuilder.appendCopy(sourceSlot: Int, destinationSlot: Int) {
    if (sourceSlot == destinationSlot) return
    append(CopySlotDispatcher(sourceSlot, destinationSlot))
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
