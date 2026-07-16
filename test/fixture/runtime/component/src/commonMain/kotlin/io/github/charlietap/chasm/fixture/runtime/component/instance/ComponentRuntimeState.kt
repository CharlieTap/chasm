package io.github.charlietap.chasm.fixture.runtime.component.instance

import io.github.charlietap.chasm.runtime.component.function.RuntimeComponentHostFunction
import io.github.charlietap.chasm.runtime.component.instance.ComponentInstanceStates
import io.github.charlietap.chasm.runtime.component.instance.ComponentRuntimeState
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

fun componentRuntimeState(
    coreInstances: Array<ModuleInstance?> = emptyArray(),
    coreFunctions: IntArray = intArrayOf(),
    memories: IntArray = intArrayOf(),
    reallocs: IntArray = intArrayOf(),
    postReturns: IntArray = intArrayOf(),
    resourceTypes: IntArray = intArrayOf(),
    hostFunctions: Array<RuntimeComponentHostFunction?> = emptyArray(),
    states: ComponentInstanceStates = componentInstanceStates(),
    deallocated: Boolean = false,
) = ComponentRuntimeState(
    coreInstances = coreInstances,
    coreFunctions = coreFunctions,
    memories = memories,
    reallocs = reallocs,
    postReturns = postReturns,
    resourceTypes = resourceTypes,
    hostFunctions = hostFunctions,
    states = states,
    deallocated = deallocated,
)
