package io.github.charlietap.chasm.fixture.executor.runtime.instance

import io.github.charlietap.chasm.executor.runtime.instance.ExportInstance
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.ir.type.DefinedType

fun moduleInstance(
    types: List<DefinedType> = emptyList(),
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
