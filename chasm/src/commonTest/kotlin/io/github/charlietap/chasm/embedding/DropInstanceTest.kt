package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicInstance
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.invoker.drop.MemoryInstanceDropper
import io.github.charlietap.chasm.fixture.runtime.instance.dataAddress
import io.github.charlietap.chasm.fixture.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.runtime.instance.elementAddress
import io.github.charlietap.chasm.fixture.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.hostReferenceValue
import io.github.charlietap.chasm.fixture.type.memoryType
import io.github.charlietap.chasm.fixture.type.sharedStatus
import io.github.charlietap.chasm.fixture.type.unsharedStatus
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class DropInstanceTest {

    @Test
    fun `calling dropInstance on an instance wipes all the associated state`() {

        val referenceValue = hostReferenceValue().toLong()

        val dataInstance = dataInstance(
            bytes = ubyteArrayOf(1u, 2u),
        )
        val elementInstance = elementInstance(
            elements = longArrayOf(referenceValue, referenceValue),
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
            elements = longArrayOf(referenceValue, referenceValue),
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
        val memoryDropper: MemoryInstanceDropper = { _memoryInstance ->
            assertEquals(memoryInstance, _memoryInstance)
            memoryDeallocated = true
        }

        val expected = ChasmResult.Success(Unit)
        val actual = dropInstance(store, instance, memoryDropper)

        assertEquals(expected, actual)
        assertEquals(true, moduleInstance.deallocated)
        assertEquals(true, memoryDeallocated)
        assertContentEquals(ubyteArrayOf(), dataInstance.bytes)
        assertContentEquals(longArrayOf(), elementInstance.elements)
        assertEquals(ExecutionValue.Uninitialised.toLongFromBoxed(), globalInstance.value)
        assertContentEquals(longArrayOf(), tableInstance.elements)
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
            moduleInstance = moduleInstance(
                memAddresses = mutableListOf(
                    memoryAddress(0),
                ),
            ),
        )

        var memoryDropped = false
        val memoryDropper: MemoryInstanceDropper = { _memoryInstance ->
            assertEquals(memoryInstance, _memoryInstance)
            memoryDropped = true
        }

        val expected = ChasmResult.Success(Unit)
        val actual = dropInstance(store, instance, memoryDropper)

        assertEquals(expected, actual)
        assertEquals(true, instance.instance.deallocated)
        assertEquals(false, memoryDropped)
    }
}
