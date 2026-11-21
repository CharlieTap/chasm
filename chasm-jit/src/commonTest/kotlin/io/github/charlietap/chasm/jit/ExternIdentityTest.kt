package io.github.charlietap.chasm.jit

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.embedding.compiled.Instance
import io.github.charlietap.chasm.embedding.compiled.PreparedFunction
import io.github.charlietap.chasm.embedding.compiled.instance
import io.github.charlietap.chasm.embedding.compiled.invoke
import io.github.charlietap.chasm.embedding.compiled.invokeUnboxed
import io.github.charlietap.chasm.embedding.compiled.prepareFunction
import io.github.charlietap.chasm.embedding.compiled.value.ReferenceValue
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.expect
import io.github.charlietap.chasm.reference.I31Reference
import io.github.charlietap.chasm.reference.StructReference
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
        val words = LongArray(call.slots)
        val references = arrayOfNulls<Any>(call.slots)
        references[0] = "host"

        call.invokeUnboxed(words, references).expect("casts")

        assertEquals(1, words[0].toInt())
    }

    @Test
    fun hostObjectsAreRejectedWhereStructsAreExpected() {

        val call = prepare("takes_box")
        val references = arrayOfNulls<Any>(call.slots)
        references[0] = "not a struct"

        val result = call.invokeUnboxed(LongArray(call.slots), references)

        assertIs<ChasmResult.Error<*>>(result)
    }

    @Test
    fun nullIsRejectedForNonNullableParameters() {

        val nonNullable = prepare("takes_box")
        val nullable = prepare("takes_nullable_box")
        val words = LongArray(nullable.slots)

        val rejected = nonNullable.invokeUnboxed(LongArray(nonNullable.slots), arrayOfNulls(nonNullable.slots))
        nullable.invokeUnboxed(words, arrayOfNulls(nullable.slots)).expect("takes_nullable_box")

        assertIs<ChasmResult.Error<*>>(rejected)
        assertEquals(1, words[0].toInt())
    }

    @Test
    fun hostBuiltI31ReferencesKeepThirtyOneBits() {

        val unsigned = prepare("i31_unsigned")
        val same = prepare("same_i31")
        val words = LongArray(1)
        val references = arrayOfNulls<Any>(1)
        references[0] = I31Reference(-1)

        unsigned.invokeUnboxed(words, references).expect("i31_unsigned")
        val bits = words[0].toInt()
        same.invokeUnboxed(words, references).expect("same_i31")

        assertEquals(0x7FFFFFFF, bits)
        assertEquals(1, words[0].toInt())
    }

    @Test
    fun forgedReferenceSubclassesAreRejected() {

        for (name in listOf("takes_box", "takes_eq")) {

            val call = prepare(name)
            val references = arrayOfNulls<Any>(call.slots)
            references[0] = Forged()

            val result = call.invokeUnboxed(LongArray(call.slots), references)

            assertIs<ChasmResult.Error<*>>(result, name)
        }
    }

    @Test
    fun equalHostObjectsStayDistinctThroughTheEmbeddingApi() {

        val first = Label("equal")
        val second = Label("equal")

        val firstBack = boxedIdentity(first)
        val secondBack = boxedIdentity(second)

        assertSame(first, firstBack)
        assertSame(second, secondBack)
    }

    @Test
    fun structsCarryEveryStorageKind() {

        val make = prepare("make_all")
        val words = LongArray(make.slots)
        val references = arrayOfNulls<Any>(make.slots)
        val label = Label("field")
        words[0] = -3L
        words[1] = 0xFFFE
        words[2] = 123_456L
        words[3] = Long.MIN_VALUE + 7
        words[4] = 1.5f.toRawBits().toLong()
        words[5] = 2.25.toRawBits()
        references[6] = label

        make.invokeUnboxed(words, references).expect("make_all")
        val all = assertIs<StructReference>(references[0])

        assertEquals(-3, readWord("read_i8", all).toInt())
        assertEquals(0xFFFE, readWord("read_i16", all).toInt())
        assertEquals(123_456, readWord("read_i32", all).toInt())
        assertEquals(Long.MIN_VALUE + 7, readWord("read_i64", all))
        assertEquals(1.5f, Float.fromBits(readWord("read_f32", all).toInt()))
        assertEquals(2.25, Double.fromBits(readWord("read_f64", all)))
        assertSame(all, readReference("read_self", all))
        assertSame(label, readReference("read_extern", all))
    }

    private fun roundTrip(
        name: String,
        value: Any,
    ): Any? {

        val call = prepare(name)
        val references = arrayOfNulls<Any>(call.slots)
        references[0] = value

        call.invokeUnboxed(LongArray(call.slots), references).expect(name)

        return references[0]
    }

    private fun call(
        name: String,
        value: Any,
    ) {

        val call = prepare(name)
        val references = arrayOfNulls<Any>(call.slots)
        references[0] = value

        call.invokeUnboxed(LongArray(call.slots), references).expect(name)
    }

    private fun result(name: String): Any? {

        val call = prepare(name)
        val references = arrayOfNulls<Any>(call.slots)

        call.invokeUnboxed(LongArray(call.slots), references).expect(name)

        return references[0]
    }

    private fun readWord(
        name: String,
        all: StructReference,
    ): Long {

        val call = prepare(name)
        val words = LongArray(call.slots)
        val references = arrayOfNulls<Any>(call.slots)
        references[0] = all

        call.invokeUnboxed(words, references).expect(name)

        return words[0]
    }

    private fun readReference(
        name: String,
        all: StructReference,
    ): Any? {

        val call = prepare(name)
        val references = arrayOfNulls<Any>(call.slots)
        references[0] = all

        call.invokeUnboxed(LongArray(call.slots), references).expect(name)

        return references[0]
    }

    private fun boxedIdentity(value: Any): Any {

        val result = invoke(store, instance, "identity", listOf(ReferenceValue.Extern(value))).expect("identity")

        return assertIs<ReferenceValue.Extern>(result.single()).value
    }

    private fun prepare(name: String): PreparedFunction {
        return prepareFunction(store, instance, name).expect(name)
    }

    private fun instantiate(): Instance {

        val module = runBlocking { module(resourceBytes("modules/extern_identity.wasm"), validate = true) }.expect("module")

        return runBlocking { instance(store, module, emptyList()) }.expect("instance")
    }

    private data class Label(val text: String)

    @OptIn(InternalChasmApi::class)
    private class Forged : StructReference()
}
