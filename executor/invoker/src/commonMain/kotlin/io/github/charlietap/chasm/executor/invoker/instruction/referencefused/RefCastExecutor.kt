package io.github.charlietap.chasm.executor.invoker.instruction.referencefused

import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.instruction.FusedReferenceInstruction
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher

internal fun RefCastExecutor(
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefCast,
) = RefCastExecutor(
    context = context,
    instruction = instruction,
    referenceTypeMatcher = ::ReferenceTypeMatcher,
    typeOfReferenceValue = ::TypeOfReferenceValue,
)

internal inline fun RefCastExecutor(
    context: ExecutionContext,
    instruction: FusedReferenceInstruction.RefCast,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline typeOfReferenceValue: TypeOf<Long, ReferenceType>,
) {
    val stack = context.vstack
    val store = context.store

    val frame = context.cstack.peekFrame()
    val moduleInstance = frame.instance

    val referenceValue = instruction.reference(stack)
    val referenceType = typeOfReferenceValue(referenceValue, store, moduleInstance)

    if (referenceTypeMatcher(referenceType, instruction.referenceType, context)) {
        instruction.destination(referenceValue, stack)
    } else {
        throw InvocationException(InvocationError.FailedToCastReference)
    }
}
