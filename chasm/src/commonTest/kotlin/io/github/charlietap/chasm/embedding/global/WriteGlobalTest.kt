package io.github.charlietap.chasm.embedding.global

import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.instance.globalInstance
import io.github.charlietap.chasm.fixture.store
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteGlobalTest {

    @Test
    fun `can write a value to a global`() {

        val value = NumberValue.I32(117)
        val instance = globalInstance()
        val store = store(globals = mutableListOf(instance))
        val address = Address.Global(0)

        val expected: ChasmResult<Unit, ChasmError.ExecutionError> = ChasmResult.Success(Unit)

        val actual = writeGlobal(
            store = store,
            address = address,
            value = value,
        )

        assertEquals(expected, actual)
        assertEquals(value, store.globals[0].value)
    }
}
