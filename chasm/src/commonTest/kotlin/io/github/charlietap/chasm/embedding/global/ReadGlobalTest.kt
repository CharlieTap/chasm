package io.github.charlietap.chasm.embedding.global

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadGlobalTest {

    @Test
    fun `can read a value from a global`() {

        val value = i32(117)
        val instance = globalInstance(value = value)
        val store = publicStore(store(globals = mutableListOf(instance)))
        val address = globalAddress(0)
        val global = Global(globalExternalValue(address))

        val expected: ChasmResult<Value, ChasmError.ExecutionError> = ChasmResult.Success(Value.Number.I32(117))

        val actual = readGlobal(
            store = store,
            global = global,
        )

        assertEquals(expected, actual)
    }
}
