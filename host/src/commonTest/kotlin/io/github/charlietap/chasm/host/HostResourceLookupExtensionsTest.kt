package io.github.charlietap.chasm.host

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class HostResourceLookupExtensionsTest {

    @Test
    fun `typed lookups resolve against the current module`() {
        val module = object : HostModuleInstance {}
        val resources = RecordingLookupHostResources()

        context(module) {
            assertFailsWith<LookupComplete> { resources.memory(ModuleIndex.MemoryIndex(3)) }
            assertFailsWith<LookupComplete> { resources.table(ModuleIndex.TableIndex(5)) }
            assertFailsWith<LookupComplete> { resources.global(ModuleIndex.GlobalIndex(7)) }
            assertFailsWith<LookupComplete> { resources.tag(ModuleIndex.TagIndex(11)) }
            Unit
        }

        resources.lookups.forEach { assertSame(module, it.module) }
        assertEquals(
            listOf(
                ModuleIndex.MemoryIndex(3),
                ModuleIndex.TableIndex(5),
                ModuleIndex.GlobalIndex(7),
                ModuleIndex.TagIndex(11),
            ),
            resources.lookups.map(Lookup::index),
        )
    }

    @Test
    fun `integer lookups delegate through typed indices`() {
        val module = object : HostModuleInstance {}
        val resources = RecordingLookupHostResources()

        context(module) {
            assertFailsWith<LookupComplete> { resources.memory(13) }
            assertFailsWith<LookupComplete> { resources.table(17) }
            assertFailsWith<LookupComplete> { resources.global(19) }
            assertFailsWith<LookupComplete> { resources.tag(23) }
            Unit
        }

        assertEquals(
            listOf(
                ModuleIndex.MemoryIndex(13),
                ModuleIndex.TableIndex(17),
                ModuleIndex.GlobalIndex(19),
                ModuleIndex.TagIndex(23),
            ),
            resources.lookups.map(Lookup::index),
        )
    }

    @Test
    fun `explicit module lookup remains available`() {
        val currentModule = object : HostModuleInstance {}
        val selectedModule = object : HostModuleInstance {}
        val resources = RecordingLookupHostResources()

        context(currentModule) {
            assertFailsWith<LookupComplete> {
                resources.memory(selectedModule, ModuleIndex.MemoryIndex(29))
            }
            Unit
        }

        assertSame(selectedModule, resources.lookups.single().module)
        assertEquals(ModuleIndex.MemoryIndex(29), resources.lookups.single().index)
    }
}

private class RecordingLookupHostResources : HostResources {
    val lookups = mutableListOf<Lookup>()

    override val references: HostReferences
        get() = error("unused")

    override val gc: HostGc
        get() = error("unused")

    override val externs: HostExterns
        get() = error("unused")

    override val exceptions: HostExceptions
        get() = error("unused")

    override fun memory(module: HostModuleInstance, index: ModuleIndex.MemoryIndex): HostMemory =
        record(module, index)

    override fun growMemory(module: HostModuleInstance, index: ModuleIndex.MemoryIndex, pagesToAdd: Int): Int =
        error("unused")

    override fun table(module: HostModuleInstance, index: ModuleIndex.TableIndex): HostTable = record(module, index)

    override fun growTable(
        module: HostModuleInstance,
        index: ModuleIndex.TableIndex,
        elementsToAdd: Int,
        value: HostReference,
    ): Int = error("unused")

    override fun global(module: HostModuleInstance, index: ModuleIndex.GlobalIndex): HostGlobal = record(module, index)

    override fun tag(module: HostModuleInstance, index: ModuleIndex.TagIndex): HostTag = record(module, index)

    private fun record(module: HostModuleInstance, index: ModuleIndex): Nothing {
        lookups += Lookup(module, index)
        throw LookupComplete()
    }
}

private data class Lookup(
    val module: HostModuleInstance,
    val index: ModuleIndex,
)

private class LookupComplete : RuntimeException()
