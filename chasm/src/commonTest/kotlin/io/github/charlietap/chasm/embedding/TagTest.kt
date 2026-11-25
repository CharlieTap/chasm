package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.address.Address
import kotlin.test.Test
import kotlin.test.assertEquals

class TagTest {

    @Test
    fun `can allocate a tag in the store and return a handle`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val type = tagType()

        val actual = tag(store, type)

        assertEquals(type, actual.type)
        assertEquals(tagType(), runtime.heap.tag(Address.Tag(0)).type)
    }
}
