package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.ReferenceOwner
import io.github.charlietap.chasm.runtime.store.Store
import kotlin.jvm.JvmField

class WasmException(
    @JvmField val store: Store,
    @JvmField val tag: Int,
    @JvmField val words: LongArray,
    @JvmField val references: Array<Any?>,
) : ExceptionReference(),
    ReferenceOwner {

    override fun owner(): Any {
        val managedStore = checkNotNull(store.referenceStorage as ManagedStore?) { "the store of this exception has been dropped" }
        return managedStore.engine
    }
}
