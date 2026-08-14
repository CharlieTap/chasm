package io.github.charlietap.chasm.decoder.layout

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ModuleLayoutScannerTest {

    @Test
    fun `scans code body ranges`() {
        val bytes = module(
            section(1, 0),
            section(
                10,
                2,
                2,
                0,
                0x0B,
                3,
                0,
                0x01,
                0x0B,
            ),
            section(11, 0),
        )

        val ranges = assertNotNull(ModuleLayoutScanner(bytes))

        assertContentEquals(intArrayOf(14, 17), ranges.starts)
        assertContentEquals(intArrayOf(17, 21), ranges.ends)
        assertContentEquals(intArrayOf(2, 3), ranges.sizes)
    }

    @Test
    fun `scans a module without a code section`() {
        val ranges = assertNotNull(ModuleLayoutScanner(module(section(1, 0))))

        assertEquals(0, ranges.size)
    }

    @Test
    fun `rejects truncated section and body ranges`() {
        assertNull(ModuleLayoutScanner(module(1, 2, 0)))
        assertNull(ModuleLayoutScanner(module(10, 3, 1, 3, 0)))
    }

    @Test
    fun `rejects invalid unsigned lengths`() {
        assertNull(ModuleLayoutScanner(module(1, 0x80, 0x80, 0x80, 0x80, 0x10)))
        assertNull(ModuleLayoutScanner(module(1, 0xFF, 0xFF, 0xFF, 0xFF, 0x0F)))
    }

    @Test
    fun `rejects duplicate code sections`() {
        assertNull(ModuleLayoutScanner(module(section(10, 0), section(10, 0))))
    }

    private fun module(vararg payload: Byte): ByteArray = MODULE_HEADER + payload

    private fun module(vararg payload: Int): ByteArray = module(*payload.map(Int::toByte).toByteArray())

    private fun module(vararg sections: ByteArray): ByteArray = MODULE_HEADER + sections.fold(ByteArray(0)) { bytes, section ->
        bytes + section
    }

    private fun section(
        id: Int,
        vararg payload: Int,
    ): ByteArray = byteArrayOf(id.toByte(), payload.size.toByte()) + payload.map(Int::toByte)

    private companion object {
        val MODULE_HEADER = byteArrayOf(0x00, 0x61, 0x73, 0x6D, 0x01, 0x00, 0x00, 0x00)
    }
}
