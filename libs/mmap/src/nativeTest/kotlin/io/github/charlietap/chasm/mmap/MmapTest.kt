@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package io.github.charlietap.chasm.mmap

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.NativePtr
import kotlinx.cinterop.get
import kotlinx.cinterop.nativeNullPtr
import kotlinx.cinterop.plus
import kotlinx.cinterop.set
import kotlinx.cinterop.toCPointer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class MmapTest {

    @Test
    fun `committed mapping exposes its first and last bytes`() {
        val address = reserve(TEST_PAGE_BYTES)
        try {
            commit(address, TEST_PAGE_BYTES)
            val bytes = address.bytes()

            assertEquals(0.toByte(), bytes[0])
            assertEquals(0.toByte(), bytes[TEST_PAGE_BYTES.toInt() - 1])

            bytes[0] = 0x12.toByte()
            bytes[TEST_PAGE_BYTES.toInt() - 1] = 0x34.toByte()

            assertEquals(0x12.toByte(), bytes[0])
            assertEquals(0x34.toByte(), bytes[TEST_PAGE_BYTES.toInt() - 1])
        } finally {
            release(address, TEST_PAGE_BYTES)
        }
    }

    @Test
    fun `separate ranges can be committed within one reservation`() {
        val reservationSize = TEST_PAGE_BYTES * 2
        val address = reserve(reservationSize)
        try {
            val secondPage = address + TEST_PAGE_BYTES
            commit(secondPage, TEST_PAGE_BYTES)
            val secondPageBytes = secondPage.bytes()

            assertEquals(0.toByte(), secondPageBytes[0])
            assertEquals(0.toByte(), secondPageBytes[TEST_PAGE_BYTES.toInt() - 1])
            secondPageBytes[0] = 0x56.toByte()
            secondPageBytes[TEST_PAGE_BYTES.toInt() - 1] = 0x78.toByte()

            commit(address, TEST_PAGE_BYTES)
            val firstPageBytes = address.bytes()
            assertEquals(0.toByte(), firstPageBytes[0])
            assertEquals(0.toByte(), firstPageBytes[TEST_PAGE_BYTES.toInt() - 1])
            assertEquals(0x56.toByte(), secondPageBytes[0])
            assertEquals(0x78.toByte(), secondPageBytes[TEST_PAGE_BYTES.toInt() - 1])
        } finally {
            release(address, reservationSize)
        }
    }

    @Test
    fun `validates sizes and permits an empty commit`() {
        assertFailsWith<IllegalArgumentException> { reserve(0) }
        assertFailsWith<IllegalArgumentException> { reserve(-1) }
        assertFailsWith<IllegalArgumentException> { commit(nativeNullPtr, 1) }
        assertFailsWith<IllegalArgumentException> { release(nativeNullPtr, 1) }

        val address = reserve(TEST_PAGE_BYTES)
        try {
            commit(address, 0)
            assertFailsWith<IllegalArgumentException> { commit(address, -1) }
            assertFailsWith<IllegalArgumentException> { release(address, 0) }
            assertFailsWith<IllegalArgumentException> { release(address, -1) }
        } finally {
            release(address, TEST_PAGE_BYTES)
        }
    }

    private fun NativePtr.bytes(): CPointer<ByteVar> = requireNotNull(toLong().toCPointer())

    private companion object {
        const val TEST_PAGE_BYTES = 64L * 1024L
    }
}
