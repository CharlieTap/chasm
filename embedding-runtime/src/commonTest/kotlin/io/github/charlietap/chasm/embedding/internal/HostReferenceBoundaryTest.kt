package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.collect
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.fixture.registerArrayType
import io.github.charlietap.chasm.embedding.fixture.registerStructType
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.functionReference
import io.github.charlietap.chasm.embedding.memory
import io.github.charlietap.chasm.embedding.memory.growMemory
import io.github.charlietap.chasm.embedding.memory.sizeMemory
import io.github.charlietap.chasm.embedding.reference.allocateNumericArray
import io.github.charlietap.chasm.embedding.reference.allocateReferenceArray
import io.github.charlietap.chasm.embedding.reference.allocateStruct
import io.github.charlietap.chasm.embedding.reference.createException
import io.github.charlietap.chasm.embedding.reference.readArrayReference
import io.github.charlietap.chasm.embedding.reference.readPayload
import io.github.charlietap.chasm.embedding.reference.readPayloadReference
import io.github.charlietap.chasm.embedding.reference.readStructField
import io.github.charlietap.chasm.embedding.reference.readStructReference
import io.github.charlietap.chasm.embedding.reference.structFieldCount
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.Table
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.embedding.table
import io.github.charlietap.chasm.embedding.table.fillTable
import io.github.charlietap.chasm.embedding.table.growTable
import io.github.charlietap.chasm.embedding.table.readTable
import io.github.charlietap.chasm.embedding.table.sizeTable
import io.github.charlietap.chasm.embedding.table.writeTable
import io.github.charlietap.chasm.embedding.tag
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.runtime.encoder.RV_SHIFT_BITS
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_EXTERN
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_HOST
import io.github.charlietap.chasm.runtime.encoder.RV_TYPE_MASK
import io.github.charlietap.chasm.type.AbstractHeapType
import io.github.charlietap.chasm.type.AddressType
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.Limits
import io.github.charlietap.chasm.type.MemoryType
import io.github.charlietap.chasm.type.Mutability
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ReferenceType
import io.github.charlietap.chasm.type.ResultType
import io.github.charlietap.chasm.type.SharedStatus
import io.github.charlietap.chasm.type.StorageType
import io.github.charlietap.chasm.type.TableType
import io.github.charlietap.chasm.type.ValueType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

class HostReferenceBoundaryTest {

    @Test
    fun `a function reference written as an externref takes a host slot and reads back as the same object`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val table = externTable(store)
        val reference = functionReference(function(store, FunctionType(ResultType(emptyList()), ResultType(emptyList())), HostFunction { _, _ -> }))

        writeTable(table, 0, reference)

