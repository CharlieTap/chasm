package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicInstance
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.fixture.ast.type.noneHeapType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.dataAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.elementAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.hostReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.nullReferenceValue
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class DeinstanceTest {

    @Test
    fun `calling deinstance on an instance wipes all the associated state`() {

        val referenceValue = hostReferenceValue()

        val dataInstance = dataInstance(
            bytes = ubyteArrayOf(1u, 2u),
        )
        val elementInstance = elementInstance(
            elements = arrayOf(referenceValue, referenceValue),
        )
        val globalInstance = globalInstance(
            value = referenceValue,
        )
        val memoryInstance = memoryInstance()
        val tableInstance = tableInstance(
            elements = arrayOf(referenceValue, referenceValue),
        )

        val store = publicStore(
            store(
                data = mutableListOf(dataInstance),
                elements = mutableListOf(elementInstance),
                globals = mutableListOf(globalInstance),
                memories = mutableListOf(memoryInstance),
                tables = mutableListOf(tableInstance),
            ),
        )
        val instance = publicInstance(
            moduleInstance(
                dataAddresses = mutableListOf(
                    dataAddress(0),
                ),
                elemAddresses = mutableListOf(
                    elementAddress(0),
                ),
                globalAddresses = mutableListOf(
                    globalAddress(0),
                ),
                memAddresses = mutableListOf(
                    memoryAddress(0),
                ),
                tableAddresses = mutableListOf(
                    tableAddress(0),
                ),
            ),
        )

        val destructor: LinearMemoryDestructor = { _memoryInstance ->
            assertEquals(memoryInstance.data, _memoryInstance)
        }

        val expected = ChasmResult.Success(Unit)
        val actual = deinstance(store, instance, destructor)

        val nullReference = nullReferenceValue(noneHeapType())
        assertEquals(expected, actual)
        assertEquals(true, instance.instance.deallocated)
        assertContentEquals(ubyteArrayOf(0u, 0u), dataInstance.bytes)
        assertContentEquals(arrayOf(nullReference, nullReference), elementInstance.elements)
        assertEquals(nullReference, globalInstance.value)
        assertContentEquals(arrayOf(nullReference, nullReference), tableInstance.elements)
    }
}
