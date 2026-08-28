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
}

private class RecordingHostResources : HostResources {
    var memoryModule: HostModuleInstance? = null
    var memoryIndex: ModuleIndex.MemoryIndex? = null
    var pagesToAdd: Int? = null
    var tableModule: HostModuleInstance? = null
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

    override fun table(module: HostModuleInstance, index: ModuleIndex.TableIndex): HostTable = error("unused")

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
