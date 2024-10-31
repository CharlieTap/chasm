@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.rolling.substitution.ReferenceTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

internal fun RefCastExecutor(
    context: ExecutionContext,
    instruction: ReferenceInstruction.RefCast,
): Result<Unit, InvocationError> = RefCastExecutor(
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
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val referenceType = instruction.referenceType

    val frame = stack.peekFrame().bind()
    val moduleInstance = frame.state.module

    val substitutedReferenceType = referenceTypeSubstitutor(referenceType, context.substitution()) // rt1

    val otherReferenceValue = stack.popReference().bind() // rt2
    val otherReferenceType = typeOfReferenceValue(otherReferenceValue, store, moduleInstance)
        .toResultOr { InvocationError.Trap.TrapEncountered }.bind()

    if (referenceTypeMatcher(otherReferenceType, substitutedReferenceType, context)) {
        stack.pushValue(otherReferenceValue)
    } else {
        Err(InvocationError.Trap.TrapEncountered).bind<Unit>()
    }
}
