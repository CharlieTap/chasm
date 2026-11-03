package io.github.charlietap.chasm.compiler.backend.dex

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DexFixtureTest {

    @Test
    fun `scalar fixture generates`() = golden("scalar")

    @Test
    fun `control fixture generates`() = golden("control")

    @Test
    fun `memory fixture generates`() = golden("memory")

    @Test
    fun `memory fixture generates unsafe accesses`() = golden("memory", "memory_unsafe", DexOptions(stacktraces = true, byteArrayBaseOffset = 16L))

    @Test
    fun `calls fixture generates`() = golden("calls")

    @Test
    fun `gc fixture generates`() = golden("gc")

    @Test
    fun `exception handling fixture generates`() = golden("eh")

    @Test
    fun `tail call fixture generates`() = golden("tail")

    @Test
    fun `wide arithmetic fixture generates`() = golden("wide")

    @Test
    fun `gc benchmark fixture generates`() = golden("bench_gc")

    @Test
    fun `benchmark fixture generates`() = golden("bench")

    @Test
    fun `a function with more registers than a method can have fails generation`() {
        val failure = assertFailsWith<UnsupportedFunctions> { generate(decodeModule(fixture("limits"))) }

        assertEquals(mapOf(2 to "frame too large"), failure.reasons)
    }

    @Test
    fun `repeated accesses past the address space generate code that traps`() {
        val generated = generate(decodeModule(fixture("large_offset")))

        assertEquals(emptyList(), zeroOffsetBranches(generated.dex))
    }

    @Test
    fun `generation is deterministic`() {
        val module = decodeModule(fixture("bench_gc"))

        val first = generate(module)
        val second = generate(module)

        assertEquals(first.dex.size, second.dex.size)
        first.dex.indices.forEach { index -> assertContentEquals(first.dex[index], second.dex[index]) }
    }

    private fun golden(
        name: String,
        golden: String = name,
        options: DexOptions = DexOptions(stacktraces = true),
    ) {
        val generated = generate(decodeModule(fixture(name)), options)

        assertEquals(emptyList(), zeroOffsetBranches(generated.dex), "branches the verifier rejects")
        assertGolden("$golden.smali", renderSmali(generated.dex))
    }
}
