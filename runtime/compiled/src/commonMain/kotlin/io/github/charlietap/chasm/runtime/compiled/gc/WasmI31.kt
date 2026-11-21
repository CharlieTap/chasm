package io.github.charlietap.chasm.runtime.compiled.gc

import kotlin.jvm.JvmField

class WasmI31(
    @JvmField val value: Int,
) : WasmEq() {

    override fun equals(other: Any?): Boolean {
        return other is WasmI31 && other.value == value
    }

    override fun hashCode(): Int {
        return value
    }
}
