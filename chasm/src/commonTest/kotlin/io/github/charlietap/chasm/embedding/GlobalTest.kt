package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicGlobal
import io.github.charlietap.chasm.embedding.fixture.publicGlobalType
import io.github.charlietap.chasm.embedding.fixture.publicI32
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.fixture.ast.type.globalType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class GlobalTest {

    @Test
    fun `can allocate a global in the store and return an external value`() {

        val store = publicStore()
        val globalType = publicGlobalType()
        val initialValue = publicI32(117)

        val expectedExternalValue = globalExternalValue(Address.Global(0))
        val expected = publicGlobal(expectedExternalValue)

        val actual = global(store, globalType, initialValue)

        assertEquals(expected, actual)
        assertEquals(globalType(), store.store.globals[0].type)
        assertEquals(i32(117), store.store.globals[0].value)
    }
}
