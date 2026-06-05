package io.github.charlietap.chasm.embedding.extern

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.fixture.runtime.instance.hostAddress
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.externReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.hostReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.nullReferenceValue
import io.github.charlietap.chasm.fixture.type.externHeapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExternRefTest {

    @Test
    fun `can create a null extern ref without allocating a host instance`() {

        val store = publicStore(store())

        val expected = nullReferenceValue(externHeapType())

        val actual = externRef(store, null)

        assertEquals(expected, actual)
        assertTrue(store.store.hosts.isEmpty())
    }

    @Test
    fun `can create a host backed extern ref`() {

        val store = publicStore(store())
        val value = "hello world"

        val expected = externReferenceValue(hostReferenceValue(hostAddress(0)))

        val actual = externRef(store, value)

        assertEquals(expected, actual)
        assertEquals(value, store.store.hosts[0].value)
    }
}
