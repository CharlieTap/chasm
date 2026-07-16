package io.github.charlietap.chasm.executor.invoker.drop

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.instance.dataAddress
import io.github.charlietap.chasm.fixture.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.runtime.instance.elementAddress
import io.github.charlietap.chasm.fixture.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleAllocation
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.store.instanceLifetimes
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class ModuleInstanceDropperTest {

    @Test
    fun `drops owned state and leaves imported state intact`() {
        val importedData = dataInstance(bytes = ubyteArrayOf(1u))
        val ownedData = dataInstance(bytes = ubyteArrayOf(2u))
        val importedElement = elementInstance(elements = longArrayOf(1L))
        val ownedElement = elementInstance(elements = longArrayOf(2L))
        val importedGlobal = globalInstance(value = 1L)
        val ownedGlobal = globalInstance(value = 2L)
        val importedMemory = memoryInstance()
        val ownedMemory = memoryInstance()
        val importedTable = tableInstance(elements = longArrayOf(1L))
        val ownedTable = tableInstance(elements = longArrayOf(2L))
        val instance = moduleInstance(
            functionAddresses = mutableListOf(functionAddress()),
            tableAddresses = mutableListOf(tableAddress(), tableAddress(1)),
            memAddresses = mutableListOf(memoryAddress(), memoryAddress(1)),
            globalAddresses = mutableListOf(globalAddress(), globalAddress(1)),
            elemAddresses = mutableListOf(elementAddress(1)),
            dataAddresses = mutableListOf(dataAddress(1)),
        )
        val function = wasmFunctionInstance(module = instance)
        val instruction = dispatchableInstruction()
        val store = store(
            data = mutableListOf(importedData, ownedData),
            elements = mutableListOf(importedElement, ownedElement),
            functions = mutableListOf(function),
            globals = mutableListOf(importedGlobal, ownedGlobal),
            memories = mutableListOf(importedMemory, ownedMemory),
            tables = mutableListOf(importedTable, ownedTable),
            instructions = mutableListOf(instruction),
        )
        val lifetimes = store.instanceLifetimes()
        lifetimes.begin(instance, emptyList())
        lifetimes.register(
            instance,
            moduleAllocation(
                functionAddresses = listOf(functionAddress()),
                instructionAddresses = listOf(functionAddress()),
                tableAddresses = listOf(tableAddress(1)),
                memoryAddresses = listOf(memoryAddress(1)),
                globalAddresses = listOf(globalAddress(1)),
                elementAddresses = listOf(elementAddress(1)),
                dataAddresses = listOf(dataAddress(1)),
            ),
        )
        lifetimes.publish(instance)
        val memoryDropper: MemoryInstanceDropper = { memory ->
            assertSame(ownedMemory, memory)
        }

        val actual = ModuleInstanceDropper(
            store = store,
            instance = instance,
            memoryDropper = memoryDropper,
            release = Release.Drop,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(true, instance.deallocated)
        assertContentEquals(ubyteArrayOf(1u), importedData.bytes)
        assertContentEquals(ubyteArrayOf(), ownedData.bytes)
        assertContentEquals(longArrayOf(1L), importedElement.elements)
        assertContentEquals(longArrayOf(), ownedElement.elements)
        assertEquals(1L, importedGlobal.value)
        assertEquals(ExecutionValue.Uninitialised.toLongFromBoxed(), ownedGlobal.value)
        assertContentEquals(longArrayOf(1L), importedTable.elements)
        assertContentEquals(longArrayOf(), ownedTable.elements)
        assertNotSame(instruction, store.instructions.single())
    }
}
