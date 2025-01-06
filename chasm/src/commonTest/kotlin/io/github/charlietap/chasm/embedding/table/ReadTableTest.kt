package io.github.charlietap.chasm.embedding.table

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.tableInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.functionReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadTableTest {

    @Test
    fun `can read a reference value from a table`() {

        val functionAddress = functionAddress()
        val value = functionReferenceValue(functionAddress)
        val instance = tableInstance(elements = arrayOf(functionReferenceValue(functionAddress)))
        val store = publicStore(store(tables = mutableListOf(instance)))
        val address = tableAddress(0)
        val table = publicTable(tableExternalValue(address))

        val expected: ChasmResult<ReferenceValue, ChasmError.ExecutionError> = ChasmResult.Success(value)

        val actual = readTable(
            store = store,
            table = table,
            elementIndex = 0,
        )

        assertEquals(expected, actual)
    }
}
