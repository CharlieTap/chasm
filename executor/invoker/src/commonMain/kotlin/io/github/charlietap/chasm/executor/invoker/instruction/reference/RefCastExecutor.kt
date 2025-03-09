package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher

internal fun RefCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefCast,
) = RefCastExecutor(
    vstack = vstack,
    cstack = cstack,
    store = store,
    context = context,
    instruction = instruction,
    referenceTypeMatcher = ::ReferenceTypeMatcher,
    typeOfReferenceValue = ::TypeOfReferenceValue,
)

internal inline fun RefCastExecutor(
    vstack: ValueStack,
    cstack: ControlStack,
    store: Store,
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefCast,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline typeOfReferenceValue: TypeOf<Long, ReferenceType>,
) {
    val frame = cstack.peekFrame()
    val moduleInstance = frame.instance

    val referenceValue = vstack.pop()
    val referenceType = typeOfReferenceValue(referenceValue, store, moduleInstance)

    if (referenceTypeMatcher(referenceType, instruction.referenceType, context)) {
        vstack.push(referenceValue)
    } else {
        throw InvocationException(InvocationError.FailedToCastReference)
    }
}
