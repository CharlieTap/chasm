package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.invoker.drop.MemoryInstanceDropper
import io.github.charlietap.chasm.fixture.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.runtime.instance.exceptionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.functionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tagInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.hostReferenceValue
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.sharedStatus
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class DropStoreTest {

    @Test
    fun `calling dropStore wipes all the associated state in the store`() {

        val referenceValue = hostReferenceValue().toLong()

        val dataInstance = dataInstance(
            bytes = ubyteArrayOf(1u, 2u),
        )
        val exceptionInstance = exceptionInstance(
            fields = longArrayOf(referenceValue),
        )
        val elementInstance = elementInstance(
            elements = longArrayOf(referenceValue, referenceValue),
        )
        val globalInstance = globalInstance(
            value = referenceValue,
        )
        val memoryInstance = memoryInstance(
            type = memoryType(
                shared = sharedStatus(),
            ),
        )
        val tableInstance = tableInstance(
            elements = longArrayOf(referenceValue, referenceValue),
        )

        val store = publicStore(
            store(
                data = mutableListOf(dataInstance),
                exceptions = mutableListOf(exceptionInstance),
                elements = mutableListOf(elementInstance),
                globals = mutableListOf(globalInstance),
                memories = mutableListOf(memoryInstance),
                tables = mutableListOf(tableInstance),
                functions = mutableListOf(functionInstance()),
                tags = mutableListOf(tagInstance()),
            ),
        )

        var memoryDropped = false
        val memoryDropper: MemoryInstanceDropper = { _memoryInstance ->
            assertEquals(memoryInstance, _memoryInstance)
            memoryDropped = true
        }

        val expected = ChasmResult.Success(Unit)
        val actual = dropStore(store, memoryDropper)

        assertEquals(expected, actual)
        assertEquals(true, memoryDropped)
        assertContentEquals(ubyteArrayOf(), dataInstance.bytes)
        assertContentEquals(longArrayOf(), exceptionInstance.fields)
        assertContentEquals(longArrayOf(), elementInstance.elements)
        assertEquals(ExecutionValue.Uninitialised.toLongFromBoxed(), globalInstance.value)
        assertContentEquals(longArrayOf(), tableInstance.elements)
        assertEquals(0, store.store.data.size)
        assertEquals(0, store.store.exceptions.size)
        assertEquals(0, store.store.elements.size)
        assertEquals(0, store.store.functions.size)
        assertEquals(0, store.store.globals.size)
        assertEquals(0, store.store.memories.size)
        assertEquals(0, store.store.tables.size)
        assertEquals(0, store.store.tags.size)
    }
}
