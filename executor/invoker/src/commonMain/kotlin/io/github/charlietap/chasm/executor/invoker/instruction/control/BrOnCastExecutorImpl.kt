@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.peekReference
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.executor.type.matching.DefinedTypeLookup
import io.github.charlietap.chasm.executor.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.executor.type.matching.TypeMatcher
import io.github.charlietap.chasm.executor.type.matching.TypeMatcherContext
import io.github.charlietap.chasm.executor.type.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.executor.type.rolling.substitution.ReferenceTypeSubstitutorImpl
import io.github.charlietap.chasm.executor.type.rolling.substitution.TypeSubstitutor
import io.github.charlietap.chasm.executor.type.value.TypeOf
import io.github.charlietap.chasm.executor.type.value.TypeOfReferenceValue

internal inline fun BrOnCastExecutorImpl(
    store: Store,
    stack: Stack,
    labelIndex: Index.LabelIndex,
    referenceType1: ReferenceType,
    referenceType2: ReferenceType,
    breakIfMatches: Boolean,
): Result<Unit, InvocationError> =
    BrOnCastExecutorImpl(
        store = store,
        stack = stack,
        labelIndex = labelIndex,
        referenceType1 = referenceType1,
        referenceType2 = referenceType2,
        breakIfMatches = breakIfMatches,
        referenceTypeSubstitutor = ::ReferenceTypeSubstitutorImpl,
        referenceTypeMatcher = ::ReferenceTypeMatcher,
        typeOfReferenceValue = ::TypeOfReferenceValue,
        breakExecutor = ::BreakExecutorImpl,
    )

@Suppress("UNUSED_PARAMETER")
internal inline fun BrOnCastExecutorImpl(
    store: Store,
    stack: Stack,
    labelIndex: Index.LabelIndex,
    referenceType1: ReferenceType,
    referenceType2: ReferenceType,
    breakIfMatches: Boolean,
    crossinline referenceTypeSubstitutor: TypeSubstitutor<ReferenceType>,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline typeOfReferenceValue: TypeOf<ReferenceValue, ReferenceType>,
    crossinline breakExecutor: BreakExecutor,
): Result<Unit, InvocationError> = binding {

    val frame = stack.peekFrame().bind()
    val moduleInstance = frame.state.module

    val lookup: (Index.TypeIndex) -> DefinedType = { index ->
        moduleInstance.types[index.index()]
    }

    val substitution: ConcreteHeapTypeSubstitutor = { concreteHeapType ->
        when (concreteHeapType) {
            is ConcreteHeapType.TypeIndex -> if (concreteHeapType.index.index() < moduleInstance.types.size) {
                ConcreteHeapType.Defined(moduleInstance.types[concreteHeapType.index.index()])
            } else {
                concreteHeapType
            }
            else -> concreteHeapType
        }
    }

    val referenceValue = stack.peekReference().bind()
    val closedReferenceType1 = typeOfReferenceValue(referenceValue, store, moduleInstance)
        .toResultOr { InvocationError.Trap.TrapEncountered }.bind()
    val closedReferenceType2 = referenceTypeSubstitutor(referenceType2, substitution)

    val context = object : TypeMatcherContext {
        override fun lookup(): DefinedTypeLookup = lookup

        override fun substitution(): ConcreteHeapTypeSubstitutor = substitution
    }

    val referenceTypeMatches = referenceTypeMatcher(closedReferenceType1, closedReferenceType2, context)

    if (referenceTypeMatches == breakIfMatches) {
        breakExecutor(stack, labelIndex).bind()
    }
}
