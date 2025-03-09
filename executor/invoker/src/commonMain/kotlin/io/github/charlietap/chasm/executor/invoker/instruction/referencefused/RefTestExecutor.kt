package io.github.charlietap.chasm.executor.invoker.instruction.referencefused

import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher

internal fun RefTestExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefTest,
) = RefTestExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    referenceTypeMatcher = ::ReferenceTypeMatcher,
    typeOfReferenceValue = ::TypeOfReferenceValue,
)

internal inline fun RefTestExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefTest,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline typeOfReferenceValue: TypeOf<Long, ReferenceType>,
) {
    val frame = cstack.peekFrame()
    val moduleInstance = frame.instance

    val referenceType = typeOfReferenceValue(instruction.reference(vstack), store, moduleInstance)
    if (referenceTypeMatcher(referenceType, instruction.referenceType, context)) {
        instruction.destination(1L, vstack)
    } else {
        instruction.destination(0L, vstack)
    }
}
