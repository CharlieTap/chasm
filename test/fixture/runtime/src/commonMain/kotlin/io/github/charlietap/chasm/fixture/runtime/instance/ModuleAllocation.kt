package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.address.RuntimeInstanceId
import io.github.charlietap.chasm.runtime.instance.ModuleAllocation

fun moduleAllocation(
    functionAddresses: List<Address.Function> = emptyList(),
    instructionAddresses: List<Address.Function> = emptyList(),
    tableAddresses: List<Address.Table> = emptyList(),
    memoryAddresses: List<Address.Memory> = emptyList(),
    tagAddresses: List<Address.Tag> = emptyList(),
    globalAddresses: List<Address.Global> = emptyList(),
    elementAddresses: List<Address.Element> = emptyList(),
    dataAddresses: List<Address.Data> = emptyList(),
    providers: List<RuntimeInstanceId> = emptyList(),
) = ModuleAllocation(
    functionAddresses = functionAddresses,
    instructionAddresses = instructionAddresses,
    tableAddresses = tableAddresses,
    memoryAddresses = memoryAddresses,
    tagAddresses = tagAddresses,
    globalAddresses = globalAddresses,
    elementAddresses = elementAddresses,
    dataAddresses = dataAddresses,
    providers = providers,
)
