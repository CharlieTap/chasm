package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher

internal fun RefCastExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefCast,
) = RefCastExecutor(
    context = context,
    instruction = instruction,
    referenceTypeMatcher = ::ReferenceTypeMatcher,
    typeOfReferenceValue = ::TypeOfReferenceValue,
)

internal inline fun RefCastExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefCast,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline typeOfReferenceValue: TypeOf<Long, ReferenceType>,
) {
    val stack = context.vstack
    val store = context.store

    val frame = context.cstack.peekFrame()
    val moduleInstance = frame.instance

    val referenceValue = stack.pop()
    val otherReferenceType = typeOfReferenceValue(referenceValue, store, moduleInstance)
        ?: throw InvocationException(InvocationError.FailedToGetTypeOfReferenceValue)

    if (referenceTypeMatcher(otherReferenceType, instruction.referenceType, context)) {
        stack.push(referenceValue)
    } else {
        throw InvocationException(InvocationError.Trap.TrapEncountered)
    }
}
