package io.github.charlietap.chasm.embedding.table

import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicFunctionReferenceValue
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTable
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instance.tableAddress
import io.github.charlietap.chasm.fixture.instance.tableExternalValue
import io.github.charlietap.chasm.fixture.instance.tableInstance
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.value.functionReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteTableTest {

    @Test
    fun `can write a reference value to a table`() {

        val functionAddress = functionAddress()
        val value = publicFunctionReferenceValue(functionAddress.address)
        val instance = tableInstance(elements = arrayOf(ReferenceValue.Null(AbstractHeapType.Func)))
        val store = publicStore(store(tables = mutableListOf(instance)))
        val address = tableAddress(0)
        val table = publicTable(tableExternalValue(address))

        val expected: ChasmResult<Unit, ChasmError.ExecutionError> = ChasmResult.Success(Unit)

        val actual = writeTable(
            store = store,
            table = table,
            elementIndex = 0,
            value = value,
        )

        assertEquals(expected, actual)
        assertEquals(functionReferenceValue(functionAddress), store.store.tables[0].elements[0])
    }
}
