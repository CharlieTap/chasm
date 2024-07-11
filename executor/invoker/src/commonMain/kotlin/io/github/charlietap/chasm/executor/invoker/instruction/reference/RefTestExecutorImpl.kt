@file:Suppress("NOTHING_TO_INLINE")

package io.github.charlietap.chasm.executor.invoker.instruction.reference

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.ConcreteHeapType
import io.github.charlietap.chasm.ast.type.DefinedType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.invoker.ext.index
import io.github.charlietap.chasm.executor.invoker.type.TypeOf
import io.github.charlietap.chasm.executor.invoker.type.TypeOfReferenceValue
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.ext.peekFrame
import io.github.charlietap.chasm.executor.runtime.ext.popReference
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.matching.DefinedTypeLookup
import io.github.charlietap.chasm.type.matching.ReferenceTypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcher
import io.github.charlietap.chasm.type.matching.TypeMatcherContext
import io.github.charlietap.chasm.type.rolling.substitution.ConcreteHeapTypeSubstitutor
import io.github.charlietap.chasm.type.rolling.substitution.ReferenceTypeSubstitutorImpl
import io.github.charlietap.chasm.type.rolling.substitution.TypeSubstitutor

internal fun RefTestExecutorImpl(
    store: Store,
    stack: Stack,
    referenceType: ReferenceType,
): Result<Unit, InvocationError> = RefTestExecutorImpl(
    store = store,
    stack = stack,
    referenceType = referenceType,
    referenceTypeSubstitutor = ::ReferenceTypeSubstitutorImpl,
    referenceTypeMatcher = ::ReferenceTypeMatcher,
    typeOfReferenceValue = ::TypeOfReferenceValue,
)

internal inline fun RefTestExecutorImpl(
    store: Store,
    stack: Stack,
    referenceType: ReferenceType,
    crossinline referenceTypeSubstitutor: TypeSubstitutor<ReferenceType>,
    crossinline referenceTypeMatcher: TypeMatcher<ReferenceType>,
    crossinline typeOfReferenceValue: TypeOf<ReferenceValue, ReferenceType>,
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

    val substitutedReferenceType = referenceTypeSubstitutor(referenceType, substitution)

    val otherReferenceValue = stack.popReference().bind()
    val otherReferenceType = typeOfReferenceValue(otherReferenceValue, store, moduleInstance)
        .toResultOr { InvocationError.Trap.TrapEncountered }.bind()

    val context = object : TypeMatcherContext {
        override fun lookup(): DefinedTypeLookup = lookup

        override fun substitution(): ConcreteHeapTypeSubstitutor = substitution
    }

    if (referenceTypeMatcher(otherReferenceType, substitutedReferenceType, context)) {
        stack.pushValue(NumberValue.I32(1))
    } else {
        stack.pushValue(NumberValue.I32(0))
    }
}
