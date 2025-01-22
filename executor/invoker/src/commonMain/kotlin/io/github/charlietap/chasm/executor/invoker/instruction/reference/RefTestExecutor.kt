package io.github.charlietap.chasm.executor.invoker.instruction.reference

import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.rolling.substitution.ReferenceTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

internal fun RefTestExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefTest,
) = RefTestExecutor(
    context = context,
    instruction = instruction,
    referenceTypeSubstitutor = ::ReferenceTypeSubstitutor,
    referenceTypeMatcher = ::ReferenceTypeMatcher,
    typeOfReferenceValue = ::TypeOfReferenceValue,
)

internal inline fun RefTestExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefTest,
    crossinline referenceTypeSubstitutor: TypeSubstitutor<ReferenceType>,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline typeOfReferenceValue: TypeOf<ReferenceValue, ReferenceType>,
) {

    val (stack, store) = context
    val referenceType = instruction.referenceType
    val frame = stack.peekFrame()
    val moduleInstance = frame.instance

    val substitutedReferenceType = referenceTypeSubstitutor(referenceType, context.substitutor)

    val otherReferenceValue = stack.popReference()
    val otherReferenceType = typeOfReferenceValue(otherReferenceValue, store, moduleInstance)
        ?: throw InvocationException(InvocationError.FailedToGetTypeOfReferenceValue)

    if (referenceTypeMatcher(otherReferenceType, substitutedReferenceType, context)) {
        stack.push(NumberValue.I32(1))
    } else {
        stack.push(NumberValue.I32(0))
    }
}
