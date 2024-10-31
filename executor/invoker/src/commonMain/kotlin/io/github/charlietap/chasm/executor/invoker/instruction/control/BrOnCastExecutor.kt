@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.invoker.context.ExecutionContext
import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.peekReference
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.rolling.substitution.ReferenceTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

internal typealias BrOnCastExecutor = (ExecutionContext, Index.LabelIndex, ReferenceType, ReferenceType, Boolean) -> Result<Unit, InvocationError>

internal inline fun BrOnCastExecutor(
    context: ExecutionContext,
    labelIndex: Index.LabelIndex,
    referenceType1: ReferenceType,
    referenceType2: ReferenceType,
    breakIfMatches: Boolean,
): Result<Unit, InvocationError> =
    BrOnCastExecutor(
        context = context,
        labelIndex = labelIndex,
        referenceType1 = referenceType1,
        referenceType2 = referenceType2,
        breakIfMatches = breakIfMatches,
        referenceTypeSubstitutor = ::ReferenceTypeSubstitutor,
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
    crossinline referenceTypeSubstitutor: TypeSubstitutor<ReferenceType>,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline typeOfReferenceValue: TypeOf<ReferenceValue, ReferenceType>,
    crossinline breakExecutor: BreakExecutor,
): Result<Unit, InvocationError> = binding {

    val (stack, store) = context
    val frame = stack.peekFrame().bind()
    val moduleInstance = frame.state.module

    val referenceValue = stack.peekReference().bind()
    val closedReferenceType1 = typeOfReferenceValue(referenceValue, store, moduleInstance)
        .toResultOr { InvocationError.Trap.TrapEncountered }.bind()
    val closedReferenceType2 = referenceTypeSubstitutor(referenceType2, context.substitution())

    val referenceTypeMatches = referenceTypeMatcher(closedReferenceType1, closedReferenceType2, context)

    if (referenceTypeMatches == breakIfMatches) {
        breakExecutor(stack, labelIndex).bind()
    }
}
