package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.exception.InvocationException
import io.github.charlietap.chasm.executor.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.executor.runtime.ext.peekReference
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.rolling.substitution.ReferenceTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

internal typealias BrOnCastExecutor = (ExecutionContext, Index.LabelIndex, ReferenceType, ReferenceType, Boolean) -> Result<Unit, InvocationError>

internal fun BrOnCastExecutor(
    context: ExecutionContext,
    instruction: ControlInstruction.BrOnCast,
) = BrOnCastExecutor(
    context = context,
    labelIndex = instruction.labelIndex,
    referenceType1 = instruction.srcReferenceType,
    referenceType2 = instruction.dstReferenceType,
    breakIfMatches = true,
    referenceTypeSubstitutor = ::ReferenceTypeSubstitutor,
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
) {

    val (stack, store) = context
    val frame = stack.peekFrame()
    val moduleInstance = frame.instance

    val referenceValue = stack.peekReference()
    val closedReferenceType1 = typeOfReferenceValue(referenceValue, store, moduleInstance)
        ?: throw InvocationException(InvocationError.FailedToGetTypeOfReferenceValue)
    val closedReferenceType2 = referenceTypeSubstitutor(referenceType2, context.substitutor)

    val referenceTypeMatches = referenceTypeMatcher(closedReferenceType1, closedReferenceType2, context)

    if (referenceTypeMatches == breakIfMatches) {
        breakExecutor(stack, labelIndex)
    }
}
