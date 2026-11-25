package io.github.charlietap.chasm.embedding.compiled.internal

import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.compiled.gc.ReferenceTable

internal class ManagedTable(
    val address: Address.Table,
    val storage: ReferenceTable,
) {

    override fun equals(other: Any?): Boolean {
        return other is ManagedTable && other.address == address
    }

    override fun hashCode(): Int {
        return address.hashCode()
    }
}
