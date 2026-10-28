package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.ext.data
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.global
import io.github.charlietap.chasm.runtime.ext.memory
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RTT
import java.util.IdentityHashMap

internal class ModuleSymbols(
    private val instance: ModuleInstance,
    store: Store,
) {

    private val names = IdentityHashMap<Any, String>()
    private val entries = HashMap<Int, String>()

    init {
        names[instance] = "module"
        names[store.fuel] = "fuel"
        names[store.interrupt] = "interrupt"
        instance.functionAddresses.forEachIndexed { index, address ->
            val function = store.function(address)
            names.putIfAbsent(function, "func[$index]")
            if (function is FunctionInstance.WasmFunction) {
                names.putIfAbsent(function.callStrategy, "func[$index]")
                entries.putIfAbsent(function.callStrategy.entryIp, "func[$index]")
            }
        }
        instance.tableAddresses.forEachIndexed { index, address -> names.putIfAbsent(store.table(address), "table[$index]") }
        instance.memAddresses.forEachIndexed { index, address -> names.putIfAbsent(store.memory(address), "memory[$index]") }
        instance.globalAddresses.forEachIndexed { index, address -> names.putIfAbsent(store.global(address), "global[$index]") }
        instance.elemAddresses.forEachIndexed { index, address -> names.putIfAbsent(store.element(address), "elem[$index]") }
        instance.dataAddresses.forEachIndexed { index, address -> names.putIfAbsent(store.data(address), "data[$index]") }
    }

    fun nameOf(value: Any): String? = names[value]

    fun entry(
        entryIp: Int,
    ): String = checkNotNull(entries[entryIp]) { "no function of the module starts at ip $entryIp" }

    fun type(rtt: RTT): String {
        val types = instance.runtimeTypes
        for (index in 0 until types.size) {
            if (types[index] == rtt) return "type[$index]"
        }
        error("runtime type ${rtt.value} is not defined by the module")
    }

    fun function(address: Address.Function): String = index("func", instance.functionAddresses.indexOf(address), address)

    fun tag(address: Address.Tag): String = index("tag", instance.tagAddresses.indexOf(address), address)

    fun tag(address: Int): String = tag(Address.Tag(address))

    private fun index(kind: String, index: Int, address: Address): String {
        check(index >= 0) { "$kind address ${address.address} is not part of the module" }
        return "$kind[$index]"
    }
}
