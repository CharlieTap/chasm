package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalTest {

    @Test
    fun `can allocate a global in the store and return an external value`() {

        val store = store()
        val globalType = globalType()
        val initialValue = i32(117)

        val expected = ExternalValue.Global(Address.Global(0))

        val actual = global(store, globalType, initialValue)

        assertEquals(expected, actual)
        assertEquals(globalType, store.globals[0].type)
        assertEquals(initialValue, store.globals[0].value)
    }
}
