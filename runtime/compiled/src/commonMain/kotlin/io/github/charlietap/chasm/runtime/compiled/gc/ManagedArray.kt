package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ReferenceOwner

abstract class ManagedArray :
    ArrayReference(),
    ReferenceOwner {

    abstract fun storage(): Any

    abstract fun length(): Int
}
