package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.SharedStatus

class MemoryTypeBuilder {

    private var limits: Limits? = null
    var shared: Boolean = false

    fun limits(builder: LimitsBuilder.() -> Unit) {
        limits = LimitsBuilder().apply(builder).build()
    }

    fun build() = MemoryType(
        addressType = AddressType.I32,
        limits = requireNotNull(limits),
        shared = if (shared) SharedStatus.Shared else SharedStatus.Unshared,
    )
}
