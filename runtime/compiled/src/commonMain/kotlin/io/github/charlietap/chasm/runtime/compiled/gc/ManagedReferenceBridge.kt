package io.github.charlietap.chasm.runtime.compiled.gc

import io.github.charlietap.chasm.host.HostGlobal
import io.github.charlietap.chasm.host.HostReferenceRoot
import io.github.charlietap.chasm.host.HostTable
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
import io.github.charlietap.chasm.type.HeapType
import io.github.charlietap.chasm.type.ValueType
import java.lang.ref.ReferenceQueue
import java.lang.ref.WeakReference
import java.util.Collections
import java.util.IdentityHashMap
import java.util.WeakHashMap

class ManagedReferenceBridge(
    private val store: Store,
) : ReferenceBridge {

    private val handles = WeakHashMap<Any, Int>()
    private val objects = ArrayList<Handle?>()
    private val freeHandles = ArrayList<Int>()
    private val collected = ReferenceQueue<Any>()
    private var results = identitySet()
    private var previousResults = identitySet()
    private val functions = IdentityHashMap<FunctionInstance, Int>()
    private val legacyHostValues = HashMap<Long, LegacyHostValue>()
    private val globals = IdentityHashMap<GlobalInstance, ReferenceGlobal>()
    private val tables = IdentityHashMap<TableInstance, ReferenceTable>()
    private val segments = IdentityHashMap<ElementInstance, ReferenceSegment>()
    private val shapes = HashMap<Int, Class<*>>()
    private val types = IdentityHashMap<Class<*>, Int>()
    private var raisedException: Any? = null

    override val references = ManagedReferences(this)

    override val gc = ManagedGc(this, store)

    override val externs = ManagedExterns(this, store)

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
    fun referenceGlobal(global: GlobalInstance): ReferenceGlobal {
        return globals.getOrPut(global) { ReferenceGlobal(fromHost(global.value)) }
    }

    @Synchronized
    fun referenceTable(table: TableInstance): ReferenceTable {
        return tables.getOrPut(table) { ReferenceTable(Array(table.elements.size) { index -> fromHost(table.elements[index]) }, table) }
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
            is WasmI31 -> ReferenceValue.I31(value.value.toUInt()).toLong()
            is FunctionInstance -> ReferenceValue.Function(Address.Function(functionAddress(value))).toLong()
            is WasmArray -> encode(RV_TYPE_ARRAY, escape(value))
            is WasmException -> encode(RV_TYPE_EXCEPTION, escape(value))
            is HostValue -> encode(RV_TYPE_HOST, escape(value))
            is LegacyHostValue -> value.encoded
            else -> encode(RV_TYPE_STRUCT, escape(value))
        }

        return if (extern) encode(RV_TYPE_EXTERN, encoded) else encoded
    }

    @Synchronized
    override fun fromHost(encoded: Long): Any? {

        val payload = encoded ushr RV_SHIFT_BITS
        return when (encoded and RV_TYPE_MASK) {
            RV_TYPE_NULL -> null
            RV_TYPE_I31 -> WasmI31(payload.toInt())
            RV_TYPE_FUNCTION -> store.function(Address.Function(payload.toInt()))
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

    @Synchronized
    fun holdRaised(encoded: Long) {
        raisedException = fromHost(encoded)
    }

    @Synchronized
    fun releaseRaised() {
        raisedException = null
    }

    override fun retainExtern(value: Any): HostReferenceRoot {
        val extern = HostValue(value)
        return references.retainObject(extern, toHost(extern, true))
    }

    @Synchronized
    override fun release(global: GlobalInstance) {
        globals.remove(global)?.value = null
    }

    @Synchronized
    override fun release(table: TableInstance) {
        tables.remove(table)?.let { storage ->
            storage.elements = emptyArray()
            storage.targets = arrayOfNulls(0)
        }
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

    @Synchronized
    fun register(
        type: Int,
        shape: Class<*>,
    ) {
        shapes.putIfAbsent(type, shape)
        types.putIfAbsent(shape, type)
    }

    @Synchronized
    fun shape(type: Int): Class<*> {
        return shapes[type] ?: throw InvocationException(InvocationError.UnexpectedReferenceValue)
    }

    @Synchronized
    fun type(value: Any): Int {

        var shape: Class<*>? = value.javaClass
        while (shape != null) {
            types[shape]?.let { type -> return type }
            shape = shape.superclass
        }

        return -1
    }

    private fun escape(value: Any): Long {

        handles[value]?.let { handle -> return handle.toLong() }

        reclaimHandles()
        val handle = if (freeHandles.isEmpty()) FIRST_HANDLE.toInt() + objects.size else freeHandles.removeAt(freeHandles.size - 1)
        val slot = handle - FIRST_HANDLE.toInt()
        val reference = Handle(value, handle, collected)
        if (slot == objects.size) objects += reference else objects[slot] = reference
        handles[value] = handle

        return handle.toLong()
    }

    private fun reclaimHandles() {
        while (true) {
            val reference = collected.poll() as? Handle ?: return
            val slot = reference.handle - FIRST_HANDLE.toInt()
            if (objects.getOrNull(slot) === reference) {
                objects[slot] = null
                freeHandles += reference.handle
            }
        }
    }

    private fun objectAt(handle: Long): Any {
        return objects.getOrNull((handle - FIRST_HANDLE).toInt())?.get() ?: throw InvocationException(InvocationError.UnexpectedReferenceValue)
    }

    private fun legacyHostValue(encoded: Long): LegacyHostValue {
        return legacyHostValues.getOrPut(encoded) { LegacyHostValue(encoded) }
    }

    private fun functionAddress(function: FunctionInstance): Int {

        functions[function]?.let { address -> return address }
        store.functions.forEachIndexed { address, candidate -> functions[candidate] = address }

        return checkNotNull(functions[function]) { "function is not in this store" }
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
        queue: ReferenceQueue<Any>,
    ) : WeakReference<Any>(value, queue)

    private companion object {
        const val FIRST_HANDLE = 1L shl 30
        val NULL_ANY = ReferenceValue.Null(AbstractHeapType.Any).toLong()
        val NULL_EXTERN = ReferenceValue.Null(AbstractHeapType.Extern).toLong()
    }
}
