package io.github.charlietap.chasm.aot.compiled

import io.github.charlietap.chasm.compiler.backend.jvm.JvmOptions
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.runtime.value.NumberValue.F32
import io.github.charlietap.chasm.runtime.value.NumberValue.F64
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import io.github.charlietap.chasm.runtime.value.NumberValue.I64
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class JvmExecutionTest {

    @Test
    fun `an exported add runs compiled`() {
        val fixture = CompiledFixture(fixture("add"))

        assertEquals(listOf(I32(42)), fixture.call("add", I32(20), I32(22)))
        assertEquals(listOf(I32(Int.MIN_VALUE)), fixture.call("add", I32(Int.MAX_VALUE), I32(1)))
        assertEquals(listOf(I32(-3)), fixture.call("add", I32(-1), I32(-2)))
    }

    @Test
    fun `generated classes verify`() {
        listOf("add", "scalar", "control", "huge").forEach { name -> generate(fixture(name)).verify() }
    }

    @Test
    fun `scalar operations match Wasm`() {
        val fixture = CompiledFixture(fixture("scalar"))

        assertEquals(listOf(F64(Math.sqrt(3.0 * 12.0))), fixture.call("mix", I64(3), F64(12.0)))
        assertEquals(listOf(I32(-1 ushr 1)), fixture.call("div_u", I32(-2), I32(1).let { I32(2) }))
        assertEquals(listOf(I32(1)), fixture.call("lt_u", I64(1), I64(-1)))
        assertEquals(listOf(F32(2f)), fixture.call("pick", I32(0), F32(1f), F32(2f)))
        assertEquals(listOf(I32(-7)), fixture.call("trunc", F32(-7.9f)))
        assertTrap(fixture.invoke("div_u", I32(1), I32(0)))
        assertTrap(fixture.invoke("trunc", F32(Float.NaN)))
        assertTrap(fixture.invoke("trap"))
    }

    @Test
    fun `control flow matches Wasm`() {
        val fixture = CompiledFixture(fixture("control"))

        assertEquals(listOf(I64(4950)), fixture.call("sum", I32(100)))
        assertEquals(listOf(I32(10)), fixture.call("classify", I32(0)))
        assertEquals(listOf(I32(20)), fixture.call("classify", I32(1)))
        assertEquals(listOf(I32(30)), fixture.call("classify", I32(7)))
        assertEquals(listOf(I32(30)), fixture.call("classify", I32(-1)))
        assertEquals(listOf(I32(5)), fixture.call("max", I32(-3), I32(5)))
        assertEquals(listOf(I32(1)), fixture.call("swap_loop", I32(2), I32(1), I32(4)))
    }

    @Test
    fun `a function too large for one method runs split and agrees with the interpreter`() {
        val bytes = fixture("huge")
        val compiled = CompiledFixture(bytes)
        val interpreted = InterpretedFixture(bytes)
        val split = compiled.generated.classes.values.any { bytes -> String(bytes, Charsets.ISO_8859_1).contains("\$c1") }

        assertTrue(split, "the big function was not split")
        listOf(1, 3).forEach { n ->
            assertEquals(interpreted.call("big", I32(n)), compiled.call("big", I32(n)))
        }
    }

    @Test
    fun `a function's own method budget takes the place of the module's`() {
        val bytes = fixture("huge")
        val interpreted = InterpretedFixture(bytes)
        val module = CompiledFixture(bytes)
        val own = CompiledFixture(bytes, options = JvmOptions(functionMaxMethodBytes = mapOf(3 to 2_000)))

        assertTrue(chunks(own, function = 3) > chunks(module, function = 3))
        listOf(1, 3).forEach { n -> assertEquals(interpreted.call("big", I32(n)), own.call("big", I32(n))) }
    }

    private fun chunks(
        fixture: CompiledFixture,
        function: Int,
    ): Int = fixture.generated.classes.values
        .flatMap { bytes -> Regex("f$function\\\$c(\\d+)").findAll(String(bytes, Charsets.ISO_8859_1)).map { it.value }.toList() }
        .toSet().size

    @Test
    fun `parameters too many for a method travel in an array`() {
        val bytes = fixture("huge")
        val compiled = CompiledFixture(bytes)
        val arguments = Array(110) { index -> I32(index * 3 - 50) }

        assertEquals(listOf(I32((1..110).sum())), compiled.call("call_wide"))
        assertEquals(listOf(I32((0 until 110).sumOf { it * 3 - 50 })), compiled.call("wide", *arguments))
    }

    @Test
    fun `a module with more functions than one class can name installs them all`() {
        val functions = 25_000
        val compiled = CompiledFixture(manyFunctions(functions))

        assertTrue(compiled.generated.classes.keys.count { name -> "/Install" in name } > 1)
        assertEquals(listOf(I32(0)), compiled.call("first"))
        assertEquals(listOf(I32(functions - 1)), compiled.call("last"))
    }

    @Test
    fun `generation is reproducible`() {
        listOf("scalar", "control", "huge").forEach { name ->
            val first = generate(fixture(name)).classes
            val second = generate(fixture(name)).classes

            assertEquals(first.keys.toList(), second.keys.toList())
            first.forEach { (key, bytes) -> assertTrue(bytes.contentEquals(second.getValue(key)), "$name: $key differs") }
        }
    }

    private fun manyFunctions(
        count: Int,
    ): ByteArray {
        val module = ArrayList<Byte>()

        fun ArrayList<Byte>.bytes(
            vararg values: Int,
        ) = values.forEach { value -> add(value.toByte()) }

        fun ArrayList<Byte>.leb(
            value: Int,
        ) {
            var rest = value
            do {
                val low = rest and 0x7f
                rest = rest ushr 7
                add((if (rest != 0) low or 0x80 else low).toByte())
            } while (rest != 0)
        }

        fun ArrayList<Byte>.signed(
            value: Int,
        ) {
            var rest = value
            while (true) {
                val low = rest and 0x7f
                rest = rest shr 7
                val done = (rest == 0 && low and 0x40 == 0) || (rest == -1 && low and 0x40 != 0)
                add((if (done) low else low or 0x80).toByte())
                if (done) return
            }
        }

        fun ArrayList<Byte>.name(
            text: String,
        ) {
            leb(text.length)
            text.forEach { character -> add(character.code.toByte()) }
        }

        fun section(
            id: Int,
            content: ArrayList<Byte>.() -> Unit,
        ) {
            val body = ArrayList<Byte>().apply(content)
            module.bytes(id)
            module.leb(body.size)
            module += body
        }

        module.bytes(0x00, 0x61, 0x73, 0x6d, 0x01, 0x00, 0x00, 0x00)
        section(1) { bytes(1, 0x60, 0, 1, 0x7f) }
        section(3) {
            leb(count)
            repeat(count) { add(0) }
        }
        section(7) {
            leb(2)
            name("first")
            add(0)
            leb(0)
            name("last")
            add(0)
            leb(count - 1)
        }
        section(10) {
            leb(count)
            repeat(count) { index ->
                val body = ArrayList<Byte>().apply {
                    bytes(0, 0x41)
                    signed(index)
                    bytes(0x0b)
                }
                leb(body.size)
                addAll(body)
            }
        }
        return module.toByteArray()
    }

    @Test
    fun `memory accesses with skipped bounds checks trap where the interpreter's do`() {
        val bytes = fixture("bounds")
        val interpreted = InterpretedFixture(bytes)
        val compiled = CompiledFixture(bytes)
        val last = PAGE - 12
        val calls = listOf(
            "covered" to listOf(I32(last)),
            "covered" to listOf(I32(last + 1)),
            "covered" to listOf(I32(-1)),
            "further" to listOf(I32(last)),
            "further" to listOf(I32(last + 4)),
            "rewritten" to listOf(I32(last)),
            "joined" to listOf(I32(last), I32(0)),
            "joined" to listOf(I32(PAGE - 4), I32(1)),
            "arms" to listOf(I32(last), I32(1)),
            "arms" to listOf(I32(last), I32(0)),
            "looped" to listOf(I32(PAGE - 4), I32(3)),
            "looped" to listOf(I32(PAGE - 3), I32(0)),
            "copied" to listOf(I32(last)),
            "copied" to listOf(I32(last + 1)),
        )

        calls.forEach { (name, arguments) ->
            val expected = interpreted.invoke(name, *arguments.toTypedArray())
            val actual = compiled.invoke(name, *arguments.toTypedArray())
            when (expected) {
                is ChasmResult.Success -> assertEquals(expected, actual, "$name $arguments")
                is ChasmResult.Error -> assertTrap(actual)
            }
        }
    }

    @Test
    fun `a loop split into the smallest chunks agrees with the interpreter`() {
        val bytes = fixture("loops")
        val interpreted = InterpretedFixture(bytes)
        val compiled = CompiledFixture(bytes, options = JvmOptions(splitInstructions = 2))

        listOf(0, 1, 7, 100).forEach { n ->
            assertEquals(interpreted.call("loops", I32(n)), compiled.call("loops", I32(n)), "n = $n")
        }
    }

    private fun assertTrap(
        result: ChasmResult<*, *>,
    ) {
        assertIs<ChasmResult.Error<*>>(result)
        assertTrue(result.toString().isNotEmpty())
    }

    private companion object {
        const val PAGE = 65_536
    }
}
