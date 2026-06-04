package io.github.charlietap.chasm.fixture.runtime.instance

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ExportInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RTT

fun moduleInstance(
    runtimeTypes: List<RTT> = [],
    functionAddresses: MutableList<Address.Function> = [],
    tableAddresses: MutableList<Address.Table> = [],
    memAddresses: MutableList<Address.Memory> = [],
    globalAddresses: MutableList<Address.Global> = [],
    tagAddresses: MutableList<Address.Tag> = [],
    elemAddresses: MutableList<Address.Element> = [],
    dataAddresses: MutableList<Address.Data> = [],
    exports: MutableList<ExportInstance> = [],
    deallocated: Boolean = false,
) = ModuleInstance(
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
