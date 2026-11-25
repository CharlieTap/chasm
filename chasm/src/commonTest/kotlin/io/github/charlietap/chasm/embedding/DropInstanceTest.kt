package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.INSTANCE_MODULE
import io.github.charlietap.chasm.embedding.fixture.MEMORY_IMPORT_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.internal.InterpreterInstance
import io.github.charlietap.chasm.embedding.memory.readInt
import io.github.charlietap.chasm.embedding.memory.writeInt
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes._payload
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class DropInstanceTest {

    @Test
    fun `calling dropInstance on an instance wipes all the associated state`() = runTest {

        val runtime = store()
        val store = interpreterStore(runtime)
        val instance = instantiate(store, INSTANCE_MODULE)
        val module = moduleInstance(instance)
        val memory = runtime.memories.single()
        assertContentEquals(ubyteArrayOf(1u, 2u), runtime.data.single().bytes)
        assertEquals(7L, runtime.globals.single().value)
        assertEquals(2, runtime.tables.single().elements.size)

        dropInstance(instance)

        assertTrue(module.deallocated)
        assertEquals(memory, runtime.memories.single())
        assertContentEquals(ubyteArrayOf(), runtime.data.single().bytes)
        runtime.elements.forEach { element -> assertContentEquals(longArrayOf(), element.elements) }
        assertEquals(0L, runtime.globals.single().value)
        assertContentEquals(longArrayOf(), runtime.tables.single().elements)
        assertEquals(0, module.dataAddresses.size)
        assertEquals(0, module.elemAddresses.size)
        assertEquals(0, module.exports.size)
        assertEquals(0, module.globalAddresses.size)
        assertEquals(0, module.functionAddresses.size)
        assertEquals(0, module.memAddresses.size)
        assertEquals(0, module.tableAddresses.size)
        assertEquals(0, module.tagAddresses.size)
    }

    @Test
    fun `instance drop leaves store owned memory allocated`() = runTest {

        val runtime = store()
        val store = interpreterStore(runtime)
        val memory = memory(store, memoryType(limits = limits(min = 1u, max = 1u)))
        val instance = instantiate(store, MEMORY_IMPORT_MODULE, listOf(Import("env", "memory", memory)))
        writeInt(memory, 7, 117)

        dropInstance(instance)

        assertTrue(moduleInstance(instance).deallocated)
        assertEquals(1, runtime.memories.size)
        assertEquals(117, readInt(memory, 7))
    }

    @Test
    fun `an instance can only be dropped once`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        dropInstance(instance)

        assertFailsWith<IllegalStateException> {
            dropInstance(instance)
        }
    }

    private fun moduleInstance(instance: Instance): ModuleInstance {
        return (instance._payload() as InterpreterInstance).module
    }
}
