package io.github.charlietap.chasm.compiler.backend.abi

import io.github.charlietap.chasm.runtime.compiled.unpackLongs
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class PackedLongsTest {

    @Test
    fun `packed longs unpack as they were`() {
        val random = Random(42)
        val values = LongArray(20_000) { index ->
            when (index % 4) {
                0 -> index.toLong()
                1 -> -index.toLong()
                2 -> random.nextLong()
                else -> listOf(0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE, 63L, 64L, -64L, -65L)[index % 8]
            }
        }

        assertContentEquals(values, unpackLongs(packLongs(values).toTypedArray()))
    }

    @Test
    fun `boundary values unpack as they were`() {
        val values = longArrayOf(0L, -1L, Long.MIN_VALUE, Long.MAX_VALUE, 63L, 64L, -64L, -65L, Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong())

        assertContentEquals(values, unpackLongs(packLongs(values).toTypedArray()))
        assertContentEquals(values, unpackLongs(packLongs(values, maxBytes = 20).toTypedArray()))
    }

    @Test
    fun `a value larger than the limit is rejected`() {
        assertFailsWith<IllegalArgumentException> { packLongs(longArrayOf(0L), maxBytes = 1) }
    }

    @Test
    fun `parts stay within the constant pool's limit`() {
        val parts = packLongs(LongArray(100_000) { Long.MIN_VALUE }, maxBytes = 1_000)

        assertTrue(parts.size > 1)
        parts.forEach { part -> assertTrue(part.sumOf { if (it.code in 1..0x7F) 1L else 2L } <= 1_000) }
    }

    @Test
    fun `no longs pack to one empty part`() {
        assertContentEquals(LongArray(0), unpackLongs(packLongs(LongArray(0)).toTypedArray()))
    }
}
