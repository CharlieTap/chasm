package io.github.charlietap.chasm.embedding.managed

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.compiled.gc.ReferenceGlobal
import io.github.charlietap.chasm.runtime.instance.GlobalInstance

@InternalChasmApi
sealed class ManagedGlobal(
    val address: Address.Global,
) {

    final override fun equals(other: Any?): Boolean {
        return other is ManagedGlobal && other.address == address
    }

    final override fun hashCode(): Int {
        return address.hashCode()
    }
}

internal class NumberManagedGlobal(
    address: Address.Global,
    val instance: GlobalInstance,
) : ManagedGlobal(address)

internal class ReferenceManagedGlobal(
    address: Address.Global,
    val storage: ReferenceGlobal,
) : ManagedGlobal(address)
