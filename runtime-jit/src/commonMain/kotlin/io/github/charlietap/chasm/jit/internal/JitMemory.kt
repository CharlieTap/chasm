package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.MemoryInstance

internal class JitMemory(
    val address: Address.Memory,
    val instance: MemoryInstance,
) {

    override fun equals(other: Any?): Boolean {
        return other is JitMemory && other.address == address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}
