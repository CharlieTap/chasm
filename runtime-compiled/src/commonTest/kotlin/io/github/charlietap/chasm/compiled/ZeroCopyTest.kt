@file:OptIn(UnsafeHostApi::class)

package io.github.charlietap.chasm.compiled

import io.github.charlietap.chasm.compiled.code.CompiledFixture
import io.github.charlietap.chasm.compiled.code.fixture
import io.github.charlietap.chasm.embedding.compiled.unsafeAllocateArray
import io.github.charlietap.chasm.embedding.compiled.unsafeBorrowBytes
import io.github.charlietap.chasm.embedding.compiled.unsafeBorrowElements
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.reference.runtimeType
import io.github.charlietap.chasm.embedding.value.NumberValue.I32
import io.github.charlietap.chasm.embedding.value.ReferenceValue
import io.github.charlietap.chasm.host.UnsafeHostApi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class ZeroCopyTest {

    private val fixture = CompiledFixture(fixture("zero_copy"))

    @Test
    fun `unsafeBorrowBytes returns the array the guest writes through`() {

        val bytes = assertIs<ReferenceValue.Array>(fixture.call("make_bytes", I32(4)).single())
        val borrowed = unsafeBorrowBytes(bytes.reference)

        fixture.call("set_byte", bytes, I32(1), I32(7))
        borrowed[2] = 9

        assertEquals(7, borrowed[1].toInt())
        assertEquals(listOf(I32(9)), fixture.call("get_byte", bytes, I32(2)))
    }

    @Test
    fun `unsafeAllocateArray adopts the host's array without copying`() {

        val storage = byteArrayOf(3, 5, 8)

        val array = unsafeAllocateArray(fixture.store, runtimeType(fixture.instance, BYTES_TYPE), storage)
        storage[1] = 13

        assertSame(storage, unsafeBorrowBytes(array))
        assertEquals(listOf(I32(13)), fixture.call("get_byte", ReferenceValue.Array(array), I32(1)))
    }

    @Test
    fun `unsafeBorrowElements returns the table's own array`() {

        val value = Any()
        val table = assertNotNull(exports(fixture.instance).table("items"))
        val elements = unsafeBorrowElements(table)

        fixture.call("set_item", I32(1), ReferenceValue.Extern(value))

        assertSame(value, elements[1])
    }

    private companion object {
        const val BYTES_TYPE = 0
    }
}
