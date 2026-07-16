package io.github.charlietap.chasm.fixture.runtime.component.canonical

import io.github.charlietap.chasm.fixture.runtime.component.index.runtimeComponentInstanceIndex
import io.github.charlietap.chasm.fixture.type.component.componentFunctionType
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalStringEncoding
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalValueTupleLayout
import io.github.charlietap.chasm.runtime.component.canonical.LiftParameterPassing
import io.github.charlietap.chasm.runtime.component.canonical.LiftResultPassing
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLiftPlan
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLowerPlan
import io.github.charlietap.chasm.runtime.component.canonical.LowerParameterPassing
import io.github.charlietap.chasm.runtime.component.canonical.LowerResultPassing
import io.github.charlietap.chasm.runtime.component.function.ComponentEntryPolicy
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.type.component.ComponentFunctionType

fun canonicalValueTupleLayout(
    layouts: IntArray = intArrayOf(),
    offsets32: UIntArray = uintArrayOf(),
    size32: UInt = 0u,
    alignment32: UInt = 1u,
    flatCount: Int = 0,
) = CanonicalValueTupleLayout(
    layouts = layouts,
    offsets32 = offsets32,
    size32 = size32,
    alignment32 = alignment32,
    flatCount = flatCount,
)

fun linearMemoryLiftPlan(
    functionType: ComponentFunctionType = componentFunctionType(),
    optionOwner: RuntimeComponentInstanceIndex = runtimeComponentInstanceIndex(),
    coreFunctionSlot: Int = 0,
    parameterTuple: CanonicalValueTupleLayout = CanonicalValueTupleLayout.Empty,
    resultTuple: CanonicalValueTupleLayout = CanonicalValueTupleLayout.Empty,
    parameterPassing: LiftParameterPassing = LiftParameterPassing.Direct,
    resultPassing: LiftResultPassing = LiftResultPassing.Direct,
    encoding: CanonicalStringEncoding = CanonicalStringEncoding.Utf8,
    memorySlot: Int = -1,
    reallocSlot: Int = -1,
    postReturnSlot: Int = -1,
) = LinearMemoryLiftPlan(
    functionType = functionType,
    optionOwner = optionOwner,
    coreFunctionSlot = coreFunctionSlot,
    parameterTuple = parameterTuple,
    resultTuple = resultTuple,
    parameterPassing = parameterPassing,
    resultPassing = resultPassing,
    encoding = encoding,
    memorySlot = memorySlot,
    reallocSlot = reallocSlot,
    postReturnSlot = postReturnSlot,
)

fun linearMemoryLowerPlan(
    functionType: ComponentFunctionType = componentFunctionType(),
    optionOwner: RuntimeComponentInstanceIndex = runtimeComponentInstanceIndex(),
    targetFunctionSlot: Int = 0,
    entryPolicy: ComponentEntryPolicy = ComponentEntryPolicy(intArrayOf()),
    parameterTuple: CanonicalValueTupleLayout = CanonicalValueTupleLayout.Empty,
    resultTuple: CanonicalValueTupleLayout = CanonicalValueTupleLayout.Empty,
    parameterPassing: LowerParameterPassing = LowerParameterPassing.Direct,
    resultPassing: LowerResultPassing = LowerResultPassing.Direct,
    encoding: CanonicalStringEncoding = CanonicalStringEncoding.Utf8,
    memorySlot: Int = -1,
    reallocSlot: Int = -1,
    fusedTarget: LinearMemoryLiftPlan? = null,
) = LinearMemoryLowerPlan(
    functionType = functionType,
    optionOwner = optionOwner,
    targetFunctionSlot = targetFunctionSlot,
    entryPolicy = entryPolicy,
    parameterTuple = parameterTuple,
    resultTuple = resultTuple,
    parameterPassing = parameterPassing,
    resultPassing = resultPassing,
    encoding = encoding,
    memorySlot = memorySlot,
    reallocSlot = reallocSlot,
    fusedTarget = fusedTarget,
)
