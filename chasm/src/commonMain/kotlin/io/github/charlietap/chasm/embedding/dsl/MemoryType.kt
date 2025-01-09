package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.ast.type.Limits
import io.github.charlietap.chasm.ast.type.MemoryType
import io.github.charlietap.chasm.ast.type.SharedStatus

class MemoryTypeBuilder {

    private var limits: Limits? = null
    var shared: Boolean = false

    fun limits(builder: LimitsBuilder.() -> Unit) {
        limits = LimitsBuilder().apply(builder).build()
    }

    fun build() = MemoryType(
        limits = requireNotNull(limits),
        shared = if (shared) SharedStatus.Shared else SharedStatus.Unshared,
    )
}
