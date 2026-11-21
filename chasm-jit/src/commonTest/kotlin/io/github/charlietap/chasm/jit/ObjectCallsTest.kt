@file:OptIn(UnsafeHostApi::class, ExperimentalChasmApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.ExperimentalChasmApi
import io.github.charlietap.chasm.embedding.compiled.ExternalType
import io.github.charlietap.chasm.embedding.compiled.Global
import io.github.charlietap.chasm.embedding.compiled.HostGcType
import io.github.charlietap.chasm.embedding.compiled.Import
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.Store
import io.github.charlietap.chasm.embedding.compiled.Table
import io.github.charlietap.chasm.embedding.compiled.Tag
import io.github.charlietap.chasm.embedding.compiled.allocateArray
import io.github.charlietap.chasm.embedding.compiled.allocateStruct
import io.github.charlietap.chasm.embedding.compiled.arrayLength
import io.github.charlietap.chasm.embedding.compiled.arrayType
import io.github.charlietap.chasm.embedding.compiled.copyArray
import io.github.charlietap.chasm.embedding.compiled.copyTable
import io.github.charlietap.chasm.embedding.compiled.createException
import io.github.charlietap.chasm.embedding.compiled.dropInstance
import io.github.charlietap.chasm.embedding.compiled.dropStore
import io.github.charlietap.chasm.embedding.compiled.exceptionTag
import io.github.charlietap.chasm.embedding.compiled.exports
import io.github.charlietap.chasm.embedding.compiled.fillArray
import io.github.charlietap.chasm.embedding.compiled.fillTable
import io.github.charlietap.chasm.embedding.compiled.function
import io.github.charlietap.chasm.embedding.compiled.growTable
import io.github.charlietap.chasm.embedding.compiled.host.HostFunction
import io.github.charlietap.chasm.embedding.compiled.host.createException
import io.github.charlietap.chasm.embedding.compiled.host.raise
import io.github.charlietap.chasm.embedding.compiled.host.readI32
import io.github.charlietap.chasm.embedding.compiled.host.readReference
import io.github.charlietap.chasm.embedding.compiled.host.writeReference
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.invokeUnboxed
import io.github.charlietap.chasm.embedding.compiled.payloadSize
import io.github.charlietap.chasm.embedding.compiled.prepareFunction
import io.github.charlietap.chasm.embedding.compiled.readArrayElement
import io.github.charlietap.chasm.embedding.compiled.readArrayElements
import io.github.charlietap.chasm.embedding.compiled.readArrayReference
import io.github.charlietap.chasm.embedding.compiled.readGlobal
import io.github.charlietap.chasm.embedding.compiled.readGlobalReference
import io.github.charlietap.chasm.embedding.compiled.readPayload
import io.github.charlietap.chasm.embedding.compiled.readPayloadReference
import io.github.charlietap.chasm.embedding.compiled.readStructField
import io.github.charlietap.chasm.embedding.compiled.readStructReference
import io.github.charlietap.chasm.embedding.compiled.readTable
import io.github.charlietap.chasm.embedding.compiled.sizeTable
import io.github.charlietap.chasm.embedding.compiled.structType
import io.github.charlietap.chasm.embedding.compiled.unsafeAllocateArray
import io.github.charlietap.chasm.embedding.compiled.unsafeAllocateStruct
import io.github.charlietap.chasm.embedding.compiled.unsafeBorrowBytes
import io.github.charlietap.chasm.embedding.compiled.unsafeBorrowElements
import io.github.charlietap.chasm.embedding.compiled.unsafeBorrowReferences
import io.github.charlietap.chasm.embedding.compiled.unsafeInvokeUnboxed
import io.github.charlietap.chasm.embedding.compiled.unsafeWriteArrayReference
import io.github.charlietap.chasm.embedding.compiled.unsafeWriteGlobalReference
import io.github.charlietap.chasm.embedding.compiled.unsafeWriteStructReference
import io.github.charlietap.chasm.embedding.compiled.unsafeWriteTable
import io.github.charlietap.chasm.embedding.compiled.writeArrayElement
import io.github.charlietap.chasm.embedding.compiled.writeArrayElements
import io.github.charlietap.chasm.embedding.compiled.writeArrayReference
import io.github.charlietap.chasm.embedding.compiled.writeGlobal
import io.github.charlietap.chasm.embedding.compiled.writeGlobalReference
import io.github.charlietap.chasm.embedding.compiled.writeStructField
import io.github.charlietap.chasm.embedding.compiled.writeStructReference
import io.github.charlietap.chasm.embedding.compiled.writeTable
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.FunctionReference
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.type.FunctionType
import kotlinx.coroutines.runBlocking
import java.lang.ref.WeakReference
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertSame

class ObjectCallsTest {

    private val store = store()
    private var echo: Echo = Echo.Parameter
    private val instance by lazy { instantiate(store) }

    @Test
    fun structsCrossCallsByIdentity() {

        val point = point(4)
        val swapped = call("swap", references = arrayOf("label", point)).references

        assertSame(point, call("same", references = arrayOf(point)).references[0])
        assertSame(point, call("tail", references = arrayOf(point)).references[0])
        assertSame(point, call("tail_host", references = arrayOf(point)).references[0])
        assertSame(point, swapped[0])
        assertSame("label", swapped[1])
    }

    @Test
    fun i31ValuesCrossCalls() {

        val result = call("i31_double", references = arrayOf(I31Reference(21))).references[0]

        assertEquals(42, assertIs<I31Reference>(result).value)
    }

    @Test
    fun structFieldsReadAndWriteThroughTheHost() {

        val point = point(4)
        val next = point(5)

        writeStructField(point, 0, 10)
        writeStructReference(point, 5, next)
        writeStructReference(point, 6, "label")

        assertEquals(10, call("x", references = arrayOf(point)).words[0].toInt())
        assertSame(next, call("next", references = arrayOf(point)).references[0])
        assertSame("label", call("label", references = arrayOf(point)).references[0])
        assertEquals(2L, readStructField(point, 1))
        assertEquals(3.5.toRawBits(), readStructField(point, 2))
        assertEquals(0xFFL, readStructField(point, 3))
        assertEquals(0xFFFFL, readStructField(point, 4))
        assertSame("label", readStructReference(point, 6))
    }

    @Test
    fun structCallsRejectMisuse() {

        val point = point(4)
        val foreign = foreignPoint()
        val pair = call("pair", references = arrayOf(point)).references[0] as StructReference

        assertFailsWith<IllegalArgumentException> { writeStructField(point, 7, 1) }
        assertFailsWith<IllegalArgumentException> { writeStructReference(pair, 0, point) }
        assertFailsWith<IllegalArgumentException> { writeStructReference(point, 5, "text") }
        assertFailsWith<IllegalArgumentException> { writeStructReference(point, 5, foreign) }
        assertFailsWith<IllegalArgumentException> { readStructField(point, 5) }
        assertFailsWith<IllegalArgumentException> { readStructReference(point, 0) }
        assertFailsWith<IndexOutOfBoundsException> { readStructField(point, 8) }
    }

    @Test
    fun structsAnswerThroughTheirOwnStore() {

        val foreignStore = store()
        val foreign = point(foreignStore, instantiate(foreignStore))

        writeStructField(foreign, 0, 6)

        assertEquals(6L, readStructField(foreign, 0))

        dropStore(foreignStore)
    }

    @Test
    fun hostAllocatedStructsReachTheGuest() {

        val type = structType(point(1))
        val references = arrayOfNulls<Any>(8).apply { this[6] = "tag" }
        val struct = allocateStruct(store, type, longArrayOf(11, 0, 0, 0, 0, 0, 0, 9), references)
        val unchecked = unsafeAllocateStruct(store, type, longArrayOf(12, 0, 0, 0, 0, 0, 0, 9), arrayOfNulls(8))

        unsafeWriteStructReference(unchecked, 5, struct)

        assertEquals(11, call("x", references = arrayOf(struct)).words[0].toInt())
        assertSame("tag", call("label", references = arrayOf(struct)).references[0])
        assertSame(struct, call("next", references = arrayOf(unchecked)).references[0])
        assertEquals(9L, readStructField(struct, 7))
        assertFailsWith<IllegalArgumentException> {
            allocateStruct(store, type, LongArray(8), arrayOfNulls<Any>(8).apply { this[5] = "text" })
        }
        assertFailsWith<IllegalArgumentException> { allocateStruct(store, arrayType(bytes(1)), LongArray(8), arrayOfNulls(8)) }
        assertFailsWith<IllegalArgumentException> { allocateStruct(store, HostGcType(UNKNOWN_TYPE), LongArray(8), arrayOfNulls(8)) }
    }

    @Test
    fun exactTypesAcceptSubtypesAndRejectSiblings() {

        val point = point(1)
        val subtype = call("point3", words = longArrayOf(7)).references[0]
        val sibling = call("other").references[0]
        val points = array(call("make_points", words = longArrayOf(1)).references[0])
        val carry = tag("carry")

        writeStructReference(point, 5, subtype)
        writeArrayReference(points, 0, subtype)
        writeTable(table("slots"), 0, subtype)
        writeGlobalReference(global("current"), subtype)
        createException(store, carry, LongArray(1), arrayOf(subtype))

        assertEquals(7, call("x", references = arrayOf(subtype)).words[0].toInt())
        assertIs<ExecutionError.Rejected>(failure("x", references = arrayOf(sibling)))
        assertFailsWith<IllegalArgumentException> { writeStructReference(point, 5, sibling) }
        assertFailsWith<IllegalArgumentException> { writeArrayReference(points, 0, sibling) }
        assertFailsWith<IllegalArgumentException> { writeTable(table("slots"), 0, sibling) }
        assertFailsWith<IllegalArgumentException> { writeGlobalReference(global("current"), sibling) }
        assertFailsWith<IllegalArgumentException> { createException(store, carry, LongArray(1), arrayOf(sibling)) }
    }

    @Test
    fun hostResultsAreCheckedAgainstExactTypes() {

        echo = Echo.Value(call("point3", words = longArrayOf(1)).references[0])
        call("tail_host", references = arrayOf(point(1)))

        echo = Echo.Value(call("other").references[0])
        val sibling = failure("tail_host", references = arrayOf(point(1)))

        echo = Echo.Value("text")
        val wrongClass = failure("tail_host", references = arrayOf(point(1)))

        assertEquals(TrapReason.HOST_FUNCTION_FAILURE, assertIs<ExecutionError.Trapped>(sibling).trap.reason)
        assertEquals(TrapReason.HOST_FUNCTION_FAILURE, assertIs<ExecutionError.Trapped>(wrongClass).trap.reason)
    }

    @Test
    fun functionReferencesInsideRecursiveGroupsAreExact() {

        val holder = call("holder").references[0] as StructReference

        writeStructReference(holder, 0, function(TRIPLE))

        assertEquals(15, call("call_held", words = longArrayOf(0, 5), references = arrayOf(holder)).words[0].toInt())
        assertFailsWith<IllegalArgumentException> { writeStructReference(holder, 0, function(INC)) }
    }

    @Test
    fun nullsAreRejectedWhereTheTypeIsNonNullable() {

        val point = point(1)

        assertFailsWith<IllegalArgumentException> { writeGlobalReference(global("origin"), null) }
        assertFailsWith<IllegalArgumentException> { writeTable(table("required"), 0, null) }
        assertFailsWith<IllegalArgumentException> { createException(store, tag("strict_carry"), LongArray(1), arrayOfNulls(1)) }
        assertIs<ExecutionError.Rejected>(failure("x", references = arrayOfNulls(1)))

        writeGlobalReference(global("origin"), point)

        assertSame(point, readGlobalReference(global("origin")))
    }

    @Test
    fun arraysCopyInAndOut() {

        val bytes = bytes(4)
        val longs = array(call("make_longs", words = longArrayOf(3)).references[0])
        val read = LongArray(3)

        writeArrayElements(bytes, 1, longArrayOf(2, 3, 4))
        fillArray(longs, 1, 2, 9)
        readArrayElements(longs, 0, read)
        copyArray(bytes, 0, bytes, 3, 1)

        assertEquals(4, arrayLength(bytes))
        assertEquals(1 + 2 + 3 + 1, sumBytes(bytes))
        assertContentEquals(longArrayOf(7, 9, 9), read)
        assertEquals(9L, readArrayElement(longs, 2))
        assertFailsWith<IllegalArgumentException> { copyArray(longs, 0, bytes, 0, 1) }
        assertFailsWith<IndexOutOfBoundsException> { readArrayElements(longs, 2, read, 0, 2) }
        assertFailsWith<IllegalArgumentException> { allocateArray(store, arrayType(bytes), -1, 0L) }
    }

    @Test
    fun packedElementsReadAsTheirUnsignedBits() {

        val chars = array(call("make_chars", words = longArrayOf(2)).references[0])

        writeArrayElement(chars, 1, 0x1234)

        assertEquals(0xFFFFL, readArrayElement(chars, 0))
        assertEquals(0x1234, call("char_at", words = longArrayOf(0, 1), references = arrayOf(chars)).words[0].toInt())
    }

    @Test
    fun immutableArraysRejectWrites() {

        val frozen = array(call("frozen").references[0])

        assertEquals(2L, readArrayElement(frozen, 1))
        assertFailsWith<IllegalArgumentException> { writeArrayElement(frozen, 0, 1) }
        assertFailsWith<IllegalArgumentException> { fillArray(frozen, 0, 1, 1) }
    }

    @Test
    fun referenceArraysHoldCheckedObjects() {

        val points = array(call("make_points", words = longArrayOf(2)).references[0])
        val point = point(3)
        val type = arrayType(points)

        writeArrayReference(points, 1, point)
        unsafeWriteArrayReference(points, 0, point)

        val filled = allocateArray(store, type, 2, point)
        val copied = allocateArray(store, type, arrayOf(null, point))

        assertSame(point, call("point_at", words = longArrayOf(0, 1), references = arrayOf(points)).references[0])
        assertSame(point, readArrayReference(points, 0))
        assertSame(point, readArrayReference(filled, 1))
        assertNull(readArrayReference(copied, 0))
        assertSame(point, call("point_at", words = longArrayOf(0, 1), references = arrayOf(copied)).references[0])
        assertFailsWith<IllegalArgumentException> { writeArrayReference(points, 0, "text") }
        assertFailsWith<IllegalArgumentException> { allocateArray(store, type, arrayOf<Any?>("text")) }
        assertFailsWith<IllegalArgumentException> { readArrayElement(points, 0) }
        assertFailsWith<IllegalArgumentException> { allocateArray(store, type, 2, 0L) }
    }

    @Test
    fun referenceArrayCopiesCheckTheDestinationType() {

        val point = point(3)
        val points = array(call("make_points", words = longArrayOf(2)).references[0])
        val strict = array(call("make_strict", words = longArrayOf(0, 2), references = arrayOf(point)).references[0])

        writeArrayReference(points, 1, point)
        copyArray(points, 1, strict, 0, 1)
        copyArray(strict, 0, points, 0, 2)

        assertSame(point, readArrayReference(strict, 0))
        assertSame(point, readArrayReference(points, 0))
        assertFailsWith<IllegalArgumentException> { copyArray(array(call("make_points", words = longArrayOf(1)).references[0]), 0, strict, 0, 1) }
    }

    @Test
    fun referenceArrayCopiesFollowSubtyping() {

        val point = point(1)
        val subtype = call("point3", words = longArrayOf(2)).references[0]
        val points = array(call("make_points", words = longArrayOf(2)).references[0])
        val subtypes = array(call("make_point3s", words = longArrayOf(2)).references[0])
        val anys = array(call("make_anys", words = longArrayOf(2)).references[0])

        writeArrayReference(subtypes, 0, subtype)
        copyArray(subtypes, 0, points, 0, 1)
        copyArray(points, 0, anys, 0, 1)
        writeArrayReference(points, 1, point)
        writeArrayReference(anys, 1, call("other").references[0])

        assertSame(subtype, readArrayReference(points, 0))
        assertSame(subtype, readArrayReference(anys, 0))
        assertFailsWith<IllegalArgumentException> { copyArray(points, 0, subtypes, 1, 1) }
        assertFailsWith<IllegalArgumentException> { copyArray(anys, 1, points, 0, 1) }
    }

    @Test
    fun tablesAndGlobalsFromAnotherStoreAreRejected() {

        val foreign = store()
        val exports = exports(instantiate(foreign))
        val table = checkNotNull(exports.table("slots"))
        val global = checkNotNull(exports.global("current"))

        assertFailsWith<IllegalArgumentException> { copyTable(table, 0, table("slots"), 0, 1) }
        assertFailsWith<IllegalArgumentException> { copyTable(table("slots"), 0, table, 0, 1) }
        assertSame(null, readGlobalReference(global))
    }

    @Test
    fun borrowedStorageAliasesTheGuestArray() {

        val bytes = bytes(3)
        val borrowed = unsafeBorrowBytes(bytes)
        val points = array(call("make_points", words = longArrayOf(1)).references[0])
        val point = point(2)

        borrowed[0] = 10
        unsafeBorrowReferences(points)[0] = point

        assertEquals(12, sumBytes(bytes))
        assertSame(point, call("point_at", words = longArrayOf(0, 0), references = arrayOf(points)).references[0])
        assertFailsWith<IllegalArgumentException> { unsafeBorrowReferences(bytes) }
    }

    @Test
    fun adoptedStorageAliasesTheHostArray() {

        val type = arrayType(bytes(1))
        val storage = byteArrayOf(1, 2, 3)
        val adopted = unsafeAllocateArray(store, type, storage)

        assertEquals(6, sumBytes(adopted))

        storage[0] = 5

        assertEquals(10, sumBytes(adopted))
        assertSame(storage, unsafeBorrowBytes(adopted))
        assertFailsWith<IllegalArgumentException> { unsafeAllocateArray(store, type, CharArray(2)) }
        assertFailsWith<IllegalArgumentException> {
            unsafeAllocateArray(store, arrayType(array(call("make_points", words = longArrayOf(1)).references[0])), arrayOfNulls<Any>(1))
        }
    }

    @Test
    fun objectsSurviveCollectionWithoutRetaining() {

        val point = point(21)
        val bytes = allocateArray(store, arrayType(bytes(1)), 2, 4L)

        repeat(3) { System.gc() }

        assertEquals(21, call("x", references = arrayOf(point)).words[0].toInt())
        assertEquals(8, sumBytes(bytes))
    }

    @Test
    fun droppedObjectsAreCollected() {

        val dropped = WeakReference(point(1))
        call("x", references = arrayOf(point(2)))

        var attempts = 0
        while (dropped.get() != null && attempts < COLLECTION_ATTEMPTS) {
            System.gc()
            Thread.sleep(COLLECTION_PAUSE)
            attempts++
        }

        assertNull(dropped.get())
    }

    @Test
    fun thrownExceptionsComeBackAsObjects() {

        val thrown = failure("throw", longArrayOf(7), arrayOf(null, "payload"))
        val exception = assertIs<ExecutionError.Thrown<ExceptionReference>>(thrown).exception
        val rethrown = failure("rethrow", references = arrayOf(exception))

        assertEquals(tag("boom"), exceptionTag(exception))
        assertEquals(2, payloadSize(exception))
        assertEquals(7L, readPayload(exception, 0))
        assertSame("payload", readPayloadReference(exception, 1))
        assertSame(exception, assertIs<ExecutionError.Thrown<ExceptionReference>>(rethrown).exception)
        assertFailsWith<IllegalArgumentException> { readPayload(exception, 1) }
    }

    @Test
    fun guestExceptionsWithSingleKindPayloadsReportTheirSize() {

        val point = point(1)
        val carried = assertIs<ExecutionError.Thrown<ExceptionReference>>(failure("throw_carry", references = arrayOf(point))).exception
        val numbers = assertIs<ExecutionError.Thrown<ExceptionReference>>(failure("throw_numbers", words = longArrayOf(4, 5))).exception

        assertEquals(1, payloadSize(carried))
        assertSame(point, readPayloadReference(carried, 0))
        assertFailsWith<IllegalArgumentException> { readPayload(carried, 0) }
        assertEquals(2, payloadSize(numbers))
        assertEquals(5L, readPayload(numbers, 1))
        assertFailsWith<IllegalArgumentException> { readPayloadReference(numbers, 0) }
    }

    @Test
    fun exceptionsFromAnotherStoreAreRejected() {

        val foreign = foreignException()

        assertIs<ExecutionError.Rejected>(failure("rethrow", references = arrayOf(foreign)))
    }

    @Test
    fun hostRaisedExceptionsAreCaughtByTheGuest() {

        val lanes = call("catch_host", words = longArrayOf(3), references = arrayOf(null, "raised"))

        assertEquals(3, lanes.words[0].toInt())
        assertSame("raised", lanes.references[1])
    }

    @Test
    fun exceptionPayloadsAreChecked() {
        assertFailsWith<IllegalArgumentException> { createException(store, tag("boom"), LongArray(2), arrayOf(null, function(INC))) }
    }

    @Test
    fun tableWritesReachIndirectCalls() {

        val functions = table("functions")

        writeTable(functions, 0, function(INC))
        val incremented = call("call_slot", words = longArrayOf(0, 5)).words[0]
        writeTable(functions, 0, function(DOUBLE))
        val doubled = call("call_slot", words = longArrayOf(0, 5)).words[0]
        unsafeWriteTable(functions, 0, function(INC))
        val again = call("call_slot", words = longArrayOf(0, 5)).words[0]
        unsafeBorrowElements(functions)[0] = function(DOUBLE)
        val borrowed = call("call_slot", words = longArrayOf(0, 5)).words[0]

        assertEquals(6, incremented.toInt())
        assertEquals(10, doubled.toInt())
        assertEquals(6, again.toInt())
        assertEquals(10, borrowed.toInt())
        assertSame(function(DOUBLE), readTable(functions, 0))
        assertFailsWith<IllegalArgumentException> { writeTable(functions, 1, "text") }
        assertFailsWith<IllegalArgumentException> { writeTable(functions, 1, foreignFunction()) }
    }

    @Test
    fun tablesHoldObjects() {

        val slots = table("slots")
        val point = point(8)
        val other = point(9)
        val buffer = arrayOfNulls<Any>(2)

        writeTable(slots, 0, point)
        val previous = growTable(slots, 2, other)
        writeTable(slots, 5, point)
        fillTable(slots, 1, other, 2)
        copyTable(slots, 0, slots, 3, 1)
        writeTable(slots, 4, arrayOf<Any?>(point, null))
        readTable(slots, buffer, 4, 2)

        assertEquals(4, previous)
        assertEquals(6, sizeTable(slots))
        assertEquals(-1, growTable(slots, 10, null))
        assertSame(point, call("slot", words = longArrayOf(3)).references[0])
        assertSame(other, call("slot", words = longArrayOf(2)).references[0])
        assertSame(point, buffer[0])
        assertNull(buffer[1])
        assertSame(point, unsafeBorrowElements(slots)[0])
        assertFailsWith<IllegalArgumentException> { growTable(slots, 1, "text") }
        assertFailsWith<IndexOutOfBoundsException> { fillTable(slots, 5, point, 2) }
    }

    @Test
    fun tableCopiesCheckTheDestinationType() {

        val anything = table("anything")

        writeTable(table("functions"), 0, function(INC))
        writeTable(anything, 0, call("other").references[0])
        writeTable(anything, 1, point(1))
        copyTable(anything, 1, table("slots"), 0, 1)

        assertFailsWith<IllegalArgumentException> { copyTable(table("functions"), 0, table("slots"), 0, 1) }
        assertFailsWith<IllegalArgumentException> { copyTable(anything, 0, table("slots"), 0, 1) }
    }

    @Test
    fun globalsHoldObjects() {

        val current = global("current")
        val count = global("count")
        val point = point(13)

        writeGlobalReference(current, point)
        writeGlobal(count, 5)

        assertEquals(13, call("current_x").words[0].toInt())
        assertEquals(5, call("read_count").words[0].toInt())
        assertSame(point, readGlobalReference(current))
        assertEquals(5L, readGlobal(count))
        assertFailsWith<IllegalArgumentException> { writeGlobalReference(current, "text") }
        assertFailsWith<IllegalArgumentException> { readGlobal(current) }
        assertFailsWith<IllegalArgumentException> { writeGlobal(global("fixed"), 1) }
        assertFailsWith<IllegalArgumentException> { writeGlobalReference(global("fixed_point"), point) }

        unsafeWriteGlobalReference(current, point(14))

        assertEquals(14, call("current_x").words[0].toInt())
    }

    @Test
    fun callArgumentsAreCheckedAgainstExactTypes() {
        assertIs<ExecutionError.Rejected>(failure("x", references = arrayOf(foreignPoint())))
        assertIs<ExecutionError.Rejected>(failure("x", references = arrayOf("text")))
    }

    @Test
    fun callsOnDroppedStoresAndInstancesAreRejected() {

        val dropped = store()
        val instance = instantiate(dropped)
        val prepared = prepareFunction(dropped, instance, "x").expect("x")
        val point = point(dropped, instance)

        dropInstance(instance)
        val deinstantiated = prepared.invokeUnboxed(LongArray(prepared.slots), arrayOf(point))
        dropStore(dropped)
        val storeDropped = prepared.invokeUnboxed(LongArray(prepared.slots), arrayOf(point))

        assertIs<ExecutionError.Rejected>(assertIs<ChasmResult.Error<InvocationError<*>>>(deinstantiated).error)
        assertIs<ExecutionError.Rejected>(assertIs<ChasmResult.Error<InvocationError<*>>>(storeDropped).error)
    }

    @Test
    fun uncheckedCallsFailInsideTheGuest() {

        val prepared = prepareFunction(store, instance, "x").expect("x")

        val result = prepared.unsafeInvokeUnboxed(LongArray(prepared.slots), arrayOf("text"))

        val trap = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<InvocationError<*>>>(result).error).trap
        assertEquals(TrapReason.CAST_FAILURE, trap.reason)
    }

    private fun point(x: Int): StructReference {
        return call("point", words = longArrayOf(x.toLong())).references[0] as StructReference
    }

    private fun bytes(length: Int): ArrayReference {
        return array(call("make_bytes", words = longArrayOf(length.toLong())).references[0])
    }

    private fun array(value: Any?): ArrayReference {
        return value as ArrayReference
    }

    private fun sumBytes(bytes: Any?): Int {
        return call("sum_bytes", references = arrayOf(bytes)).words[0].toInt()
    }

    private fun point(
        store: Store,
        instance: Instance,
    ): StructReference {
        return call(store, instance, "point", longArrayOf(1), arrayOf()).references[0] as StructReference
    }

    private fun foreignPoint(): StructReference {
        val foreign = store()
        return point(foreign, instantiate(foreign))
    }

    private fun foreignException(): Any {

        val foreign = store()
        val prepared = prepareFunction(foreign, instantiate(foreign), "throw").expect("throw")
        val result = prepared.invokeUnboxed(LongArray(prepared.slots), arrayOfNulls(prepared.slots))

        return assertIs<ExecutionError.Thrown<ExceptionReference>>(assertIs<ChasmResult.Error<InvocationError<ExceptionReference>>>(result).error).exception
    }

    private fun foreignFunction(): Any? {
        val foreign = store()
        return call(foreign, instantiate(foreign), "function", longArrayOf(INC.toLong()), arrayOf()).references[0]
    }

    private fun function(index: Int): FunctionReference {
        return call("function", words = longArrayOf(index.toLong())).references[0] as FunctionReference
    }

    private fun call(
        name: String,
        words: LongArray = LongArray(0),
        references: Array<Any?> = arrayOf(),
    ): Lanes {
        return call(store, instance, name, words, references)
    }

    private fun call(
        store: Store,
        instance: Instance,
        name: String,
        words: LongArray,
        references: Array<Any?>,
    ): Lanes {

        val prepared = prepareFunction(store, instance, name).expect(name)
        val lanes = Lanes(words.copyOf(prepared.slots), references.copyOf(prepared.slots))

        prepared.invokeUnboxed(lanes.words, lanes.references).expect(name)

        return lanes
    }

    private fun failure(
        name: String,
        words: LongArray = LongArray(0),
        references: Array<Any?> = arrayOf(),
    ): InvocationError<ExceptionReference> {

        val prepared = prepareFunction(store, instance, name).expect(name)
        val result = prepared.invokeUnboxed(words.copyOf(prepared.slots), references.copyOf(prepared.slots))

        return assertIs<ChasmResult.Error<InvocationError<ExceptionReference>>>(result).error
    }

    private fun table(name: String): Table {
        return checkNotNull(exports(instance).table(name))
    }

    private fun global(name: String): Global {
        return checkNotNull(exports(instance).global(name))
    }

    private fun tag(name: String): Tag {
        return checkNotNull(exports(instance).tag(name))
    }

    private fun instantiate(store: Store): Instance {

        val module = runBlocking { module(resourceBytes("modules/object_calls.wasm"), validate = true) }.expect("module")
        val echoType = (module.imports.first { import -> import.entityName == "echo" }.type as ExternalType.Function).functionType
        val imports = listOf(
            Import(
                "host",
                "raise",
                function(
                    store,
                    functionType {
                        params {
                            i32()
                            externref()
                        }
                    },
                    raise,
                ),
            ),
            Import("host", "echo", function(store, echoType, echoFunction)),
        )

        return runBlocking { instance(store, module, imports) }.expect("instance")
    }

    private val raise = HostFunction { parameters, _ ->
        val payload = arrayOf(null, parameters.readReference(1))
        raise(tag("boom").createException(longArrayOf(parameters.readI32(0).toLong(), 0), payload))
    }

    private val echoFunction = HostFunction { parameters, results ->
        val result = when (val current = echo) {
            Echo.Parameter -> parameters.readReference(0)
            is Echo.Value -> current.value
        }
        results.writeReference(0, result)
    }

    private sealed interface Echo {

        data object Parameter : Echo

        class Value(val value: Any?) : Echo
    }

    private class Lanes(
        val words: LongArray,
        val references: Array<Any?>,
    )

    private companion object {
        const val COLLECTION_ATTEMPTS = 100
        const val COLLECTION_PAUSE = 10L
        const val UNKNOWN_TYPE = 100_000
        const val INC = 0
        const val DOUBLE = 1
        const val TRIPLE = 2
    }
}

private fun functionType(builder: FunctionTypeBuilder.() -> Unit): FunctionType {
    return FunctionTypeBuilder().apply(builder).build()
}
