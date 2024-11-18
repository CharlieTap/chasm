package io.github.charlietap.chasm.embedding.dsl

import io.github.charlietap.chasm.embedding.shapes.Limits
import io.github.charlietap.chasm.embedding.shapes.MemoryType

class MemoryTypeBuilder {

    private var limits: Limits? = null
    var shared: Boolean = false

    fun limits(builder: LimitsBuilder.() -> Unit) {
        limits = LimitsBuilder().apply(builder).build()
    }

    fun build() = MemoryType(requireNotNull(limits), shared)
}
