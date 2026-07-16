package io.github.charlietap.chasm.runtime.component.resource

import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress

class ResourceTypeTable {

    private val entries = mutableListOf<RuntimeResourceType?>()

    val size: Int
        get() = entries.size

    fun define(type: RuntimeResourceType): RuntimeResourceTypeAddress {
        val address = RuntimeResourceTypeAddress(entries.size)
        entries += type
        return address
    }

    operator fun get(address: RuntimeResourceTypeAddress): RuntimeResourceType? = entries.getOrNull(address.address)

    fun discard(addresses: IntArray) {
        addresses.forEach { address ->
            entries[address] = null
        }
    }
}
