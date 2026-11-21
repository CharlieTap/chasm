package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.reference.ReferenceOwner
import io.github.charlietap.chasm.reference.StructReference

abstract class ManagedStruct :
    StructReference(),
    ReferenceOwner {

    override fun owner(): Any {
        return ShapeOwners.owner(javaClass)
    }
}
