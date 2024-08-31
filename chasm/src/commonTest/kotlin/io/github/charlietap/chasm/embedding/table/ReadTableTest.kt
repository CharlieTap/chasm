package io.github.charlietap.chasm.embedding.table

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicFunctionReferenceValue
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instance.tableAddress
import io.github.charlietap.chasm.fixture.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.instance.tableInstance
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.value.functionReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadTableTest {

    @Test
    fun `can read a reference value from a table`() {

        val functionAddress = functionAddress()
        val value = publicFunctionReferenceValue(functionAddress.address)
        val instance = tableInstance(elements = arrayOf(functionReferenceValue(functionAddress)))
        val store = publicStore(store(tables = mutableListOf(instance)))
        val address = tableAddress(0)
        val table = publicTable(tableExternalValue(address))

        val expected: ChasmResult<Value.Reference, ChasmError.ExecutionError> = ChasmResult.Success(value)

        val actual = readTable(
            store = store,
            table = table,
            elementIndex = 0,
        )

        assertEquals(expected, actual)
    }
}
