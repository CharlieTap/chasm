package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.reference.ArrayReference

abstract class ManagedArray : ArrayReference() {

    abstract fun storage(): Any

    abstract fun length(): Int
}
