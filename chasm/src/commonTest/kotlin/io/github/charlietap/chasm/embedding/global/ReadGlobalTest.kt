package io.github.charlietap.chasm.embedding.global

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.instance.globalInstance
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadGlobalTest {

    @Test
    fun `can read a value from a global`() {

        val value = NumberValue.I32(117)
        val instance = globalInstance(value = value)
        val store = store(globals = mutableListOf(instance))
        val address = Address.Global(0)

        val expected: ChasmResult<ExecutionValue, ChasmError.ExecutionError> = ChasmResult.Success(value)

        val actual = readGlobal(
            store = store,
            address = address,
        )

        assertEquals(expected, actual)
    }
}
