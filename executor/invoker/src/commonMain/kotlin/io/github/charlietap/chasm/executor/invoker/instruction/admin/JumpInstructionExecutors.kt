package io.github.charlietap.chasm.executor.invoker.instruction.admin

import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.ext.isNullableReference
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ReferenceType

internal inline fun JumpExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.Jump,
) {
    jump(
        cstack = cstack,
        continuation = instruction.continuation,
        discardCount = instruction.discardCount,
        continuationSource = instruction.continuationSource,
        continuationOffset = instruction.continuationOffset,
    )
}

internal fun JumpExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpIfI,
) = JumpExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = instruction.operand,
    continuation = instruction.continuation,
    discardCount = instruction.discardCount,
    takenInstructions = instruction.takenInstructions,
    continuationSource = instruction.continuationSource,
    continuationOffset = instruction.continuationOffset,
)

internal fun JumpExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpIfS,
) = JumpExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot),
    continuation = instruction.continuation,
    discardCount = instruction.discardCount,
    takenInstructions = instruction.takenInstructions,
    continuationSource = instruction.continuationSource,
    continuationOffset = instruction.continuationOffset,
)

internal inline fun JumpExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    operand: Long,
    continuation: Array<DispatchableInstruction>,
    discardCount: Int,
    takenInstructions: List<DispatchableInstruction>,
    continuationSource: List<DispatchableInstruction?>?,
    continuationOffset: Int,
) {
    if (operand != 0L) {
        executeTakenInstructions(vstack, cstack, store, context, takenInstructions)
        jump(cstack, continuation, discardCount, continuationSource, continuationOffset)
    }
}

internal fun JumpTableExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpTableI,
) = JumpTableExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = instruction.operand,
    continuations = instruction.continuations,
    defaultContinuation = instruction.defaultContinuation,
    discardCount = instruction.discardCount,
    takenInstructions = instruction.takenInstructions,
    defaultTakenInstructions = instruction.defaultTakenInstructions,
    continuationSource = instruction.continuationSource,
    continuationOffsets = instruction.continuationOffsets,
    defaultContinuationOffset = instruction.defaultContinuationOffset,
)

internal fun JumpTableExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpTableS,
) = JumpTableExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot).toInt(),
    continuations = instruction.continuations,
    defaultContinuation = instruction.defaultContinuation,
    discardCount = instruction.discardCount,
    takenInstructions = instruction.takenInstructions,
    defaultTakenInstructions = instruction.defaultTakenInstructions,
    continuationSource = instruction.continuationSource,
    continuationOffsets = instruction.continuationOffsets,
    defaultContinuationOffset = instruction.defaultContinuationOffset,
)

internal inline fun JumpTableExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    operand: Int,
    continuations: List<Array<DispatchableInstruction>>,
    defaultContinuation: Array<DispatchableInstruction>,
    discardCount: Int,
    takenInstructions: List<List<DispatchableInstruction>>,
    defaultTakenInstructions: List<DispatchableInstruction>,
    continuationSource: List<DispatchableInstruction?>?,
    continuationOffsets: List<Int>,
    defaultContinuationOffset: Int,
) {
    val targetCount = continuationSource?.let { continuationOffsets.size } ?: continuations.size
    val targetIndex = if (operand >= 0 && operand < targetCount) operand else -1
    val continuation = if (targetIndex >= 0 && continuationSource == null) continuations[targetIndex] else defaultContinuation
    val continuationOffset = if (continuationSource == null) {
        -1
    } else if (targetIndex >= 0) {
        continuationOffsets[targetIndex]
    } else {
        defaultContinuationOffset
    }
    val selectedInstructions = if (targetIndex >= 0) takenInstructions[targetIndex] else defaultTakenInstructions

    executeTakenInstructions(vstack, cstack, store, context, selectedInstructions)
    jump(cstack, continuation, discardCount, continuationSource, continuationOffset)
}

internal fun JumpOnNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpOnNullI,
) = JumpOnNullExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = instruction.operand,
    continuation = instruction.continuation,
    discardCount = instruction.discardCount,
    takenInstructions = instruction.takenInstructions,
    continuationSource = instruction.continuationSource,
    continuationOffset = instruction.continuationOffset,
)

internal fun JumpOnNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpOnNullS,
) = JumpOnNullExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot),
    continuation = instruction.continuation,
    discardCount = instruction.discardCount,
    takenInstructions = instruction.takenInstructions,
    continuationSource = instruction.continuationSource,
    continuationOffset = instruction.continuationOffset,
)

