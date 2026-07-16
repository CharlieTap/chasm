package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.address.RuntimeInstanceId

data class ModuleAllocation(
    val functionAddresses: List<Address.Function> = emptyList(),
    val instructionAddresses: List<Address.Function> = emptyList(),
    val tableAddresses: List<Address.Table> = emptyList(),
    val memoryAddresses: List<Address.Memory> = emptyList(),
    val tagAddresses: List<Address.Tag> = emptyList(),
    val globalAddresses: List<Address.Global> = emptyList(),
    val elementAddresses: List<Address.Element> = emptyList(),
    val dataAddresses: List<Address.Data> = emptyList(),
    val providers: List<RuntimeInstanceId> = emptyList(),
)
