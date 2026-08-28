package io.github.charlietap.chasm.host

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class HostGrowthExtensionsTest {

    @Test
    fun `typed growth functions delegate to host resources`() {
        val module = object : HostModuleInstance {}
        val resources = RecordingHostResources()
        val memoryIndex = ModuleIndex.MemoryIndex(3)
        val tableIndex = ModuleIndex.TableIndex(5)

        context(module, resources) {
            assertEquals(7, growMemory(memoryIndex, 11))
            assertEquals(13, growTable(tableIndex, 17, 19L))
        }

        assertSame(module, resources.memoryModule)
        assertEquals(memoryIndex, resources.memoryIndex)
        assertEquals(11, resources.pagesToAdd)
        assertSame(module, resources.tableModule)
        assertEquals(tableIndex, resources.tableIndex)
        assertEquals(17, resources.elementsToAdd)
        assertEquals(19L, resources.tableValue)
    }

    @Test
    fun `integer growth functions delegate through typed indices`() {
        val module = object : HostModuleInstance {}
        val resources = RecordingHostResources()

        context(module, resources) {
            assertEquals(7, growMemory(23, 29))
            assertEquals(13, growTable(31, 37, 41L))
        }

        assertEquals(ModuleIndex.MemoryIndex(23), resources.memoryIndex)
        assertEquals(29, resources.pagesToAdd)
        assertEquals(ModuleIndex.TableIndex(31), resources.tableIndex)
        assertEquals(37, resources.elementsToAdd)
        assertEquals(41L, resources.tableValue)
    }

    @Test
    fun `table receiver growth uses the index provided by withTable`() {
        val module = object : HostModuleInstance {}
        val resources = RecordingHostResources()
        val tableIndex = ModuleIndex.TableIndex(43)

        context(module, resources) {
            withTable(tableIndex) {
                assertSame(resources.table, this)
                assertEquals(13, grow(47, 53L))
            }
        }

        assertSame(module, resources.tableModule)
        assertEquals(tableIndex, resources.tableLookupIndex)
        assertEquals(tableIndex, resources.tableIndex)
        assertEquals(47, resources.elementsToAdd)
        assertEquals(53L, resources.tableValue)
    }
}

private class RecordingHostResources : HostResources {
    val table = RecordingHostTable()
    var memoryModule: HostModuleInstance? = null
    var memoryIndex: ModuleIndex.MemoryIndex? = null
    var pagesToAdd: Int? = null
    var tableModule: HostModuleInstance? = null
    var tableLookupIndex: ModuleIndex.TableIndex? = null
    var tableIndex: ModuleIndex.TableIndex? = null
    var elementsToAdd: Int? = null
    var tableValue: HostReference? = null

    override val references: HostReferences
        get() = error("unused")

    override val gc: HostGc
        get() = error("unused")

    override val externs: HostExterns
        get() = error("unused")

    override val exceptions: HostExceptions
        get() = error("unused")

    override fun memory(module: HostModuleInstance, index: ModuleIndex.MemoryIndex): HostMemory = error("unused")

    override fun growMemory(module: HostModuleInstance, index: ModuleIndex.MemoryIndex, pagesToAdd: Int): Int {
        memoryModule = module
        memoryIndex = index
        this.pagesToAdd = pagesToAdd
        return 7
    }

    override fun table(module: HostModuleInstance, index: ModuleIndex.TableIndex): HostTable {
        tableLookupIndex = index
        return table
    }

    override fun growTable(
        module: HostModuleInstance,
        index: ModuleIndex.TableIndex,
        elementsToAdd: Int,
        value: HostReference,
    ): Int {
        tableModule = module
        tableIndex = index
        this.elementsToAdd = elementsToAdd
        tableValue = value
        return 13
    }

    override fun global(module: HostModuleInstance, index: ModuleIndex.GlobalIndex): HostGlobal = error("unused")

    override fun tag(module: HostModuleInstance, index: ModuleIndex.TagIndex): HostTag = error("unused")
}

@OptIn(UnsafeHostApi::class)
private class RecordingHostTable : HostTable {
    override val size: Int
        get() = error("unused")

    override fun readRaw(index: Int): Long = error("unused")

    override fun writeRaw(index: Int, value: Long) = error("unused")

    override fun read(buffer: LongArray, elementIndex: Int, elementsToRead: Int, bufferIndex: Int): LongArray =
        error("unused")

    override fun write(elementIndex: Int, buffer: LongArray, bufferIndex: Int, elementsToWrite: Int) = error("unused")

    override fun fill(elementIndex: Int, value: HostReference, elementsToFill: Int) = error("unused")

    override fun copy(
        sourceElementIndex: Int,
        destinationElementIndex: Int,
        elementsToCopy: Int,
        source: HostTable,
    ) = error("unused")

    override fun move(
        sourceElementIndex: Int,
        destinationElementIndex: Int,
        elementsToMove: Int,
        source: HostTable,
    ) = error("unused")

    override fun unsafeBorrowElements(): LongArray = error("unused")
}
