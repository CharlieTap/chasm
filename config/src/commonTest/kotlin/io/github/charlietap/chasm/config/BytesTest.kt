package io.github.charlietap.chasm.config

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BytesTest {

    @Test
    fun `sizes are 1024-based and a page is 64 KiB`() {
        assertEquals(1_024L, 1.KB.value)
        assertEquals(1_048_576L, 1.MB.value)
        assertEquals(1_073_741_824L, 1.GB.value)
        assertEquals(65_536L, 1.pages.value)
        assertEquals(7L, 7.bytes.value)
    }

    @Test
    fun `a size prints in its largest whole unit`() {
        assertEquals("256 MB", 256.MB.toString())
        assertEquals("1500 bytes", Bytes(1500).toString())
        assertEquals("64 KB", 1.pages.toString())
        assertEquals("2 GB", 2.GB.toString())
        assertEquals("0 bytes", Bytes(0).toString())
    }

    @Test
    fun `a negative size is refused`() {
        assertFailsWith<IllegalArgumentException> { Bytes(-1) }
    }
}
