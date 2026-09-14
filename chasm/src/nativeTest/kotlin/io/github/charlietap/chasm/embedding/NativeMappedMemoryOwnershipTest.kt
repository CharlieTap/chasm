@file:OptIn(
    kotlin.experimental.ExperimentalNativeApi::class,
    kotlin.native.runtime.NativeRuntimeApi::class,
)

package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicInstance
import io.github.charlietap.chasm.embedding.fixture.publicMemory
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.memory.factory.LinearMemoryFactory
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import platform.posix.usleep
import kotlin.native.ref.WeakReference
import kotlin.native.runtime.GC
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import io.github.charlietap.chasm.runtime.store.Store as InternalStore

class NativeMappedMemoryOwnershipTest {

    @Test
    fun `dropStore releases every mapped memory before returning`() {
        val first = mappedMemory()
        val second = mappedMemory()
        val store = publicStore(
            store(
                memories = mutableListOf(
                    memoryInstance(type = onePageMemoryType(), data = first),
                    memoryInstance(type = onePageMemoryType(), data = second),
                ),
            ),
        )

        assertEquals(ChasmResult.Success(Unit), dropStore(store))
        assertEquals(0, first.byteSize)
        assertEquals(0, second.byteSize)
    }

    @Test
    fun `dropInstance leaves imported mapped memory alive in its store`() {
        val backing = mappedMemory()
        val internalStore = store(
            memories = mutableListOf(
                memoryInstance(type = onePageMemoryType(), data = backing),
            ),
        )
        val store = publicStore(internalStore)
        val importingInstance = publicInstance(
            store = internalStore,
            moduleInstance = moduleInstance(memAddresses = mutableListOf(memoryAddress(0))),
        )

        assertEquals(ChasmResult.Success(Unit), dropInstance(store, importingInstance))
        assertEquals(LinearMemory.PAGE_SIZE, backing.byteSize)
        backing.writeI32(7, 0x12345678)
        assertEquals(0x12345678, backing.readI32(7))

        assertEquals(ChasmResult.Success(Unit), dropStore(store))
    }

    @Test
    fun `memory handle retains its internal store and mapped backing`() {
        val graph = memoryHandleGraph()

        awaitCollected(graph.publicStore)

        assertNull(graph.publicStore.value)
        val internalStore = assertNotNull(graph.internalStore.value)
        val backing = assertNotNull(graph.backing.value)
        backing.writeI32(3, 42)
        assertEquals(42, backing.readI32(3))
        assertEquals(memoryExternalValue(memoryAddress(0)), graph.handle.reference)

        assertEquals(ChasmResult.Success(Unit), dropStore(publicStore(internalStore)))
    }

    @Test
    fun `instance handle retains its internal store and mapped backing`() {
        val graph = instanceHandleGraph()

        awaitCollected(graph.publicStore)

        assertNull(graph.publicStore.value)
        val internalStore = assertNotNull(graph.internalStore.value)
        val backing = assertNotNull(graph.backing.value)
        backing.writeI32(3, 42)
        assertEquals(42, backing.readI32(3))
        assertEquals(1, graph.handle.instance.memAddresses.size)

        assertEquals(ChasmResult.Success(Unit), dropStore(publicStore(internalStore)))
    }

    @Test
    fun `dropping the final memory handle makes the store graph unreachable`() {
        val graph = forgottenMemoryHandleGraph()

        awaitCollected(graph.internalStore)
        awaitCollected(graph.backing)

        assertNull(graph.internalStore.value)
        assertNull(graph.backing.value)
    }

    private fun mappedMemory(): LinearMemory {
        val pages = LinearMemory.Pages(1u)
        return LinearMemoryFactory(pages, pages)
    }

    private fun onePageMemoryType() = memoryType(
        limits = limits(min = 1u, max = 1u),
    )

    private fun memoryHandleGraph(): MemoryHandleGraph {
        val backing = mappedMemory()
        val internalStore = store(
            memories = mutableListOf(
                memoryInstance(type = onePageMemoryType(), data = backing),
            ),
        )
        val publicStore = publicStore(internalStore)
        val handle = publicMemory(
            reference = memoryExternalValue(memoryAddress(0)),
            store = internalStore,
        )
        return MemoryHandleGraph(
            publicStore = WeakReference(publicStore),
            internalStore = WeakReference(internalStore),
            backing = WeakReference(backing),
            handle = handle,
        )
    }

    private fun instanceHandleGraph(): InstanceHandleGraph {
        val backing = mappedMemory()
        val internalStore = store(
            memories = mutableListOf(
                memoryInstance(type = onePageMemoryType(), data = backing),
            ),
        )
        val publicStore = publicStore(internalStore)
        val handle = publicInstance(
            store = internalStore,
            moduleInstance = moduleInstance(memAddresses = mutableListOf(memoryAddress(0))),
        )
        return InstanceHandleGraph(
            publicStore = WeakReference(publicStore),
            internalStore = WeakReference(internalStore),
            backing = WeakReference(backing),
            handle = handle,
        )
    }

    private fun forgottenMemoryHandleGraph(): ForgottenGraph {
        val graph = memoryHandleGraph()
        return ForgottenGraph(graph.internalStore, graph.backing)
    }

    private fun <T : Any> awaitCollected(reference: WeakReference<T>) {
        repeat(100) {
            GC.collect()
            usleep(10_000u)
            if (isCollected(reference)) return
        }
    }

    private fun <T : Any> isCollected(reference: WeakReference<T>): Boolean = reference.value == null

    private class MemoryHandleGraph(
        val publicStore: WeakReference<io.github.charlietap.chasm.embedding.shapes.Store>,
        val internalStore: WeakReference<InternalStore>,
        val backing: WeakReference<LinearMemory>,
        val handle: Memory,
    )

    private class InstanceHandleGraph(
        val publicStore: WeakReference<io.github.charlietap.chasm.embedding.shapes.Store>,
        val internalStore: WeakReference<InternalStore>,
        val backing: WeakReference<LinearMemory>,
        val handle: Instance,
    )

    private class ForgottenGraph(
        val internalStore: WeakReference<InternalStore>,
        val backing: WeakReference<LinearMemory>,
    )
}
