@file:OptIn(InternalChasmApi::class, UnsafeHostApi::class)

package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.dropInstance
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.dsl.FunctionTypeBuilder
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InvocationError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.moduleInfo
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Function
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.host.HostGcType
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.jit.internal._functionReference
import io.github.charlietap.chasm.jit.internal._objects
import io.github.charlietap.chasm.reference.ArrayReference
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ExternalValue
import io.github.charlietap.chasm.runtime.instance.GlobalInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.runtime.type.ExternalType
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
import kotlin.test.assertTrue

class ObjectCallsTest {

    private val store = store()
    private val objects = _objects(store)
    private val structs = objects.structs
    private val arrays = objects.arrays
    private val exceptions = objects.exceptions
    private val tables = objects.tables
    private val globals = objects.globals
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

        structs.writeStructField(point, 0, 10)
        structs.writeStructReference(point, 5, next)
        structs.writeStructReference(point, 6, "label")

        assertEquals(10, call("x", references = arrayOf(point)).words[0].toInt())
        assertSame(next, call("next", references = arrayOf(point)).references[0])
        assertSame("label", call("label", references = arrayOf(point)).references[0])
        assertEquals(2L, structs.readStructField(point, 1))
        assertEquals(3.5.toRawBits(), structs.readStructField(point, 2))
        assertEquals(0xFFL, structs.readStructField(point, 3))
        assertEquals(0xFFFFL, structs.readStructField(point, 4))
        assertSame("label", structs.readStructReference(point, 6))
    }

    @Test
    fun structCallsRejectMisuse() {

        val point = point(4)
        val foreign = foreignPoint()
        val pair = call("pair", references = arrayOf(point)).references[0]

        assertFailsWith<IllegalArgumentException> { structs.writeStructField(point, 7, 1) }
        assertFailsWith<IllegalArgumentException> { structs.writeStructReference(pair, 0, point) }
        assertFailsWith<IllegalArgumentException> { structs.writeStructReference(point, 5, "text") }
        assertFailsWith<IllegalArgumentException> { structs.writeStructReference(point, 5, foreign) }
        assertFailsWith<IllegalArgumentException> { structs.readStructField(point, 5) }
        assertFailsWith<IllegalArgumentException> { structs.readStructReference(point, 0) }
        assertFailsWith<IllegalArgumentException> { structs.readStructField("not a struct", 0) }
        assertFailsWith<IllegalArgumentException> { structs.readStructField(foreign, 0) }
        assertFailsWith<IndexOutOfBoundsException> { structs.readStructField(point, 8) }
    }

    @Test
    fun hostAllocatedStructsReachTheGuest() {

        val type = structs.structType(point(1))
        val references = arrayOfNulls<Any>(8).apply { this[6] = "tag" }
        val struct = structs.allocateStruct(type, longArrayOf(11, 0, 0, 0, 0, 0, 0, 9), references)
        val unchecked = structs.unsafeAllocateStruct(type, longArrayOf(12, 0, 0, 0, 0, 0, 0, 9), arrayOfNulls(8))

        structs.unsafeWriteStructReference(unchecked, 5, struct)

        assertIs<StructReference>(struct)
        assertEquals(11, call("x", references = arrayOf(struct)).words[0].toInt())
        assertSame("tag", call("label", references = arrayOf(struct)).references[0])
        assertSame(struct, call("next", references = arrayOf(unchecked)).references[0])
        assertEquals(9L, structs.readStructField(struct, 7))
        assertFailsWith<IllegalArgumentException> {
            structs.allocateStruct(type, LongArray(8), arrayOfNulls<Any>(8).apply { this[5] = "text" })
        }
        assertFailsWith<IllegalArgumentException> { structs.allocateStruct(arrays.arrayType(bytes(1)), LongArray(8), arrayOfNulls(8)) }
        assertFailsWith<IllegalArgumentException> { structs.allocateStruct(HostGcType(UNKNOWN_TYPE), LongArray(8), arrayOfNulls(8)) }
    }

    @Test
    fun exactTypesAcceptSubtypesAndRejectSiblings() {

        val point = point(1)
        val subtype = call("point3", words = longArrayOf(7)).references[0]
        val sibling = call("other").references[0]
        val points = call("make_points", words = longArrayOf(1)).references[0]
        val carry = tag("carry")

        structs.writeStructReference(point, 5, subtype)
        arrays.writeArrayReference(points, 0, subtype)
        tables.writeTable(table("slots"), 0, subtype)
        globals.writeGlobalReference(global("current"), subtype)
        exceptions.createException(carry, LongArray(1), arrayOf(subtype))

        assertEquals(7, call("x", references = arrayOf(subtype)).words[0].toInt())
        assertIs<ExecutionError.Rejected>(failure("x", references = arrayOf(sibling)))
        assertFailsWith<IllegalArgumentException> { structs.writeStructReference(point, 5, sibling) }
        assertFailsWith<IllegalArgumentException> { arrays.writeArrayReference(points, 0, sibling) }
        assertFailsWith<IllegalArgumentException> { tables.writeTable(table("slots"), 0, sibling) }
        assertFailsWith<IllegalArgumentException> { globals.writeGlobalReference(global("current"), sibling) }
        assertFailsWith<IllegalArgumentException> { exceptions.createException(carry, LongArray(1), arrayOf(sibling)) }
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

        val holder = call("holder").references[0]

        structs.writeStructReference(holder, 0, function("triple"))

        assertEquals(15, call("call_held", words = longArrayOf(0, 5), references = arrayOf(holder)).words[0].toInt())
        assertFailsWith<IllegalArgumentException> { structs.writeStructReference(holder, 0, function("inc")) }
    }

    @Test
    fun nullsAreRejectedWhereTheTypeIsNonNullable() {

        val point = point(1)

        assertFailsWith<IllegalArgumentException> { globals.writeGlobalReference(global("origin"), null) }
        assertFailsWith<IllegalArgumentException> { tables.writeTable(table("required"), 0, null) }
        assertFailsWith<IllegalArgumentException> { exceptions.createException(tag("strict_carry"), LongArray(1), arrayOfNulls(1)) }
        assertIs<ExecutionError.Rejected>(failure("x", references = arrayOfNulls(1)))

        globals.writeGlobalReference(global("origin"), point)

        assertSame(point, globals.readGlobalReference(global("origin")))
    }

    @Test
    fun arraysCopyInAndOut() {

        val bytes = bytes(4)
        val longs = call("make_longs", words = longArrayOf(3)).references[0]
        val read = LongArray(3)

        arrays.writeArrayElements(bytes, 1, longArrayOf(2, 3, 4), 0, 3)
        arrays.fillArray(longs, 1, 2, 9)
        arrays.readArrayElements(longs, 0, read, 0, 3)
        arrays.copyArray(bytes, 0, bytes, 3, 1)

        assertIs<ArrayReference>(bytes)
        assertEquals(4, arrays.arrayLength(bytes))
        assertEquals(1 + 2 + 3 + 1, sumBytes(bytes))
        assertContentEquals(longArrayOf(7, 9, 9), read)
        assertEquals(9L, arrays.readArrayElement(longs, 2))
        assertFailsWith<IllegalArgumentException> { arrays.copyArray(longs, 0, bytes, 0, 1) }
        assertFailsWith<IndexOutOfBoundsException> { arrays.readArrayElements(longs, 2, read, 0, 2) }
        assertFailsWith<IllegalArgumentException> { arrays.allocateArray(arrays.arrayType(bytes), -1, 0L) }
    }

    @Test
    fun packedElementsReadAsTheirUnsignedBits() {

        val chars = call("make_chars", words = longArrayOf(2)).references[0]

        arrays.writeArrayElement(chars, 1, 0x1234)

        assertEquals(0xFFFFL, arrays.readArrayElement(chars, 0))
        assertEquals(0x1234, call("char_at", words = longArrayOf(0, 1), references = arrayOf(chars)).words[0].toInt())
    }

    @Test
    fun immutableArraysRejectWrites() {

        val frozen = call("frozen").references[0]

        assertEquals(2L, arrays.readArrayElement(frozen, 1))
        assertFailsWith<IllegalArgumentException> { arrays.writeArrayElement(frozen, 0, 1) }
        assertFailsWith<IllegalArgumentException> { arrays.fillArray(frozen, 0, 1, 1) }
    }

    @Test
    fun referenceArraysHoldCheckedObjects() {

        val points = call("make_points", words = longArrayOf(2)).references[0]
        val point = point(3)
        val type = arrays.arrayType(points)

        arrays.writeArrayReference(points, 1, point)
        arrays.unsafeWriteArrayReference(points, 0, point)

        val filled = arrays.allocateArray(type, 2, point)
        val copied = arrays.allocateArray(type, arrayOf(null, point), 0, 2)

        assertSame(point, call("point_at", words = longArrayOf(0, 1), references = arrayOf(points)).references[0])
        assertSame(point, arrays.readArrayReference(points, 0))
        assertSame(point, arrays.readArrayReference(filled, 1))
        assertNull(arrays.readArrayReference(copied, 0))
        assertSame(point, call("point_at", words = longArrayOf(0, 1), references = arrayOf(copied)).references[0])
        assertFailsWith<IllegalArgumentException> { arrays.writeArrayReference(points, 0, "text") }
        assertFailsWith<IllegalArgumentException> { arrays.allocateArray(type, arrayOf<Any?>("text"), 0, 1) }
        assertFailsWith<IllegalArgumentException> { arrays.readArrayElement(points, 0) }
        assertFailsWith<IllegalArgumentException> { arrays.allocateArray(type, 2, 0L) }
    }

    @Test
    fun referenceArrayCopiesCheckTheDestinationType() {

        val point = point(3)
        val points = call("make_points", words = longArrayOf(2)).references[0]
        val strict = call("make_strict", words = longArrayOf(0, 2), references = arrayOf(point)).references[0]

        arrays.writeArrayReference(points, 1, point)
        arrays.copyArray(points, 1, strict, 0, 1)
        arrays.copyArray(strict, 0, points, 0, 2)

        assertSame(point, arrays.readArrayReference(strict, 0))
        assertSame(point, arrays.readArrayReference(points, 0))
        assertFailsWith<IllegalArgumentException> { arrays.copyArray(call("make_points", words = longArrayOf(1)).references[0], 0, strict, 0, 1) }
    }

    @Test
    fun referenceArrayCopiesFollowSubtyping() {

        val point = point(1)
        val subtype = call("point3", words = longArrayOf(2)).references[0]
        val points = call("make_points", words = longArrayOf(2)).references[0]
        val subtypes = call("make_point3s", words = longArrayOf(2)).references[0]
        val anys = call("make_anys", words = longArrayOf(2)).references[0]

        arrays.writeArrayReference(subtypes, 0, subtype)
        arrays.copyArray(subtypes, 0, points, 0, 1)
        arrays.copyArray(points, 0, anys, 0, 1)
        arrays.writeArrayReference(points, 1, point)
        arrays.writeArrayReference(anys, 1, call("other").references[0])

        assertSame(subtype, arrays.readArrayReference(points, 0))
        assertSame(subtype, arrays.readArrayReference(anys, 0))
        assertFailsWith<IllegalArgumentException> { arrays.copyArray(points, 0, subtypes, 1, 1) }
        assertFailsWith<IllegalArgumentException> { arrays.copyArray(anys, 1, points, 0, 1) }
    }

    @Test
    fun tablesAndGlobalsFromAnotherStoreAreRejected() {

        val foreign = store()
        val instance = instantiate(foreign)
        val exports = instance.instance.exports.associate { export -> export.name.name to export.value }
        val table = foreign.store.tables[(exports.getValue("slots") as ExternalValue.Table).address.address]
        val global = foreign.store.globals[(exports.getValue("current") as ExternalValue.Global).address.address]

        assertFailsWith<IllegalArgumentException> { tables.readTable(table, 0) }
        assertFailsWith<IllegalArgumentException> { tables.copyTable(table, 0, table("slots"), 0, 1) }
        assertFailsWith<IllegalArgumentException> { globals.readGlobalReference(global) }
    }

    @Test
    fun borrowedStorageAliasesTheGuestArray() {

        val bytes = bytes(3)
        val borrowed = arrays.borrow(bytes, ByteArray::class.java)
        val points = call("make_points", words = longArrayOf(1)).references[0]
        val point = point(2)

        borrowed[0] = 10
        arrays.borrow(points, Array<Any?>::class.java)[0] = point

        assertEquals(12, sumBytes(bytes))
        assertSame(point, call("point_at", words = longArrayOf(0, 0), references = arrayOf(points)).references[0])
        assertFailsWith<IllegalArgumentException> { arrays.borrow(bytes, CharArray::class.java) }
    }

    @Test
    fun adoptedStorageAliasesTheHostArray() {

        val type = arrays.arrayType(bytes(1))
        val storage = byteArrayOf(1, 2, 3)
        val before = store.store.allocation.allocatedBytes
        val adopted = arrays.unsafeAllocateArray(type, storage)

        assertTrue(store.store.allocation.allocatedBytes - before >= storage.size)
        assertEquals(6, sumBytes(adopted))

        storage[0] = 5

        assertEquals(10, sumBytes(adopted))
        assertSame(storage, arrays.borrow(adopted, ByteArray::class.java))
        assertFailsWith<IllegalArgumentException> { arrays.unsafeAllocateArray(type, CharArray(2)) }
        assertFailsWith<IllegalArgumentException> {
            arrays.unsafeAllocateArray(arrays.arrayType(call("make_points", words = longArrayOf(1)).references[0]), arrayOfNulls<Any>(1))
        }
    }

    @Test
    fun objectsSurviveCollectionWithoutRetaining() {

        val point = point(21)
        val bytes = arrays.allocateArray(arrays.arrayType(bytes(1)), 2, 4L)

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

        assertEquals(tag("boom"), exceptions.exceptionTag(exception))
        assertEquals(2, exceptions.payloadSize(exception))
        assertEquals(7L, exceptions.readPayload(exception, 0))
        assertSame("payload", exceptions.readPayloadReference(exception, 1))
        assertSame(exception, assertIs<ExecutionError.Thrown<ExceptionReference>>(rethrown).exception)
        assertFailsWith<IllegalArgumentException> { exceptions.readPayload(exception, 1) }
    }

    @Test
    fun guestExceptionsWithSingleKindPayloadsReportTheirSize() {

        val point = point(1)
        val carried = assertIs<ExecutionError.Thrown<ExceptionReference>>(failure("throw_carry", references = arrayOf(point))).exception
        val numbers = assertIs<ExecutionError.Thrown<ExceptionReference>>(failure("throw_numbers", words = longArrayOf(4, 5))).exception

        assertEquals(1, exceptions.payloadSize(carried))
        assertSame(point, exceptions.readPayloadReference(carried, 0))
        assertFailsWith<IllegalArgumentException> { exceptions.readPayload(carried, 0) }
        assertEquals(2, exceptions.payloadSize(numbers))
        assertEquals(5L, exceptions.readPayload(numbers, 1))
        assertFailsWith<IllegalArgumentException> { exceptions.readPayloadReference(numbers, 0) }
    }

    @Test
    fun exceptionsFromAnotherStoreAreRejected() {

        val foreign = foreignException()

        assertFailsWith<IllegalArgumentException> { exceptions.payloadSize(foreign) }
        assertFailsWith<IllegalArgumentException> { exceptions.raise(foreign) }
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

        assertFailsWith<IllegalArgumentException> { exceptions.createException(tag("boom"), LongArray(2), arrayOf(null, function("inc"))) }
        assertFailsWith<IllegalArgumentException> { exceptions.createException(Address.Tag(UNKNOWN_TYPE), LongArray(0), arrayOf()) }
    }

    @Test
    fun tableWritesReachIndirectCalls() {

        val functions = table("functions")

        tables.writeTable(functions, 0, function("inc"))
        val incremented = call("call_slot", words = longArrayOf(0, 5)).words[0]
        tables.writeTable(functions, 0, function("double"))
        val doubled = call("call_slot", words = longArrayOf(0, 5)).words[0]
        tables.unsafeWriteTable(functions, 0, function("inc"))
        val again = call("call_slot", words = longArrayOf(0, 5)).words[0]
        tables.borrowElements(functions)[0] = function("double")
        val borrowed = call("call_slot", words = longArrayOf(0, 5)).words[0]

        assertEquals(6, incremented.toInt())
        assertEquals(10, doubled.toInt())
        assertEquals(6, again.toInt())
        assertEquals(10, borrowed.toInt())
        assertSame(function("double"), tables.readTable(functions, 0))
        assertFailsWith<IllegalArgumentException> { tables.writeTable(functions, 1, "text") }
        assertFailsWith<IllegalArgumentException> { tables.writeTable(functions, 1, foreignFunction()) }
    }

    @Test
    fun tablesHoldObjects() {

        val slots = table("slots")
        val point = point(8)
        val other = point(9)
        val buffer = arrayOfNulls<Any>(2)

        tables.writeTable(slots, 0, point)
        val previous = tables.growTable(slots, 2, other)
        tables.writeTable(slots, 5, point)
        tables.fillTable(slots, 1, other, 2)
        tables.copyTable(slots, 0, slots, 3, 1)
        tables.writeTable(slots, 4, arrayOf<Any?>(point, null), 0, 2)
        tables.readTable(slots, buffer, 4, 2, 0)

        assertEquals(4, previous)
        assertEquals(6, tables.sizeTable(slots))
        assertEquals(-1, tables.growTable(slots, 10, null))
        assertSame(point, call("slot", words = longArrayOf(3)).references[0])
        assertSame(other, call("slot", words = longArrayOf(2)).references[0])
        assertSame(point, buffer[0])
        assertNull(buffer[1])
        assertSame(point, tables.borrowElements(slots)[0])
        assertFailsWith<IllegalArgumentException> { tables.growTable(slots, 1, "text") }
        assertFailsWith<IndexOutOfBoundsException> { tables.fillTable(slots, 5, point, 2) }
    }

    @Test
    fun tableCopiesCheckTheDestinationType() {

        val anything = table("anything")

        tables.writeTable(table("functions"), 0, function("inc"))
        tables.writeTable(anything, 0, call("other").references[0])
        tables.writeTable(anything, 1, point(1))
        tables.copyTable(anything, 1, table("slots"), 0, 1)

        assertFailsWith<IllegalArgumentException> { tables.copyTable(table("functions"), 0, table("slots"), 0, 1) }
        assertFailsWith<IllegalArgumentException> { tables.copyTable(anything, 0, table("slots"), 0, 1) }
    }

    @Test
    fun globalsHoldObjects() {

        val current = global("current")
        val count = global("count")
        val point = point(13)

        globals.writeGlobalReference(current, point)
        globals.writeGlobal(count, 5)

        assertEquals(13, call("current_x").words[0].toInt())
        assertEquals(5, call("read_count").words[0].toInt())
        assertSame(point, globals.readGlobalReference(current))
        assertEquals(5L, globals.readGlobal(count))
        assertFailsWith<IllegalArgumentException> { globals.writeGlobalReference(current, "text") }
        assertFailsWith<IllegalArgumentException> { globals.readGlobal(current) }
        assertFailsWith<IllegalArgumentException> { globals.writeGlobal(global("fixed"), 1) }
        assertFailsWith<IllegalArgumentException> { globals.writeGlobalReference(global("fixed_point"), point) }

        globals.unsafeWriteGlobalReference(current, point(14))

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
        val call = prepareCall(dropped, instance, "x").expect("x")
        val point = point(dropped, instance)

        dropInstance(dropped, instance).expect("drop instance")
        val deinstantiated = call.call(LongArray(call.slots), ReferenceSlots(arrayOf(point)))
        dropStore(dropped).expect("drop store")
        val storeDropped = call.call(LongArray(call.slots), ReferenceSlots(arrayOf(point)))

        assertIs<ExecutionError.Rejected>(assertIs<ChasmResult.Error<InvocationError<*>>>(deinstantiated).error)
        assertIs<ExecutionError.Rejected>(assertIs<ChasmResult.Error<InvocationError<*>>>(storeDropped).error)
    }

    @Test
    fun uncheckedCallsFailInsideTheGuest() {

        val call = prepareCall(store, instance, "x").expect("x")
        val references = ReferenceSlots(arrayOf<Any?>("text"))

        val result = call.unsafeCall(LongArray(call.slots), references)

        val trap = assertIs<ExecutionError.Trapped>(assertIs<ChasmResult.Error<InvocationError<*>>>(result).error).trap
        assertEquals(TrapReason.CAST_FAILURE, trap.reason)
    }

    private fun point(x: Int): Any? {
        return call("point", words = longArrayOf(x.toLong())).references[0]
    }

    private fun bytes(length: Int): Any? {
        return call("make_bytes", words = longArrayOf(length.toLong())).references[0]
    }

    private fun sumBytes(bytes: Any?): Int {
        return call("sum_bytes", references = arrayOf(bytes)).words[0].toInt()
    }

    private fun point(
        store: Store,
        instance: Instance,
    ): Any? {

        val call = prepareCall(store, instance, "point").expect("point")
        val references = ReferenceSlots(call.slots)

        call.call(longArrayOf(1), references).expect("point")

        return references.objects[0]
    }

    private fun foreignPoint(): Any? {
        val foreign = store()
        return point(foreign, instantiate(foreign))
    }

    private fun foreignException(): Any? {

        val foreign = store()
        val instance = instantiate(foreign)
        val call = prepareCall(foreign, instance, "throw").expect("throw")
        val result = call.call(LongArray(call.slots), ReferenceSlots(call.slots))

        return assertIs<ExecutionError.Thrown<*>>(assertIs<ChasmResult.Error<InvocationError<*>>>(result).error).exception
    }

    private fun foreignFunction(): Any {
        val foreign = store()
        return _functionReference(foreign, instantiate(foreign).exports.first { export -> export.name == "inc" }.value as Function)
    }

    private fun call(
        name: String,
        words: LongArray = LongArray(0),
        references: Array<Any?> = arrayOf(),
    ): Lanes {

        val call = prepareCall(store, instance, name).expect(name)
        val lanes = Lanes(words.copyOf(call.slots), references.copyOf(call.slots))

        call.call(lanes.words, ReferenceSlots(lanes.references)).expect(name)

        return lanes
    }

    private fun failure(
        name: String,
        words: LongArray = LongArray(0),
        references: Array<Any?> = arrayOf(),
    ): InvocationError<ExceptionReference> {

        val call = prepareCall(store, instance, name).expect(name)
        val result = call.call(words.copyOf(call.slots), ReferenceSlots(references.copyOf(call.slots)))

        return assertIs<ChasmResult.Error<InvocationError<ExceptionReference>>>(result).error
    }

    private fun export(name: String): ExternalValue {
        return instance.instance.exports.first { export -> export.name.name == name }.value
    }

    private fun table(name: String): TableInstance {
        return store.store.tables[(export(name) as ExternalValue.Table).address.address]
    }

    private fun global(name: String): GlobalInstance {
        return store.store.globals[(export(name) as ExternalValue.Global).address.address]
    }

    private fun tag(name: String): Address.Tag {
        return (export(name) as ExternalValue.Tag).address
    }

    private fun function(name: String): Any {
        return _functionReference(store, instance.exports.first { export -> export.name == name }.value as Function)
    }

    private fun instantiate(store: Store): Instance {

        val module = runBlocking { module(resourceBytes("modules/object_calls.wasm"), validate = true) }.expect("module")

        return runBlocking { instance(store, module, imports(store)) }.expect("instance")
    }

    private fun imports(store: Store): List<Import> {
        return listOf(
            Import(
                "host",
                "raise",
                referenceFunction(
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
            Import(
                "host",
                "echo",
                referenceFunction(store, echoType(), echoFunction),
            ),
        )
    }

    private fun echoType(): FunctionType {

        val module = runBlocking { module(resourceBytes("modules/object_calls.wasm"), validate = true) }.expect("module")
        val echo = moduleInfo(module).imports.first { import -> import.entityName == "echo" }

        return (echo.type as ExternalType.Function).functionType
    }

    private val raise = ReferenceHostFunction { parameters, _ ->
        val payload = arrayOf(null, parameters.readReference(1).value)
        exceptions.raise(exceptions.createException(tag("boom"), longArrayOf(parameters.readI32(0).toLong(), 0), payload))
    }

    private val echoFunction = ReferenceHostFunction { parameters, results ->
        val result = when (val current = echo) {
            Echo.Parameter -> parameters.readReference(0)
            is Echo.Value -> WasmReference(current.value)
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
    }
}

private fun functionType(builder: FunctionTypeBuilder.() -> Unit): FunctionType {
    return FunctionTypeBuilder().apply(builder).build()
}
