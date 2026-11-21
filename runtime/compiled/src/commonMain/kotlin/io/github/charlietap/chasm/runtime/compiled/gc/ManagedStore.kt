package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXTERN
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_FUNCTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_I31
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.reference.ReferenceStorage
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.DefinedType
import java.util.IdentityHashMap
import java.util.concurrent.ConcurrentHashMap

class ManagedStore(
    private val store: Store,
    accessors: ShapeAccessors,
) : ReferenceStorage, ShapeRegistry {

    private val globals = IdentityHashMap<GlobalInstance, ReferenceGlobal>()
    private val tables = IdentityHashMap<TableInstance, ReferenceTable>()
    private val segments = IdentityHashMap<ElementInstance, ReferenceSegment>()
    private val shapes = ConcurrentHashMap<Int, Class<*>>()
    private val types = ConcurrentHashMap<Class<*>, Int>()
    private val definedTypes = ConcurrentHashMap<Int, DefinedType>()

    lateinit var engine: Any

    val functions = ManagedFunctions(store)

    val checks = StoreReferenceChecks(this, functions, store)

    val layouts = ReflectedShapeLayouts(this, store.heap, checks, accessors)

    val allocator = ManagedAllocator(store)

    val pending = PendingExceptionSlot()

    val objects = ManagedObjects(
        structs = StructOperations(layouts, allocator),
        arrays = ArrayOperations(layouts, allocator),
        exceptions = ExceptionOperations(store, checks, pending),
        tables = TableOperations(checks),
        globals = GlobalOperations(checks),
    )

    override fun initializeTable(
        table: TableInstance,
        element: ElementInstance,
        offset: Int,
        count: Int,
    ) {
        referenceSegment(element).elements.copyInto(referenceTable(table).elements, offset, 0, count)
    }

    override fun dropElement(element: ElementInstance) {
        referenceSegment(element).elements = emptyArray()
    }

    @Synchronized
    fun referenceGlobal(global: GlobalInstance): ReferenceGlobal {

        val storage = globals.getOrPut(global) {
            require(store.globals.any { candidate -> candidate === global }) { "not a global of this store" }
            ReferenceGlobal(decodeReference(global.value), global)
        }
        check(!storage.released) { "the global's instance has been dropped" }

        return storage
    }

    @Synchronized
    fun referenceTable(table: TableInstance): ReferenceTable {

        val storage = tables.getOrPut(table) {
            require(store.tables.any { candidate -> candidate === table }) { "not a table of this store" }
            ReferenceTable(Array(table.elements.size) { index -> decodeReference(table.elements[index]) }, table)
        }
        check(!storage.released) { "the table's instance has been dropped" }

        return storage
    }

    @Synchronized
    fun referenceSegment(element: ElementInstance): ReferenceSegment {
        return segments.getOrPut(element) { ReferenceSegment(Array(element.elements.size) { index -> decodeReference(element.elements[index]) }) }
    }

    private fun decodeReference(encoded: Long): Any? {

        val payload = encoded ushr RV_SHIFT_BITS
        return when (encoded and RV_TYPE_MASK) {
            RV_TYPE_NULL -> null
            RV_TYPE_I31 -> I31Reference(payload.toInt())
            RV_TYPE_FUNCTION -> functions.at(payload.toInt())
            RV_TYPE_EXTERN -> decodeReference(payload)
            else -> throw InvocationException(InvocationError.UnexpectedReferenceValue)
        }
    }

    @Synchronized
    override fun release(global: GlobalInstance) {

        val storage = globals[global] ?: return
        storage.value = null
        storage.released = true
    }

    @Synchronized
    override fun release(table: TableInstance) {

        val storage = tables[table] ?: return
        storage.elements = emptyArray()
        storage.released = true
    }

    @Synchronized
    override fun release(element: ElementInstance) {
        segments.remove(element)?.elements = emptyArray()
    }

    @Synchronized
    fun drop() {
        globals.clear()
        tables.clear()
        segments.clear()
        pending.clear()
    }

    fun register(
        type: Int,
        definedType: DefinedType,
        shape: Class<*>?,
    ) {

        definedTypes.putIfAbsent(type, definedType)

        if (shape != null) {
            shapes.putIfAbsent(type, shape)
            types.putIfAbsent(shape, type)
        }
    }

    override fun shape(type: HostGcType): Class<*>? {
        return shapes[type.id]
    }

    override fun definedType(type: HostGcType): DefinedType? {
        return definedTypes[type.id]
    }

    override fun type(value: Any): Int {

        var shape: Class<*>? = value.javaClass
        while (shape != null) {
            types[shape]?.let { type -> return type }
            shape = shape.superclass
        }

        return -1
    }
}
