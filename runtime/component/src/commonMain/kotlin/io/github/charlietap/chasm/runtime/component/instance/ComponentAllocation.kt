package io.github.charlietap.chasm.runtime.component.instance

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.address.RuntimeInstanceId
import io.github.charlietap.chasm.runtime.instance.ModuleAllocation

data class ComponentAllocation(
    val coreModules: List<ModuleAllocation> = emptyList(),
    val stackFunctions: List<Address.Function> = emptyList(),
    val providers: List<RuntimeInstanceId> = emptyList(),
    val rootProviders: Set<ComponentRootAddress> = emptySet(),
    val resourceTypes: IntArray = intArrayOf(),
)
