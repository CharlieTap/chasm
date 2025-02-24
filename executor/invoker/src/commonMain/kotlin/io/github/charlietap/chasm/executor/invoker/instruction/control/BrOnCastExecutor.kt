package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher

internal fun BrOnCastExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnCast,
) = BrOnCastExecutor(
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
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnCastFail,
) =
    BrOnCastExecutor(
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
    context: ExecutionContext,
    labelIndex: Index.LabelIndex,
    referenceType1: ReferenceType,
    referenceType2: ReferenceType,
    breakIfMatches: Boolean,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline typeOfReferenceValue: TypeOf<Long, ReferenceType>,
    crossinline breakExecutor: BreakExecutor,
) {
    val stack = context.vstack
    val store = context.store
    val frame = context.cstack.peekFrame()
    val moduleInstance = frame.instance

    val referenceType = typeOfReferenceValue(stack.peek(), store, moduleInstance)
    val referenceTypeMatches = referenceTypeMatcher(referenceType, referenceType2, context)

    if (referenceTypeMatches == breakIfMatches) {
        breakExecutor(context.cstack, stack, labelIndex)
    }
}
