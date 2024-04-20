package io.github.charlietap.chasm.embedding.table

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instance.tableInstance
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadTableTest {

    @Test
    fun `can read a reference value from a table`() {

        val functionAddress = functionAddress()
        val value = ReferenceValue.Function(functionAddress)
        val instance = tableInstance(elements = mutableListOf(value))
        val store = store(tables = mutableListOf(instance))
        val address = Address.Table(0)

        val expected: ChasmResult<ReferenceValue, ChasmError.ExecutionError> = ChasmResult.Success(value)

        val actual = readTable(
            store = store,
            address = address,
            elementIndex = 0,
        )

        assertEquals(expected, actual)
    }
}
