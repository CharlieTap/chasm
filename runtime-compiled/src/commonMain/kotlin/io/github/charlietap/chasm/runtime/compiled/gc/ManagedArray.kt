package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.reference.ArrayReference

@InternalChasmApi
abstract class ManagedArray : ArrayReference() {

    abstract fun storage(): Any

    abstract fun length(): Int
}
