package io.github.charlietap.chasm.runtime.compiled.gc

abstract class WasmArray : WasmEq() {

    abstract fun storage(): Any

    abstract fun length(): Int
}
