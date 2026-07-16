package io.github.charlietap.chasm.runtime.component.canonical

import io.github.charlietap.chasm.runtime.component.function.ComponentEntryPolicy
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.type.component.ComponentFunctionType

sealed interface CanonicalCallPlan

data class LinearMemoryLiftPlan(
    val functionType: ComponentFunctionType,
    val optionOwner: RuntimeComponentInstanceIndex,
    val coreFunctionSlot: Int,
    val parameterTuple: CanonicalValueTupleLayout = CanonicalValueTupleLayout.Empty,
    val resultTuple: CanonicalValueTupleLayout = CanonicalValueTupleLayout.Empty,
    val parameterPassing: LiftParameterPassing,
    val resultPassing: LiftResultPassing,
    val encoding: CanonicalStringEncoding,
    val memorySlot: Int = ABSENT_RUNTIME_SLOT,
    val reallocSlot: Int = ABSENT_RUNTIME_SLOT,
    val postReturnSlot: Int = ABSENT_RUNTIME_SLOT,
) : CanonicalCallPlan

data class LinearMemoryLowerPlan(
    val functionType: ComponentFunctionType,
    val optionOwner: RuntimeComponentInstanceIndex,
    val targetFunctionSlot: Int,
    val entryPolicy: ComponentEntryPolicy,
    val parameterTuple: CanonicalValueTupleLayout = CanonicalValueTupleLayout.Empty,
    val resultTuple: CanonicalValueTupleLayout = CanonicalValueTupleLayout.Empty,
    val parameterPassing: LowerParameterPassing,
    val resultPassing: LowerResultPassing,
    val encoding: CanonicalStringEncoding,
    val memorySlot: Int = ABSENT_RUNTIME_SLOT,
    val reallocSlot: Int = ABSENT_RUNTIME_SLOT,
    val fusedTarget: LinearMemoryLiftPlan? = null,
) : CanonicalCallPlan

enum class LiftParameterPassing {
    Direct,
    IndirectTuple,
}

enum class LiftResultPassing {
    Direct,
    IndirectPointer,
}

enum class LowerParameterPassing {
    Direct,
    IndirectPointer,
}

enum class LowerResultPassing {
    Direct,
    IndirectPointer,
}

private const val ABSENT_RUNTIME_SLOT = -1
