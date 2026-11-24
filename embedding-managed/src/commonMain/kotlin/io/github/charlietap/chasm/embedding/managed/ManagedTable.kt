package io.github.charlietap.chasm.embedding.managed

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.compiled.gc.ReferenceTable

@InternalChasmApi
class ManagedTable(
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
