package io.github.charlietap.chasm.executor.instantiator.runtime.allocation

import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocationJournal
import io.github.charlietap.chasm.fixture.ir.module.tableIndex
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.moduleAllocation
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.runtimeInstanceId
import io.github.charlietap.chasm.fixture.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.runtime.instance.tagAddress
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleAllocationJournalTest {

    @Test
    fun `records only addresses allocated after imports`() {
        val instance = moduleInstance(
            functionAddresses = mutableListOf(functionAddress()),
            tableAddresses = mutableListOf(tableAddress()),
            memAddresses = mutableListOf(memoryAddress()),
            tagAddresses = mutableListOf(tagAddress()),
            globalAddresses = mutableListOf(globalAddress()),
        )
        val journal = ModuleAllocationJournal(instance)
        journal.markImports()
        instance.functionAddresses += functionAddress(1)
        instance.tableAddresses += tableAddress(1)
        instance.memAddresses += memoryAddress(1)
        instance.tagAddresses += tagAddress(1)
        instance.globalAddresses += globalAddress(1)
        val providers = listOf(runtimeInstanceId())

        val actual = Triple(
            journal.isImported(tableIndex()),
            journal.isImported(tableIndex(1)),
            journal.allocation(providers),
        )

        val expected = Triple(
            true,
            false,
            moduleAllocation(
                functionAddresses = listOf(functionAddress(1)),
                instructionAddresses = listOf(functionAddress(1)),
                tableAddresses = listOf(tableAddress(1)),
                memoryAddresses = listOf(memoryAddress(1)),
                tagAddresses = listOf(tagAddress(1)),
                globalAddresses = listOf(globalAddress(1)),
                providers = providers,
            ),
        )
        assertEquals(expected, actual)
    }
}
