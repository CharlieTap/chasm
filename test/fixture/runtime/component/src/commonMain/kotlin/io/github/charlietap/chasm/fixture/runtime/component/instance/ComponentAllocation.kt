package io.github.charlietap.chasm.fixture.runtime.component.instance

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.address.RuntimeInstanceId
import io.github.charlietap.chasm.runtime.component.instance.ComponentAllocation
import io.github.charlietap.chasm.runtime.instance.ModuleAllocation

fun componentAllocation(
    coreModules: List<ModuleAllocation> = emptyList(),
    stackFunctions: List<Address.Function> = emptyList(),
    providers: List<RuntimeInstanceId> = emptyList(),
    rootProviders: Set<ComponentRootAddress> = emptySet(),
    resourceTypes: IntArray = intArrayOf(),
) = ComponentAllocation(
    coreModules = coreModules,
    stackFunctions = stackFunctions,
    providers = providers,
    rootProviders = rootProviders,
    resourceTypes = resourceTypes,
)
