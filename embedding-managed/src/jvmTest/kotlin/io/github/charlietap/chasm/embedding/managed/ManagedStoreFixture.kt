package io.github.charlietap.chasm.embedding.managed

import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.runtime.compiled.gc.ArrayLayout
import io.github.charlietap.chasm.runtime.compiled.gc.GcTypes
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.compiled.gc.ShapeLayouts
import io.github.charlietap.chasm.runtime.compiled.gc.StructLayout
import io.github.charlietap.chasm.runtime.store.Store

fun managedStore(store: Store): ManagedStore {
    return ManagedStore(store) { UnusedGcTypes() }
}

private class UnusedGcTypes : GcTypes {

    override val layouts: ShapeLayouts = UnusedShapeLayouts()

    override fun type(value: Any): Int {
        return -1
    }

    override fun drop() {
    }
}

private class UnusedShapeLayouts : ShapeLayouts {

    override fun struct(value: Any): StructLayout? {
        error("no GC types are registered")
    }

    override fun array(value: Any): ArrayLayout? {
        error("no GC types are registered")
    }

    override fun struct(type: HostGcType): StructLayout {
        error("no GC types are registered")
    }

    override fun array(type: HostGcType): ArrayLayout {
        error("no GC types are registered")
    }
}
