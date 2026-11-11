package io.github.charlietap.chasm.jit.internal

import io.github.charlietap.chasm.compiler.backend.dex.DexModule
import java.nio.ByteBuffer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CachedCompilationTest {

    private val module = DexModule(
        dex = listOf(byteArrayOf(1, 2, 3), byteArrayOf(4, 5, 6, 7)),
        installer = "chasm.Installer",
        inlineMemory = true,
        chunkStarts = mapOf(3 to intArrayOf(0, 40), 9 to intArrayOf(0, 12, 80)),
    )

    @Test
    fun `an entry round trips several dex files and chunk starts`() {
        val decoded = checkNotNull(decodeCompilation(encodeCompilation(PACKAGE, module), PACKAGE))

        assertEquals(module.dex.map(ByteArray::toList), decoded.dex.map(::contents))
        assertEquals(module.installer, decoded.installer)
        assertTrue(decoded.inlineMemory)
        assertEquals(module.chunkStarts.mapValues { (_, starts) -> starts.toList() }, decoded.chunkStarts.mapValues { (_, starts) -> starts.toList() })
    }

    @Test
    fun `an entry for another package decodes to nothing`() {
        assertNull(decodeCompilation(encodeCompilation(PACKAGE, module), PACKAGE + "_2"))
    }

    @Test
    fun `a truncated entry decodes to nothing`() {
        val entry = encodeCompilation(PACKAGE, module)

        (0 until entry.size).forEach { size -> assertNull(decodeCompilation(entry.copyOf(size), PACKAGE), "truncated to $size") }
    }

    @Test
    fun `an entry with trailing bytes decodes to nothing`() {
        assertNull(decodeCompilation(encodeCompilation(PACKAGE, module) + 0, PACKAGE))
    }

    private fun contents(
        buffer: ByteBuffer,
    ): List<Byte> = ByteArray(buffer.remaining()).also { bytes -> buffer.duplicate().get(bytes) }.toList()

    private companion object {
        const val PACKAGE = "chasm.jit.h00"
    }
}
