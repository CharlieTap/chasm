package io.github.charlietap.chasm.runtime.instance

import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.diagnostic.CompiledFrames
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap

data class ModuleInstance(
    val runtimeTypes: RuntimeTypeMap,
    val functionAddresses: MutableList<Address.Function> = [],
    val tableAddresses: MutableList<Address.Table> = [],
    val memAddresses: MutableList<Address.Memory> = [],
    val tagAddresses: MutableList<Address.Tag> = [],
    val globalAddresses: MutableList<Address.Global> = [],
    val elemAddresses: MutableList<Address.Element> = [],
    val dataAddresses: MutableList<Address.Data> = [],
    val exports: MutableList<ExportInstance> = [],
    var deallocated: Boolean = false,
) : HostModuleInstance {
    // Set before compiled code can run, so a trap can name the frames it ran.
    var compiledFrames: CompiledFrames? = null
}
