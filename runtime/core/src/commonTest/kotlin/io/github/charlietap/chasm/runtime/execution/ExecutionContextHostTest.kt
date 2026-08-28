package io.github.charlietap.chasm.runtime.execution

import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.memory.NoOpLinearMemory
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.type.rtt
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.host.HostTag
import io.github.charlietap.chasm.host.ModuleIndex
import io.github.charlietap.chasm.host.UnsafeHostApi
import io.github.charlietap.chasm.host.withGc
import io.github.charlietap.chasm.host.withGlobal
import io.github.charlietap.chasm.host.withMemory
import io.github.charlietap.chasm.host.withTable
import io.github.charlietap.chasm.host.withTag
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ExecutionContextHostTest {

    @Test
    @OptIn(UnsafeHostApi::class)
    fun `resolves and edits the callers existing host resources without wrappers`() {
        val decoyMemory = object : LinearMemory by NoOpLinearMemory {}
        val targetMemory = object : LinearMemory by NoOpLinearMemory {}
        val decoyTable = tableInstance(elements = longArrayOf(1L))
        val targetTable = tableInstance(elements = longArrayOf(2L, 3L))
        val decoyGlobal = globalInstance(value = 4L)
        val targetGlobal = globalInstance(value = 5L)
        val store = store(
            memories = mutableListOf(memoryInstance(data = decoyMemory), memoryInstance(data = targetMemory)),
            tables = mutableListOf(decoyTable, targetTable),
            globals = mutableListOf(decoyGlobal, targetGlobal),
        )
        store.heap.registerTag(rtt(), tagType())
        val targetTagAddress = store.heap.registerTag(rtt(), tagType())
        val caller = moduleInstance(
            memAddresses = mutableListOf(memoryAddress(1)),
            tableAddresses = mutableListOf(tableAddress(1)),
            globalAddresses = mutableListOf(globalAddress(1)),
            tagAddresses = mutableListOf(targetTagAddress),
        )
        val context = executionContext(
            store = store,
            instance = moduleInstance(),
        )

        val memoryIndex = ModuleIndex.MemoryIndex(0)
        val tableIndex = ModuleIndex.TableIndex(0)
        val globalIndex = ModuleIndex.GlobalIndex(0)
        val tagIndex = ModuleIndex.TagIndex(0)
        val memory = context.memory(caller, memoryIndex)
        val table = context.table(caller, tableIndex)
        val global = context.global(caller, globalIndex)

        assertSame(targetMemory, memory)
        assertSame(targetTable, table)
        assertSame(targetGlobal, global)
        assertSame(store.heap, context.references)
        assertSame(store.heap, context.gc)
        assertEquals(HostTag(targetTagAddress.address), context.tag(caller, tagIndex))

        context(caller, context) {
            withGc { assertSame(store.heap, this) }
            withMemory(memoryIndex) { assertSame(targetMemory, this) }
            withTable(tableIndex) { assertSame(targetTable, this) }
            withGlobal(0) { assertSame(targetGlobal, this) }
            withTag(0) { assertEquals(targetTagAddress.address, rawAddress) }
        }

        table.writeRaw(1, 31L)
        global.rawValue = 47L

        assertEquals(31L, targetTable.elements[1])
        assertEquals(47L, targetGlobal.value)
        assertSame(targetTable.elements, table.unsafeBorrowElements())
    }
}
