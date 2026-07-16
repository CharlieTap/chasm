package io.github.charlietap.chasm.runtime.store

import io.github.charlietap.chasm.fixture.runtime.store
import kotlin.test.Test
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class StoreIdentityTest {

    @Test
    fun `identity is stable for the lifetime of a store`() {
        val store = store()

        val first = store.identity()
        val second = store.identity()

        assertSame(first, second)
    }

    @Test
    fun `stores have distinct identities`() {
        val first = store().identity()
        val second = store().identity()

        assertNotSame(first, second)
    }
}
