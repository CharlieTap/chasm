package io.github.charlietap.chasm.embedding.global

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadGlobalTest {

    @Test
    fun `can read a value from a global`() {

        val value = 117L
        val instance = globalInstance(value = value)
        val store = publicStore(store(globals = mutableListOf(instance)))
        val address = globalAddress(0)
        val global = Global(globalExternalValue(address))

        val expected: ChasmResult<ExecutionValue, ChasmError.ExecutionError> = ChasmResult.Success(NumberValue.I32(117))

        val actual = readGlobal(
            store = store,
            global = global,
        )

        assertEquals(expected, actual)
    }
}
