package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.compiled.gc.ReferenceGlobal
import io.github.charlietap.chasm.runtime.instance.GlobalInstance

internal sealed class JitGlobal(
    val address: Address.Global,
) {

    final override fun equals(other: Any?): Boolean {
        return other is JitGlobal && other.address == address
    }

    final override fun hashCode(): Int {
        return address.hashCode()
    }
}

internal class NumberJitGlobal(
    address: Address.Global,
    val instance: GlobalInstance,
) : JitGlobal(address)

internal class ReferenceJitGlobal(
    address: Address.Global,
    val storage: ReferenceGlobal,
) : JitGlobal(address)
