package io.github.charlietap.chasm.ast.module

import io.github.charlietap.chasm.ast.value.NameValue

data class Custom(val name: NameValue, val data: UByteArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (other !is Custom) return false
        if (name != other.name) return false
        return true
    }

    override fun hashCode(): Int = name.hashCode()
}
