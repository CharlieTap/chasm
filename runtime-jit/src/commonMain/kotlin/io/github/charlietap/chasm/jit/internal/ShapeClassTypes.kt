package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.runtime.compiled.gc.GcTypes
import io.github.charlietap.chasm.runtime.compiled.gc.ManagedStore
import io.github.charlietap.chasm.runtime.compiled.gc.ReflectedShapeLayouts
import io.github.charlietap.chasm.runtime.compiled.gc.ShapeAccessors
import io.github.charlietap.chasm.runtime.compiled.gc.ShapeLayouts
import io.github.charlietap.chasm.runtime.compiled.gc.ShapeRegistry
import io.github.charlietap.chasm.runtime.heap.WasmHeap
import io.github.charlietap.chasm.type.DefinedType
import java.util.concurrent.ConcurrentHashMap

internal class ShapeClassTypes(
    private val managedStore: ManagedStore,
    heap: WasmHeap,
    accessors: ShapeAccessors,
) : GcTypes,
    ShapeRegistry {

    private val shapes = ConcurrentHashMap<Int, Class<*>>()
    private val types = ConcurrentHashMap<Class<*>, Int>()

    override val layouts: ShapeLayouts = ReflectedShapeLayouts(this, heap, managedStore.checks, accessors)

    fun register(
        type: Int,
        shape: Class<*>,
    ) {
        shapes.putIfAbsent(type, shape)
        types.putIfAbsent(shape, type)
    }

    override fun shape(type: HostGcType): Class<*>? {
        return shapes[type.id]
    }

    override fun definedType(type: HostGcType): DefinedType? {
        return managedStore.definedType(type)
    }

    override fun type(value: Any): Int {

        var shape: Class<*>? = value.javaClass
        while (shape != null) {
            types[shape]?.let { type -> return type }
            shape = shape.superclass
        }

        return -1
    }

    override fun drop() {
        shapes.clear()
        types.clear()
    }
}
