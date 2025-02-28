package io.github.charlietap.chasm.executor.invoker.instruction.referencefused

import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher

internal fun RefTestExecutor(
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefTest,
) = RefTestExecutor(
    context = context,
    instruction = instruction,
    referenceTypeMatcher = ::ReferenceTypeMatcher,
    typeOfReferenceValue = ::TypeOfReferenceValue,
)

internal inline fun RefTestExecutor(
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefTest,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline typeOfReferenceValue: TypeOf<Long, ReferenceType>,
) {
    val stack = context.vstack
    val store = context.store
    val frame = context.cstack.peekFrame()
    val moduleInstance = frame.instance

    val referenceType = typeOfReferenceValue(instruction.reference(stack), store, moduleInstance)
    if (referenceTypeMatcher(referenceType, instruction.referenceType, context)) {
        instruction.destination(1L, stack)
    } else {
        instruction.destination(0L, stack)
    }
}
