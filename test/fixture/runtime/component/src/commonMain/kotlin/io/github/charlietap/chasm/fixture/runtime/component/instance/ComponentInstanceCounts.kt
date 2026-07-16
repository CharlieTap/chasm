package io.github.charlietap.chasm.fixture.runtime.component.instance

import io.github.charlietap.chasm.runtime.component.instance.ComponentInstanceCounts

fun componentInstanceCounts(
    componentInstances: Int = 0,
    coreInstances: Int = 0,
    coreFunctions: Int = 0,
    memories: Int = 0,
    reallocs: Int = 0,
    postReturns: Int = 0,
    resourceTypes: Int = 0,
    hostFunctions: Int = 0,
) = ComponentInstanceCounts(
    componentInstances = componentInstances,
    coreInstances = coreInstances,
    coreFunctions = coreFunctions,
    memories = memories,
    reallocs = reallocs,
    postReturns = postReturns,
    resourceTypes = resourceTypes,
    hostFunctions = hostFunctions,
)
