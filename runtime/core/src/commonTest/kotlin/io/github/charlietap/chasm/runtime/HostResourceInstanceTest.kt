package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.host.HostGlobal
import io.github.charlietap.chasm.host.HostTable
import io.github.charlietap.chasm.host.UnsafeHostApi
import kotlin.test.Test
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
}
