package io.github.charlietap.chasm.embedding.compiled.internal

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.MemoryInstance

internal class ManagedMemory(
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