        assertHostSlot(runtime.tables.single().elements[0])
        assertSame(reference, readTable(table, 0))
    }

    @Test
    fun `an exception reference written as an externref takes a host slot and reads back as the same object`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val table = externTable(store)
        val exception = createException(tag(store, tagType()), LongArray(0), arrayOfNulls(0))

        writeTable(table, 0, exception)

        assertHostSlot(runtime.tables.single().elements[0])
        assertSame(exception, readTable(table, 0))
    }

    @Test
    fun `a struct of another store written as an externref is that host's object`() {

        val foreignRuntime = store()
        val foreignStore = interpreterStore(foreignRuntime)
        val foreign = allocateStruct(foreignStore, numericStructType(foreignRuntime), longArrayOf(7L), arrayOfNulls(0))
        val runtime = store()
        val table = externTable(interpreterStore(runtime))

        writeTable(table, 0, foreign)

        assertHostSlot(runtime.tables.single().elements[0])
        assertSame(foreign, readTable(table, 0))
    }

    @Test
    fun `host objects held only by a struct field an array element and an exception payload survive a collection`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val fieldValue = HostValue("field")
        val elementValue = HostValue("element")
        val payloadValue = HostValue("payload")
        val struct = allocateStruct(store, externStructType(runtime, 1), LongArray(1), arrayOf(fieldValue))
        val array = allocateReferenceArray(store, externArrayType(runtime), 1, elementValue)
        val exception = createException(externTag(store, 1), LongArray(1), arrayOf(payloadValue))

        collect(store)

        assertSame(fieldValue, readStructReference(struct, 0))
        assertSame(elementValue, readArrayReference(array, 0))
        assertSame(payloadValue, readPayloadReference(exception, 0))
    }

    @Test
    fun `two externrefs allocated into a struct keep their distinct objects across slot collections`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = externStructType(runtime, 2)

        repeat(ROUNDS) {

            val first = Any()
            val second = Any()

            val struct = allocateStruct(store, type, LongArray(2), arrayOf(first, second))

            assertSame(first, readStructReference(struct, 0))
            assertSame(second, readStructReference(struct, 1))
        }
    }

    @Test
    fun `two externrefs allocated into an array keep their distinct objects across slot collections`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = externArrayType(runtime)

        repeat(ROUNDS) {

            val first = Any()
            val second = Any()

            val array = allocateReferenceArray(store, type, arrayOf(first, second), 0, 2)

            assertSame(first, readArrayReference(array, 0))
            assertSame(second, readArrayReference(array, 1))
        }
    }

    @Test
    fun `two externrefs created into an exception keep their distinct objects across slot collections`() {

        val store = interpreterStore()
        val tag = externTag(store, 2)

        repeat(ROUNDS) {

            val first = Any()
            val second = Any()

            val exception = createException(tag, LongArray(2), arrayOf(first, second))

            assertSame(first, readPayloadReference(exception, 0))
            assertSame(second, readPayloadReference(exception, 1))
        }
    }

    @Test
    fun `filled and grown externrefs land as the objects passed across slot collections`() {

        val store = interpreterStore()
        val table = externTable(store)

        repeat(ROUNDS) {

            val filled = Any()
            val grown = Any()

            fillTable(table, 0, filled, 2)
            growTable(table, 1, grown)

            assertSame(filled, readTable(table, 0))
            assertSame(filled, readTable(table, 1))
            assertSame(grown, readTable(table, sizeTable(table) - 1))
        }
    }

    @Test
    fun `held references stay equal with stable hash codes after the store is dropped`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val inner = allocateStruct(store, numericStructType(runtime), longArrayOf(7L), arrayOfNulls(0))
        val outer = allocateStruct(store, structReferenceType(runtime), LongArray(1), arrayOf(inner))
        val first = readStructReference(outer, 0)
        val second = readStructReference(outer, 0)
        val hashCode = first.hashCode()

        dropStore(store)

        assertNotSame(first, second)
        assertEquals(first, second)
        assertEquals(inner, first)
        assertEquals(hashCode, first.hashCode())
        assertEquals(hashCode, second.hashCode())
    }

    @Test
    fun `a gc type of the wrong kind is rejected with illegal argument`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val structType = numericStructType(runtime)
        val arrayType = externArrayType(runtime)

        assertFailsWith<IllegalArgumentException> { allocateStruct(store, arrayType, LongArray(1), arrayOfNulls(1)) }
        assertFailsWith<IllegalArgumentException> { allocateNumericArray(store, structType, 1, 0L) }
        assertFailsWith<IllegalArgumentException> { allocateReferenceArray(store, structType, 1, null) }
        assertFailsWith<IllegalArgumentException> { structFieldCount(store, arrayType) }
    }

    @Test
    fun `numeric structs and exceptions need no reference lane`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val i32 = ValueType.Number(NumberType.I32)
        val tag = tag(store, tagType(functionType = FunctionType(ResultType(listOf(i32)), ResultType(emptyList()))))

        val struct = allocateStruct(store, numericStructType(runtime), longArrayOf(7L), arrayOfNulls(0))
        val exception = createException(tag, longArrayOf(9L), arrayOfNulls(0))

        assertEquals(7L, readStructField(struct, 0))
        assertEquals(9L, readPayload(exception, 0))
    }

    @Test
    fun `memory and table handles keep the type they were created with after growth`() {

        val store = interpreterStore()
        val memoryType = MemoryType(AddressType.I32, Limits(1u, 3u), SharedStatus.Unshared)
        val tableType = TableType(AddressType.I32, EXTERNREF, Limits(1u, 3u))
        val memory = memory(store, memoryType)
        val table = table(store, tableType, null)

        growMemory(memory, 1)
        growTable(table, 1, null)

        assertEquals(2, sizeMemory(memory))
        assertEquals(2, sizeTable(table))
        assertEquals(1u, memory.type.limits.min)
        assertEquals(1u, table.type.limits.min)
        assertEquals(1u, memoryType.limits.min)
        assertEquals(1u, tableType.limits.min)
    }

    private fun assertHostSlot(word: Long) {
        assertEquals(RV_TYPE_EXTERN, word and RV_TYPE_MASK)
        assertEquals(RV_TYPE_HOST, (word shr RV_SHIFT_BITS) and RV_TYPE_MASK)
    }

    private fun externTable(store: Store): Table {
        return table(store, TableType(AddressType.I32, EXTERNREF, Limits(2u)), null)
    }

    private fun externTag(
        store: Store,
        parameters: Int,
    ): Tag {

        val type = FunctionType(ResultType(List(parameters) { ValueType.Reference(EXTERNREF) }), ResultType(emptyList()))

        return tag(store, tagType(functionType = type))
    }

    private fun numericStructType(runtime: RuntimeStore): HostGcType {
        return registerStructType(runtime, listOf(mutableField(ValueType.Number(NumberType.I64))))
    }

    private fun structReferenceType(runtime: RuntimeStore): HostGcType {
        return registerStructType(runtime, listOf(mutableField(ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Struct)))))
    }

    private fun externStructType(
        runtime: RuntimeStore,
        fields: Int,
    ): HostGcType {
        return registerStructType(runtime, List(fields) { mutableField(ValueType.Reference(EXTERNREF)) })
    }

    private fun externArrayType(runtime: RuntimeStore): HostGcType {
        return registerArrayType(runtime, mutableField(ValueType.Reference(EXTERNREF)))
    }

    private fun mutableField(type: ValueType): FieldType {
        return FieldType(StorageType.Value(type), Mutability.Var)
    }

    private data class HostValue(val name: String)

    private companion object {
        const val ROUNDS = 200
        val EXTERNREF = ReferenceType.RefNull(AbstractHeapType.Extern)
    }
}
