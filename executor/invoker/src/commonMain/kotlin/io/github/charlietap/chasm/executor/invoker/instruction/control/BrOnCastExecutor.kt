package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher

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
    referenceTypeMatcher = ::ReferenceTypeMatcher,
    typeOfReferenceValue = ::TypeOfReferenceValue,
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
    referenceTypeMatcher = ::ReferenceTypeMatcher,
    typeOfReferenceValue = ::TypeOfReferenceValue,
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
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline typeOfReferenceValue: TypeOf<Long, ReferenceType>,
    crossinline breakExecutor: BreakExecutor,
) {
    val frame = cstack.peekFrame()
    val moduleInstance = frame.instance

    val referenceType = typeOfReferenceValue(vstack.peek(), store, moduleInstance)
    val referenceTypeMatches = referenceTypeMatcher(referenceType, referenceType2, context)

    if (referenceTypeMatches == breakIfMatches) {
        breakExecutor(cstack, vstack, labelIndex)
    }
}
