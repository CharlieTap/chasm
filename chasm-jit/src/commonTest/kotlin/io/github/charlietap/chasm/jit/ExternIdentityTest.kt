package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.extern.createExternRef
import io.github.charlietap.chasm.embedding.invoke
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class ExternIdentityTest {

    private val store = store()
    private val instance by lazy { instantiate() }

    @Test
    fun hostObjectsComeBackAsTheSameObject() {

        val text = "hello"
        val first = Label("same")
        val second = Label("same")
        val bytes = byteArrayOf(1, 2, 3)

        for (value in listOf<Any>(text, first, second, bytes)) {
            for (name in listOf("identity", "through_any")) {
                assertSame(value, roundTrip(name, value), name)
            }
        }

        assertNotSame(roundTrip("identity", first), roundTrip("identity", second))
    }

    @Test
    fun hostObjectsSurviveGlobalsAndTables() {

        val value = Label("stored")

        call("save", value)
        call("put", value)

        assertSame(value, result("saved"))
        assertSame(value, result("take"))
    }

    @Test
    fun internalisedHostObjectsMatchOnlyAny() {

        val call = prepare("casts")
        val stack = LongArray(call.slots)
        val references = ReferenceSlots(call.slots)
        references[0] = externReference("host")

        call.call(stack, references).expect("casts")

        assertEquals(1, stack[0].toInt())
    }

    @Test
    fun hostObjectsAreRejectedWhereStructsAreExpected() {

        val call = prepare("takes_box")
        val references = ReferenceSlots(call.slots)
        references[0] = externReference("not a struct")

        val result = call.call(LongArray(call.slots), references)

        assertIs<ChasmResult.Error<*>>(result)
    }

    @Test
    fun nullIsRejectedForNonNullableParameters() {

        val nonNullable = prepare("takes_box")
        val nullable = prepare("takes_nullable_box")
        val stack = LongArray(nullable.slots)

        val rejected = nonNullable.call(LongArray(nonNullable.slots), ReferenceSlots(nonNullable.slots))
        nullable.call(stack, ReferenceSlots(nullable.slots)).expect("takes_nullable_box")

        assertIs<ChasmResult.Error<*>>(rejected)
        assertEquals(1, stack[0].toInt())
    }

    @Test
    fun hostBuiltI31ReferencesKeepThirtyOneBits() {

        val unsigned = prepare("i31_unsigned")
        val same = prepare("same_i31")
        val stack = LongArray(1)
        val references = ReferenceSlots(1)
        references[0] = externReference(I31Reference(-1))

        unsigned.call(stack, references).expect("i31_unsigned")
        val bits = stack[0].toInt()
        same.call(stack, references).expect("same_i31")

        assertEquals(0x7FFFFFFF, bits)
        assertEquals(1, stack[0].toInt())
    }

    @Test
    fun forgedReferenceSubclassesAreRejected() {

        for (name in listOf("takes_box", "takes_eq")) {

            val call = prepare(name)
            val references = ReferenceSlots(call.slots)
            references[0] = externReference(Forged())

            val result = call.call(LongArray(call.slots), references)

            assertIs<ChasmResult.Error<*>>(result, name)
        }
    }

    @Test
    fun equalHostObjectsStayDistinctThroughTheEmbeddingApi() {

        val first = Label("equal")
        val second = Label("equal")

        val firstBack = classicIdentity(first)
        val secondBack = classicIdentity(second)

        assertSame(first, firstBack)
        assertSame(second, secondBack)
    }

    @Test
    fun structsCarryEveryStorageKind() {

        val make = prepare("make_all")
        val stack = LongArray(make.slots)
        val references = ReferenceSlots(make.slots)
        val label = Label("field")
        stack[0] = -3L
        stack[1] = 0xFFFE
        stack[2] = 123_456L
        stack[3] = Long.MIN_VALUE + 7
        stack[4] = 1.5f.toRawBits().toLong()
        stack[5] = 2.25.toRawBits()
        references[6] = externReference(label)

        make.call(stack, references).expect("make_all")
        val all = references[0]

        assertEquals(WasmReferenceKind.Struct, all.kind)
        assertEquals(-3, readWord("read_i8", all).toInt())
        assertEquals(0xFFFE, readWord("read_i16", all).toInt())
        assertEquals(123_456, readWord("read_i32", all).toInt())
        assertEquals(Long.MIN_VALUE + 7, readWord("read_i64", all))
        assertEquals(1.5f, Float.fromBits(readWord("read_f32", all).toInt()))
        assertEquals(2.25, Double.fromBits(readWord("read_f64", all)))
        assertEquals(all, readReference("read_self", all))
        assertSame(label, hostValue(store, readReference("read_extern", all)))
    }

    private fun roundTrip(
        name: String,
        value: Any,
    ): Any? {

        val call = prepare(name)
        val references = ReferenceSlots(call.slots)
        references[0] = externReference(value)

        call.call(LongArray(call.slots), references).expect(name)

        return hostValue(store, references[0])
    }

    private fun call(
        name: String,
        value: Any,
    ) {

        val call = prepare(name)
        val references = ReferenceSlots(maxOf(call.slots, 1))
        references[0] = externReference(value)

        call.call(LongArray(maxOf(call.slots, 1)), references).expect(name)
    }

    private fun result(name: String): Any? {

        val call = prepare(name)
        val references = ReferenceSlots(call.slots)

        call.call(LongArray(call.slots), references).expect(name)

        return hostValue(store, references[0])
    }

    private fun readWord(
        name: String,
        all: WasmReference,
    ): Long {

        val call = prepare(name)
        val stack = LongArray(call.slots)
        val references = ReferenceSlots(call.slots)
        references[0] = all

        call.call(stack, references).expect(name)

        return stack[0]
    }

    private fun readReference(
        name: String,
        all: WasmReference,
    ): WasmReference {

        val call = prepare(name)
        val references = ReferenceSlots(call.slots)
        references[0] = all

        call.call(LongArray(call.slots), references).expect(name)

        return references[0]
    }

    private fun classicIdentity(value: Any): Any? {

        val extern = createExternRef(store, value).expect("extern").asValue().expect("value")
        val result = invoke(store, instance, "identity", listOf(extern)).expect("identity")

        return hostValue(store, wasmReference(store, result.single() as ReferenceValue))
    }

    private fun prepare(name: String): PreparedCall {
        return prepareCall(store, instance, name).expect(name)
    }

    private fun instantiate(): Instance {

        val module = runBlocking { module(resourceBytes("modules/extern_identity.wasm"), validate = true) }.expect("module")

        return runBlocking { instance(store, module, emptyList()) }.expect("instance")
    }

    private data class Label(val text: String)

    @OptIn(InternalChasmApi::class)
    private class Forged : StructReference()
}
