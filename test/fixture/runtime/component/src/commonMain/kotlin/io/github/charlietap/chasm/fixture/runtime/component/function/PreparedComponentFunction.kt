package io.github.charlietap.chasm.fixture.runtime.component.function

import io.github.charlietap.chasm.fixture.runtime.component.canonical.canonicalValueTupleLayout
import io.github.charlietap.chasm.fixture.runtime.component.canonical.linearMemoryLiftPlan
import io.github.charlietap.chasm.fixture.type.component.componentFunctionType
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalValueTupleLayout
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLiftPlan
import io.github.charlietap.chasm.runtime.component.function.ComponentEntryPolicy
import io.github.charlietap.chasm.runtime.component.function.PreparedComponentFunction
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.type.component.ComponentFunctionType

fun liftedCoreComponentFunction(
    liftPlan: LinearMemoryLiftPlan = linearMemoryLiftPlan(),
    entryPolicy: ComponentEntryPolicy = componentEntryPolicy(liftPlan.optionOwner.index),
) = PreparedComponentFunction.LiftedCore(
    liftPlan = liftPlan,
    entryPolicy = entryPolicy,
)

fun hostImportComponentFunction(
    importSlot: Int = 0,
    owner: RuntimeComponentInstanceIndex = RuntimeComponentInstanceIndex(0),
    functionType: ComponentFunctionType = componentFunctionType(),
    parameterTuple: CanonicalValueTupleLayout = canonicalValueTupleLayout(),
    resultTuple: CanonicalValueTupleLayout = canonicalValueTupleLayout(),
    preparedHostCompatible: Boolean = true,
    entryPolicy: ComponentEntryPolicy = componentEntryPolicy(owner.index),
) = PreparedComponentFunction.HostImport(
    importSlot = importSlot,
    owner = owner,
    functionType = functionType,
    parameterTuple = parameterTuple,
    resultTuple = resultTuple,
    preparedHostCompatible = preparedHostCompatible,
    entryPolicy = entryPolicy,
)

fun componentEntryPolicy(vararg enteringInstances: Int) = ComponentEntryPolicy(enteringInstances)
