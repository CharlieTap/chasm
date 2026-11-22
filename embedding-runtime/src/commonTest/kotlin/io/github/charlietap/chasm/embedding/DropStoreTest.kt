package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.INSTANCE_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.limits
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.host.HostFunction
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class DropStoreTest {

    @Test
    fun `calling dropStore wipes all the associated state in the store`() = runTest {

        val runtime = store()
        val store = interpreterStore(runtime)
        function(store, functionType(), HostFunction { _, _ -> })
        memory(store, memoryType(limits = limits(min = 1u)))
        instantiate(store, INSTANCE_MODULE)
        val data = runtime.data.toList()
        val elements = runtime.elements.toList()
        val globals = runtime.globals.toList()
        val memories = runtime.memories.toList()
        val tables = runtime.tables.toList()

        dropStore(store)

        data.forEach { instance -> assertContentEquals(ubyteArrayOf(), instance.bytes) }
        elements.forEach { instance -> assertContentEquals(longArrayOf(), instance.elements) }
        globals.forEach { instance -> assertEquals(0L, instance.value) }
        memories.forEach { instance -> assertEquals(0uL, instance.type.limits.min) }
        tables.forEach { instance -> assertContentEquals(longArrayOf(), instance.elements) }
        assertEquals(2, memories.size)
        assertEquals(0, runtime.data.size)
        assertEquals(0, runtime.elements.size)
        assertEquals(0, runtime.functions.size)
        assertEquals(0, runtime.globals.size)
        assertEquals(0, runtime.memories.size)
        assertEquals(0, runtime.tables.size)
    }

    @Test
    fun `a store can only be dropped once`() {

        val store = interpreterStore()

        dropStore(store)

        assertFailsWith<IllegalStateException> {
            dropStore(store)
        }
    }
}
