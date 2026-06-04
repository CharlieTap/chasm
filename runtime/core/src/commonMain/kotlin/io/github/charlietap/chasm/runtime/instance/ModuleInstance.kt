package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RTT

data class ModuleInstance(
    val runtimeTypes: List<RTT>,
    val functionAddresses: MutableList<Address.Function> = [],
    val tableAddresses: MutableList<Address.Table> = [],
    val memAddresses: MutableList<Address.Memory> = [],
    val tagAddresses: MutableList<Address.Tag> = [],
    val globalAddresses: MutableList<Address.Global> = [],
    val elemAddresses: MutableList<Address.Element> = [],
    val dataAddresses: MutableList<Address.Data> = [],
    val exports: MutableList<ExportInstance> = [],
    var deallocated: Boolean = false,
)
