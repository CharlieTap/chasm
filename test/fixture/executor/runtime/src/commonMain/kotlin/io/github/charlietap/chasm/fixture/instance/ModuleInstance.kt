package io.github.charlietap.chasm.fixture.instance

import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.executor.runtime.instance.ExportInstance
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Address

fun moduleInstance(
    types: List<FunctionType> = emptyList(),
    functionAddresses: MutableList<Address.Function> = mutableListOf(),
    tableAddresses: MutableList<Address.Table> = mutableListOf(),
    memAddresses: MutableList<Address.Memory> = mutableListOf(),
    globalAddresses: MutableList<Address.Global> = mutableListOf(),
    elemAddresses: MutableList<Address.Element> = mutableListOf(),
    dataAddresses: MutableList<Address.Data> = mutableListOf(),
    exports: MutableList<ExportInstance> = mutableListOf(),
) = ModuleInstance(
    types = types,
    functionAddresses = functionAddresses,
    tableAddresses = tableAddresses,
    memAddresses = memAddresses,
    globalAddresses = globalAddresses,
    elemAddresses = elemAddresses,
    dataAddresses = dataAddresses,
    exports = exports,
)
