package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
class RttTypes(
    private val managedStore: ManagedStore,
    val registry: RttRegistry,
) : GcTypes {

    override val layouts: ShapeLayouts = RttShapeLayouts(managedStore, registry)

    override fun type(value: Any): Int {

        val rtt = when (value) {
            is CompiledStruct -> value.rtt
            is CompiledArray -> value.rtt
            else -> return -1
        }

        return if (rtt.engine === managedStore.engine) rtt.id else -1
    }

    override fun drop() {
        registry.clear()
    }
}
