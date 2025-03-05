package io.github.charlietap.chasm.embedding.global

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.runtime.value.NumberValue
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteGlobalTest {

    @Test
    fun `can write a value to a global`() {

        val value = NumberValue.I32(117)
        val instance = globalInstance()
        val store = publicStore(store(globals = mutableListOf(instance)))
        val address = globalAddress(0)
        val global = Global(globalExternalValue(address))

        val expected: ChasmResult<Unit, ChasmError.ExecutionError> = ChasmResult.Success(Unit)

        val actual = writeGlobal(
            store = store,
            global = global,
            value = value,
        )

        assertEquals(expected, actual)
        assertEquals(117, store.store.globals[0].value)
    }
}
