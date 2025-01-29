package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.rolling.substitution.ReferenceTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

internal fun RefCastExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefCast,
) = RefCastExecutor(
    context = context,
    instruction = instruction,
    referenceTypeSubstitutor = ::ReferenceTypeSubstitutor,
    referenceTypeMatcher = ::ReferenceTypeMatcher,
    typeOfReferenceValue = ::TypeOfReferenceValue,
)

internal inline fun RefCastExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefCast,
    crossinline referenceTypeSubstitutor: TypeSubstitutor<ReferenceType>,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline typeOfReferenceValue: TypeOf<ReferenceValue, ReferenceType>,
) {
    val stack = context.vstack
    val store = context.store
    val referenceType = instruction.referenceType

    val frame = context.cstack.peekFrame()
    val moduleInstance = frame.instance

    val substitutedReferenceType = referenceTypeSubstitutor(referenceType, context.substitutor) // rt1

    val otherReferenceValue = stack.popReference() // rt2
    val otherReferenceType = typeOfReferenceValue(otherReferenceValue, store, moduleInstance)
        ?: throw InvocationException(InvocationError.FailedToGetTypeOfReferenceValue)

    if (referenceTypeMatcher(otherReferenceType, substitutedReferenceType, context)) {
        stack.push(otherReferenceValue.toLongFromBoxed())
    } else {
        throw InvocationException(InvocationError.Trap.TrapEncountered)
    }
}