internal inline fun JumpOnNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    operand: Long,
    continuation: Array<DispatchableInstruction>,
    discardCount: Int,
    takenInstructions: List<DispatchableInstruction>,
    continuationSource: List<DispatchableInstruction?>?,
    continuationOffset: Int,
) {
    if (operand.isNullableReference()) {
        executeTakenInstructions(vstack, cstack, store, context, takenInstructions)
        jump(cstack, continuation, discardCount, continuationSource, continuationOffset)
    }
}

internal fun JumpOnNonNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpOnNonNullI,
) = JumpOnNonNullExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = instruction.operand,
    continuation = instruction.continuation,
    discardCount = instruction.discardCount,
    takenInstructions = instruction.takenInstructions,
    continuationSource = instruction.continuationSource,
    continuationOffset = instruction.continuationOffset,
)

internal fun JumpOnNonNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpOnNonNullS,
) = JumpOnNonNullExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot),
    continuation = instruction.continuation,
    discardCount = instruction.discardCount,
    takenInstructions = instruction.takenInstructions,
    continuationSource = instruction.continuationSource,
    continuationOffset = instruction.continuationOffset,
)

internal inline fun JumpOnNonNullExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    operand: Long,
    continuation: Array<DispatchableInstruction>,
    discardCount: Int,
    takenInstructions: List<DispatchableInstruction>,
    continuationSource: List<DispatchableInstruction?>?,
    continuationOffset: Int,
) {
    if (!operand.isNullableReference()) {
        executeTakenInstructions(vstack, cstack, store, context, takenInstructions)
        jump(cstack, continuation, discardCount, continuationSource, continuationOffset)
    }
}

internal fun JumpOnCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpOnCastI,
) = JumpOnCastExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = instruction.operand,
    continuation = instruction.continuation,
    discardCount = instruction.discardCount,
    dstReferenceType = instruction.dstReferenceType,
    takenInstructions = instruction.takenInstructions,
    continuationSource = instruction.continuationSource,
    continuationOffset = instruction.continuationOffset,
    jumpIfMatches = true,
    caster = ::Caster,
)

internal fun JumpOnCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpOnCastS,
) = JumpOnCastExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot),
    continuation = instruction.continuation,
    discardCount = instruction.discardCount,
    dstReferenceType = instruction.dstReferenceType,
    takenInstructions = instruction.takenInstructions,
    continuationSource = instruction.continuationSource,
    continuationOffset = instruction.continuationOffset,
    jumpIfMatches = true,
    caster = ::Caster,
)

internal fun JumpOnCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpOnCastFailI,
) = JumpOnCastExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = instruction.operand,
    continuation = instruction.continuation,
    discardCount = instruction.discardCount,
    dstReferenceType = instruction.dstReferenceType,
    takenInstructions = instruction.takenInstructions,
    continuationSource = instruction.continuationSource,
    continuationOffset = instruction.continuationOffset,
    jumpIfMatches = false,
    caster = ::Caster,
)

internal fun JumpOnCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: AdminInstruction.JumpOnCastFailS,
) = JumpOnCastExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    operand = vstack.getFrameSlot(instruction.operandSlot),
    continuation = instruction.continuation,
    discardCount = instruction.discardCount,
    dstReferenceType = instruction.dstReferenceType,
    takenInstructions = instruction.takenInstructions,
    continuationSource = instruction.continuationSource,
    continuationOffset = instruction.continuationOffset,
    jumpIfMatches = false,
    caster = ::Caster,
)

internal inline fun JumpOnCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    operand: Long,
    continuation: Array<DispatchableInstruction>,
    discardCount: Int,
    dstReferenceType: ReferenceType,
    takenInstructions: List<DispatchableInstruction>,
    continuationSource: List<DispatchableInstruction?>?,
    continuationOffset: Int,
    jumpIfMatches: Boolean,
    crossinline caster: Caster,
) {
    val moduleInstance = cstack.peekFrame().instance
    val casted = caster(operand, dstReferenceType, moduleInstance, store)
    if (casted == jumpIfMatches) {
        executeTakenInstructions(vstack, cstack, store, context, takenInstructions)
        jump(cstack, continuation, discardCount, continuationSource, continuationOffset)
    }
}

private inline fun jump(
    cstack: ControlStack,
    continuation: Array<DispatchableInstruction>,
    discardCount: Int,
    continuationSource: List<DispatchableInstruction?>? = null,
    continuationOffset: Int = -1,
) {
    cstack.shrinkInstructions(cstack.instructionsDepth() - discardCount)
    if (continuationSource != null) {
        cstack.pushContinuation(continuationSource, continuationOffset)
    } else if (continuation.isNotEmpty()) {
        cstack.push(continuation)
    }
}

private inline fun executeTakenInstructions(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instructions: List<DispatchableInstruction>,
) {
    instructions.forEach { instruction ->
        instruction(vstack, cstack, store, context)
    }
}
