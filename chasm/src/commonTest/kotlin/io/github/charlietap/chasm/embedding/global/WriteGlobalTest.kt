package io.github.charlietap.chasm.embedding.global

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Global
import io.github.charlietap.chasm.embedding.shapes.Value
import io.github.charlietap.chasm.fixture.instance.globalAddress
import io.github.charlietap.chasm.fixture.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.instance.globalInstance
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class WriteGlobalTest {

    @Test
    fun `can write a value to a global`() {

        val value = Value.Number.I32(117)
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
        assertEquals(i32(117), store.store.globals[0].value)
    }
}
