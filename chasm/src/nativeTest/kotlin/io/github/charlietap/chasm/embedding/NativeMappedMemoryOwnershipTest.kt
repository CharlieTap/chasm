@file:OptIn(
    kotlin.experimental.ExperimentalNativeApi::class,
    kotlin.native.runtime.NativeRuntimeApi::class,
)

package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.engine.Engine
import io.github.charlietap.chasm.embedding.fixture.INSTANCE_MODULE
import io.github.charlietap.chasm.embedding.fixture.MEMORY_IMPORT_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.internal.InterpreterEngine
import io.github.charlietap.chasm.embedding.memory.readInt
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Memory
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes._engine
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.runtime.memory.LinearMemory
import io.github.charlietap.chasm.type.MemoryType
import kotlinx.coroutines.test.runTest
import platform.posix.usleep
import kotlin.native.ref.WeakReference
import kotlin.native.runtime.GC
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

class NativeMappedMemoryOwnershipTest {

    @Test
    fun `dropStore releases every mapped memory before returning`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        memory(store, onePageMemoryType())
        memory(store, onePageMemoryType())
        val first = runtime.memories[0].data
        val second = runtime.memories[1].data

        dropStore(store)

        assertEquals(0, first.byteSize)
        assertEquals(0, second.byteSize)
    }

    @Test
    fun `dropInstance leaves imported mapped memory alive in its store`() = runTest {

        val runtime = store()
        val store = interpreterStore(runtime)
        val memory = memory(store, onePageMemoryType())
        val backing = runtime.memories.single().data
        val importingInstance = instantiate(store, MEMORY_IMPORT_MODULE, listOf(Import("env", "memory", memory)))

        dropInstance(importingInstance)

        assertEquals(LinearMemory.PAGE_SIZE, backing.byteSize)
        backing.writeI32(7, 0x12345678)
        assertEquals(0x12345678, backing.readI32(7))

        dropStore(store)
    }

    @Test
    fun `memory handle retains its store and mapped backing`() {

        val graph = memoryHandleGraph()

        awaitCollected(graph.unrelated)

        assertNotNull(graph.runtime.value)
        val backing = assertNotNull(graph.backing.value)
        backing.writeI32(3, 42)
        assertEquals(42, backing.readI32(3))
        assertEquals(42, readInt(graph.handle, 3))

        dropStore(storeOf(graph.handle._engine()))
    }

    @Test
    fun `instance handle retains its store and mapped backing`() = runTest {

        val graph = instanceHandleGraph()

        awaitCollected(graph.unrelated)

        assertNotNull(graph.runtime.value)
        val backing = assertNotNull(graph.backing.value)
        backing.writeI32(3, 42)
        assertEquals(42, backing.readI32(3))
        assertEquals(42, readInt(assertNotNull(exports(graph.handle).memory("memory")), 3))

        dropStore(storeOf(graph.handle._engine()))
    }

    @Test
    fun `dropping the final memory handle makes the store graph unreachable`() {

        val graph = forgottenMemoryHandleGraph()

        awaitCollected(graph.runtime)
        awaitCollected(graph.backing)

        assertNull(graph.runtime.value)
        assertNull(graph.backing.value)
    }

    private fun onePageMemoryType(): MemoryType {
        return memoryType(limits = limits(min = 1u, max = 1u))
    }

    private fun memoryHandleGraph(): MemoryHandleGraph {

        val runtime = store()
        val handle = memory(interpreterStore(runtime), onePageMemoryType())

        return MemoryHandleGraph(
            runtime = WeakReference(runtime),
            backing = WeakReference(runtime.memories.single().data),
            unrelated = WeakReference(interpreterStore()),
            handle = handle,
        )
    }

    private suspend fun instanceHandleGraph(): InstanceHandleGraph {

        val runtime = store()
        val handle = instantiate(interpreterStore(runtime), INSTANCE_MODULE)

        return InstanceHandleGraph(
            runtime = WeakReference(runtime),
            backing = WeakReference(runtime.memories.single().data),
            unrelated = WeakReference(interpreterStore()),
            handle = handle,
        )
    }

    private fun forgottenMemoryHandleGraph(): ForgottenGraph {

        val graph = memoryHandleGraph()

        return ForgottenGraph(graph.runtime, graph.backing)
    }

    private fun storeOf(engine: Engine): Store {
        return (engine as InterpreterEngine).store
    }

    private fun <T : Any> awaitCollected(reference: WeakReference<T>) {
        repeat(100) {
            GC.collect()
            usleep(10_000u)
            if (reference.value == null) {
                return
            }
        }
    }

    private class MemoryHandleGraph(
        val runtime: WeakReference<RuntimeStore>,
        val backing: WeakReference<LinearMemory>,
        val unrelated: WeakReference<Store>,
        val handle: Memory,
    )

    private class InstanceHandleGraph(
        val runtime: WeakReference<RuntimeStore>,
        val backing: WeakReference<LinearMemory>,
        val unrelated: WeakReference<Store>,
        val handle: Instance,
    )

    private class ForgottenGraph(
        val runtime: WeakReference<RuntimeStore>,
        val backing: WeakReference<LinearMemory>,
    )
}
