package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.ast.type.memoryType
import io.github.charlietap.chasm.fixture.ast.type.sharedStatus
import io.github.charlietap.chasm.fixture.executor.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.exceptionInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tagInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.hostReferenceValue
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class DropStoreTest {

    @Test
    fun `calling dropStore wipes all the associated state in the store`() {

        val referenceValue = hostReferenceValue()

        val dataInstance = dataInstance(
            bytes = ubyteArrayOf(1u, 2u),
        )
        val exceptionInstance = exceptionInstance(
            fields = listOf(referenceValue),
        )
        val elementInstance = elementInstance(
            elements = arrayOf(referenceValue, referenceValue),
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
            elements = arrayOf(referenceValue, referenceValue),
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

        var memoryDeallocated = false
        val destructor: LinearMemoryDestructor = { _memoryInstance ->
            assertEquals(memoryInstance.data, _memoryInstance)
            memoryDeallocated = true
        }

        val expected = ChasmResult.Success(Unit)
        val actual = dropStore(store, destructor)

        assertEquals(expected, actual)
        assertEquals(true, memoryDeallocated)
        assertContentEquals(ubyteArrayOf(), dataInstance.bytes)
        assertEquals(emptyList<ExecutionValue>(), exceptionInstance.fields)
        assertContentEquals(arrayOf(), elementInstance.elements)
        assertEquals(ExecutionValue.Uninitialised, globalInstance.value)
        assertContentEquals(arrayOf(), tableInstance.elements)
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
