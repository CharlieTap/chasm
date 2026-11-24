package io.github.charlietap.chasm.embedding.managed

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.MemoryInstance

@InternalChasmApi
class ManagedMemory(
    val address: Address.Memory,
    val instance: MemoryInstance,
) {

    override fun equals(other: Any?): Boolean {
        return other is ManagedMemory && other.address == address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}
