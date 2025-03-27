package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.type.Caster
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ReferenceType

internal fun BrOnCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnCast,
) = BrOnCastExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    labelIndex = instruction.labelIndex,
    referenceType1 = instruction.srcReferenceType,
    referenceType2 = instruction.dstReferenceType,
    breakIfMatches = true,
    caster = ::Caster,
    breakExecutor = ::BreakExecutor,
)

internal fun BrOnCastFailExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnCastFail,
) = BrOnCastExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    labelIndex = instruction.labelIndex,
    referenceType1 = instruction.srcReferenceType,
    referenceType2 = instruction.dstReferenceType,
    breakIfMatches = false,
    caster = ::Caster,
    breakExecutor = ::BreakExecutor,
)

@Suppress("UNUSED_PARAMETER")
internal inline fun BrOnCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    labelIndex: Index.LabelIndex,
    referenceType1: ReferenceType,
    referenceType2: ReferenceType,
    breakIfMatches: Boolean,
    crossinline caster: Caster,
    crossinline breakExecutor: BreakExecutor,
) {
    val frame = cstack.peekFrame()
    val moduleInstance = frame.instance

    val casted = caster(vstack.peek(), referenceType2, moduleInstance, store)
    if (casted == breakIfMatches) {
        breakExecutor(cstack, vstack, labelIndex)
    }
}
