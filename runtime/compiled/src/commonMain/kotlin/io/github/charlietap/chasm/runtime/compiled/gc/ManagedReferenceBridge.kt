package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostExternReference
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.host.HostGlobal
import io.github.charlietap.chasm.host.HostReferenceRoot
import io.github.charlietap.chasm.host.HostTable
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_ARRAY
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXCEPTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXTERN
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_FUNCTION
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_HOST
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_I31
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_NULL
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_STRUCT
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.HostRaisedWasmException
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.ext.function
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instance.ElementInstance
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.reference.ReferenceBridge
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.ValueType
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.IdentityHashMap
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.JvmField

class ManagedReferenceBridge(
    private val store: Store,
) : ReferenceBridge, ShapeRegistry, ReferenceStorage, PendingExceptions {

    private val handles = HashMap<Int, MutableList<Int>>()
    private val legacyHostValues = HashMap<Long, LegacyHostValue>()
    private val slots = ArrayList<Handle?>()
    private val freeHandles = ArrayList<Int>()
    private val collected = ReferenceQueue<Any>()
    private var results = identitySet()
    private var previousResults = identitySet()
    private val globals = IdentityHashMap<GlobalInstance, ReferenceGlobal>()
    private val tables = IdentityHashMap<TableInstance, ReferenceTable>()
    private val segments = IdentityHashMap<ElementInstance, ReferenceSegment>()
    private val shapes = ConcurrentHashMap<Int, Class<*>>()
    private val types = ConcurrentHashMap<Class<*>, Int>()
    private val definedTypes = ConcurrentHashMap<Int, DefinedType>()
    private var raisedException: Any? = null

    lateinit var engine: Any

    val functions = ManagedFunctions(store)

    val checks = StoreReferenceChecks(this, functions, store)

    val layouts = ReflectedShapeLayouts(this, store.heap, checks)

    val allocator = ManagedAllocator(store)

    val objects = ManagedObjects(
        structs = StructOperations(layouts, allocator),
        arrays = ArrayOperations(layouts, allocator),
        exceptions = ExceptionOperations(store, checks, this),
        tables = TableOperations(this, checks),
        globals = GlobalOperations(this, checks),
    )

    override val references = ManagedReferences(this)

    override val gc = ManagedGc(this, store)

    override val externs = ManagedExterns(this)

    override val exceptions = ManagedExceptions(this, store)

    override fun global(global: GlobalInstance): HostGlobal {

        val type = global.type.valueType as? ValueType.Reference ?: return global

        return ManagedHostGlobal(this, referenceGlobal(global), type.referenceType.heapType.isExtern())
    }

    override fun table(table: TableInstance): HostTable {
        return ManagedHostTable(this, referenceTable(table))
    }

    override fun growTable(
        table: TableInstance,
        elementsToAdd: Int,
        value: Long,
    ): Int {
        return tableGrow(referenceTable(table), elementsToAdd, fromHost(value))
    }

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
    override fun referenceGlobal(global: GlobalInstance): ReferenceGlobal {
        return globals.getOrPut(global) {
            require(store.globals.any { candidate -> candidate === global }) { "not a global of this store" }
            ReferenceGlobal(fromHost(global.value))
        }
    }

    @Synchronized
    override fun referenceTable(table: TableInstance): ReferenceTable {
        return tables.getOrPut(table) {
            require(store.tables.any { candidate -> candidate === table }) { "not a table of this store" }
            ReferenceTable(Array(table.elements.size) { index -> fromHost(table.elements[index]) }, table)
        }
    }

    @Synchronized
    fun referenceSegment(element: ElementInstance): ReferenceSegment {
        return segments.getOrPut(element) { ReferenceSegment(Array(element.elements.size) { index -> fromHost(element.elements[index]) }) }
    }

    @Synchronized
    override fun toHost(
        value: Any?,
        extern: Boolean,
    ): Long {

        val encoded = when (value) {
            null -> return if (extern) NULL_EXTERN else NULL_ANY
            is I31Reference -> ReferenceValue.I31(value.value.toUInt()).toLong()
            is ManagedFunction -> ReferenceValue.Function(Address.Function(value.address)).toLong()
            is ManagedStruct -> encode(RV_TYPE_STRUCT, escape(value))
            is ManagedArray -> encode(RV_TYPE_ARRAY, escape(value))
            is WasmException -> encode(RV_TYPE_EXCEPTION, escape(value))
            is LegacyHostValue -> value.encoded
            else -> encode(RV_TYPE_HOST, escape(value))
        }

        return if (extern) encode(RV_TYPE_EXTERN, encoded) else encoded
    }

    @Synchronized
    override fun fromHost(encoded: Long): Any? {

        val payload = encoded ushr RV_SHIFT_BITS
        return when (encoded and RV_TYPE_MASK) {
            RV_TYPE_NULL -> null
            RV_TYPE_I31 -> I31Reference(payload.toInt())
            RV_TYPE_FUNCTION -> functions.at(payload.toInt())
            RV_TYPE_EXTERN -> fromHost(payload)
            RV_TYPE_HOST -> if (payload < FIRST_HANDLE) legacyHostValue(encoded) else objectAt(payload)
            else -> objectAt(payload)
        }
    }

    @Synchronized
    override fun toHostResult(
        value: Any?,
        extern: Boolean,
    ): Long {
        if (value != null) results += value
        return toHost(value, extern)
    }

    override fun raise(exception: WasmException): Nothing {

        store.heap.setPendingException(toHostResult(exception, false))

        throw HostRaisedWasmException.instance
    }

    @Synchronized
    override fun take(): WasmException {
        return fromHost(store.heap.takePendingExceptionReference()) as WasmException
    }

    @Synchronized
    fun holdRaised(encoded: Long) {
        raisedException = fromHost(encoded)
    }

    @Synchronized
    fun releaseRaised() {
        raisedException = null
    }

    override fun retainExtern(value: Any): HostReferenceRoot {
        return references.retainObject(value, toHost(value, true))
    }

    @Synchronized
    override fun release(global: GlobalInstance) {
        globals.remove(global)?.value = null
    }

    @Synchronized
    override fun release(table: TableInstance) {
        tables.remove(table)?.elements = emptyArray()
    }

    @Synchronized
    override fun release(element: ElementInstance) {
        segments.remove(element)?.elements = emptyArray()
    }

    @Synchronized
    override fun releaseEscaped() {
        previousResults = results
        results = identitySet()
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

    private fun escape(value: Any): Long {

        reclaimHandles()

        val identity = System.identityHashCode(value)
        val candidates = handles[identity]
        candidates?.forEach { handle ->
            if (slots[handle - FIRST_HANDLE.toInt()]?.get() === value) {
                return handle.toLong()
            }
        }

        val handle = newHandle(value, identity)
        handles.getOrPut(identity) { ArrayList(1) }.add(handle)

        return handle.toLong()
    }

    private fun newHandle(
        value: Any,
        identity: Int,
    ): Int {

        val handle = if (freeHandles.isEmpty()) FIRST_HANDLE.toInt() + slots.size else freeHandles.removeAt(freeHandles.size - 1)
        val slot = handle - FIRST_HANDLE.toInt()
        val reference = Handle(value, handle, identity, collected)

        if (slot == slots.size) {
            slots += reference
        } else {
            slots[slot] = reference
        }

        return handle
    }

    private fun reclaimHandles() {
        while (true) {

            val reference = collected.poll() as? Handle ?: return
            val slot = reference.handle - FIRST_HANDLE.toInt()

            if (slots.getOrNull(slot) === reference) {

                slots[slot] = null
                freeHandles += reference.handle

                val candidates = handles[reference.identity]
                candidates?.remove(reference.handle)
                if (candidates != null && candidates.isEmpty()) {
                    handles.remove(reference.identity)
                }
            }
        }
    }

    private fun objectAt(handle: Long): Any {
        return slots.getOrNull((handle - FIRST_HANDLE).toInt())?.get() ?: throw InvocationException(InvocationError.UnexpectedReferenceValue)
    }

    private fun legacyHostValue(encoded: Long): LegacyHostValue {
        return legacyHostValues.getOrPut(encoded) { LegacyHostValue(encoded) }
    }

    fun hostValue(value: Any): Any {
        return if (value is LegacyHostValue) {
            store.heap.value(HostExternReference((value.encoded shl RV_SHIFT_BITS) or RV_TYPE_EXTERN))
        } else {
            value
        }
    }

    private fun encode(
        tag: Long,
        payload: Long,
    ): Long {
        return (payload shl RV_SHIFT_BITS) or tag
    }

    private fun identitySet(): MutableSet<Any> {
        return Collections.newSetFromMap(IdentityHashMap())
    }

    private fun HeapType.isExtern(): Boolean {
        return this == AbstractHeapType.Extern || this == AbstractHeapType.NoExtern
    }

    private class Handle(
        value: Any,
        val handle: Int,
        val identity: Int,
        queue: ReferenceQueue<Any>,
    ) : WeakReference<Any>(value, queue)

    private companion object {
        const val FIRST_HANDLE = 1L shl 30
        val NULL_ANY = ReferenceValue.Null(AbstractHeapType.Any).toLong()
        val NULL_EXTERN = ReferenceValue.Null(AbstractHeapType.Extern).toLong()
    }
}

class LegacyHostValue(
    @JvmField val encoded: Long,
)
