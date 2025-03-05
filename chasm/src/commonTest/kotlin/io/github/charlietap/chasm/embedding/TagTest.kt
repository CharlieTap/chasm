package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.fixture.publicTag
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.address.Address
import kotlin.test.Test
import kotlin.test.assertEquals

class TagTest {

    @Test
    fun `can allocate a tag in the store and return an external value`() {

        val store = publicStore()
        val tagType = tagType()

        val expectedExternalValue = ExternalValue.Tag(Address.Tag(0))
        val expected = publicTag(expectedExternalValue)

        val actual = tag(store, tagType)

        assertEquals(expected, actual)
        assertEquals(tagType(), store.store.tags[0].type)
    }
}
