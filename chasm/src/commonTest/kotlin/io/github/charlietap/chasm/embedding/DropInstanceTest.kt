package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicInstance
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.memory.destruct.LinearMemoryDestructor
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.ast.type.memoryType
import io.github.charlietap.chasm.fixture.ast.type.sharedStatus
import io.github.charlietap.chasm.fixture.ast.type.unsharedStatus
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
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class DropInstanceTest {

    @Test
    fun `calling dropInstance on an instance wipes all the associated state`() {

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
        val memoryInstance = memoryInstance(
            type = memoryType(
                shared = unsharedStatus(),
            ),
        )
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
        val moduleInstance = moduleInstance(
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
        )
        val instance = publicInstance(
            moduleInstance = moduleInstance,
        )

        var memoryDeallocated = false
        val destructor: LinearMemoryDestructor = { _memoryInstance ->
            assertEquals(memoryInstance.data, _memoryInstance)
            memoryDeallocated = true
        }

        val expected = ChasmResult.Success(Unit)
        val actual = dropInstance(store, instance, destructor)

        assertEquals(expected, actual)
        assertEquals(true, moduleInstance.deallocated)
        assertEquals(true, memoryDeallocated)
        assertContentEquals(ubyteArrayOf(), dataInstance.bytes)
        assertContentEquals(arrayOf(), elementInstance.elements)
        assertEquals(ExecutionValue.Uninitialised, globalInstance.value)
        assertContentEquals(arrayOf(), tableInstance.elements)
        assertEquals(0, moduleInstance.dataAddresses.size)
        assertEquals(0, moduleInstance.elemAddresses.size)
        assertEquals(0, moduleInstance.exports.size)
        assertEquals(0, moduleInstance.globalAddresses.size)
        assertEquals(0, moduleInstance.functionAddresses.size)
        assertEquals(0, moduleInstance.memAddresses.size)
        assertEquals(0, moduleInstance.tableAddresses.size)
        assertEquals(0, moduleInstance.tagAddresses.size)
    }

    @Test
    fun `shared memories are not deallocated during deinstantiation`() {

        val memoryInstance = memoryInstance(
            type = memoryType(
                shared = sharedStatus(),
            ),
        )
        val store = publicStore(
            store(
                memories = mutableListOf(memoryInstance),
            ),
        )
        val instance = publicInstance(
            moduleInstance(
                memAddresses = mutableListOf(
                    memoryAddress(0),
                ),
            ),
        )

        var memoryDeallocated = false
        val destructor: LinearMemoryDestructor = { _memoryInstance ->
            assertEquals(memoryInstance.data, _memoryInstance)
            memoryDeallocated = true
        }

        val expected = ChasmResult.Success(Unit)
        val actual = dropInstance(store, instance, destructor)

        assertEquals(expected, actual)
        assertEquals(true, instance.instance.deallocated)
        assertEquals(false, memoryDeallocated)
    }
}
