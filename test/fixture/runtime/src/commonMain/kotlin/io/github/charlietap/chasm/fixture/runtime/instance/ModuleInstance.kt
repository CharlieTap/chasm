package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ExportInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RTT

fun moduleInstance(
    types: List<DefinedType> = emptyList(),
    runtimeTypes: List<RTT> = emptyList(),
    functionAddresses: MutableList<Address.Function> = mutableListOf(),
    tableAddresses: MutableList<Address.Table> = mutableListOf(),
    memAddresses: MutableList<Address.Memory> = mutableListOf(),
    globalAddresses: MutableList<Address.Global> = mutableListOf(),
    tagAddresses: MutableList<Address.Tag> = mutableListOf(),
    elemAddresses: MutableList<Address.Element> = mutableListOf(),
    dataAddresses: MutableList<Address.Data> = mutableListOf(),
    exports: MutableList<ExportInstance> = mutableListOf(),
    deallocated: Boolean = false,
) = ModuleInstance(
    types = types,
    runtimeTypes = runtimeTypes,
    functionAddresses = functionAddresses,
    tableAddresses = tableAddresses,
    memAddresses = memAddresses,
    globalAddresses = globalAddresses,
    tagAddresses = tagAddresses,
    elemAddresses = elemAddresses,
    dataAddresses = dataAddresses,
    exports = exports,
    deallocated = deallocated,
)
