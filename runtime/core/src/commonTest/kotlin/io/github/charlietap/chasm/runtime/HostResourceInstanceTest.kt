package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.host.HostGlobal
import io.github.charlietap.chasm.host.HostTable
import io.github.charlietap.chasm.host.UnsafeHostApi
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame

class HostResourceInstanceTest {

    @Test
    fun `host global reads and writes the runtime value`() {
        val instance = globalInstance(value = 42L)
        val global: HostGlobal = instance

        assertEquals(42L, global.rawValue)

        global.rawValue = 84L

        assertEquals(84L, instance.value)
    }

    @OptIn(UnsafeHostApi::class)
    @Test
    fun `host table operates on the runtime elements`() {
        val elements = longArrayOf(11L, 22L)
        val instance = tableInstance(elements = elements)
        val table: HostTable = instance

        assertEquals(2, table.size)
        assertEquals(22L, table.readRaw(1))

        table.writeRaw(0, 33L)

        assertEquals(33L, elements[0])
        assertSame(elements, table.unsafeBorrowElements())
    }

    @Test
    fun `host table bulk reads and writes caller buffers`() {
        val instance = tableInstance(elements = longArrayOf(11L, 22L, 33L, 44L))
        val table: HostTable = instance
        val readBuffer = longArrayOf(-1L, -1L, -1L, -1L, -1L)

        val returned = table.read(
            buffer = readBuffer,
            elementIndex = 1,
            elementsToRead = 2,
            bufferIndex = 2,
        )

        assertSame(readBuffer, returned)
        assertContentEquals(longArrayOf(-1L, -1L, 22L, 33L, -1L), readBuffer)

        table.write(
            elementIndex = 0,
            buffer = longArrayOf(55L, 66L, 77L),
            bufferIndex = 1,
        )

        assertContentEquals(longArrayOf(66L, 77L, 33L, 44L), instance.elements)
    }

    @Test
    fun `host table fills and copies raw references`() {
        val source = tableInstance(elements = longArrayOf(11L, 22L, 33L, 44L))
        val destination = tableInstance(elements = longArrayOf(0L, 0L, 0L, 0L, 0L))

        destination.fill(elementIndex = 1, value = 7L, elementsToFill = 3)
        destination.copy(
            sourceElementIndex = 1,
            destinationElementIndex = 2,
            elementsToCopy = 2,
            source = source,
        )

        assertContentEquals(longArrayOf(0L, 7L, 22L, 33L, 0L), destination.elements)
    }

    @Test
    fun `host table moves overlapping raw references`() {
        val instance = tableInstance(elements = longArrayOf(11L, 22L, 33L, 44L, 55L))

        instance.move(sourceElementIndex = 0, destinationElementIndex = 1, elementsToMove = 4)
        assertContentEquals(longArrayOf(11L, 11L, 22L, 33L, 44L), instance.elements)

        instance.move(sourceElementIndex = 1, destinationElementIndex = 0, elementsToMove = 4)
        assertContentEquals(longArrayOf(11L, 22L, 33L, 44L, 44L), instance.elements)
    }
}
