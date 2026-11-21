package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.compiled.gc.ReferenceTable

internal class JitTable(
    val address: Address.Table,
    val storage: ReferenceTable,
) {

    override fun equals(other: Any?): Boolean {
        return other is JitTable && other.address == address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}
