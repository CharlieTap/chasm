package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RTT

data class ModuleInstance(
    val types: List<DefinedType>,
    val runtimeTypes: List<RTT>,
    val functionAddresses: MutableList<Address.Function> = mutableListOf(),
    val tableAddresses: MutableList<Address.Table> = mutableListOf(),
    val memAddresses: MutableList<Address.Memory> = mutableListOf(),
    val tagAddresses: MutableList<Address.Tag> = mutableListOf(),
    val globalAddresses: MutableList<Address.Global> = mutableListOf(),
    val elemAddresses: MutableList<Address.Element> = mutableListOf(),
    val dataAddresses: MutableList<Address.Data> = mutableListOf(),
    val exports: MutableList<ExportInstance> = mutableListOf(),
    var deallocated: Boolean = false,
)
