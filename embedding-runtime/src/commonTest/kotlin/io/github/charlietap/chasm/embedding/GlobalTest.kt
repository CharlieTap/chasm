package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicGlobal
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.fixture.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.globalType
import io.github.charlietap.chasm.runtime.address.Address
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalTest {

    @Test
    fun `can allocate a global in the store and return an external value`() {

        val store = publicStore()
        val globalType = globalType()
        val initialValue = i32(117)

        val expectedExternalValue = globalExternalValue(Address.Global(0))
        val expected = publicGlobal(expectedExternalValue)

        val actual = global(store, globalType, initialValue)

        assertEquals(expected, actual)
        assertEquals(globalType(), store.store.globals[0].type)
        assertEquals(117, store.store.globals[0].value)
    }
}
