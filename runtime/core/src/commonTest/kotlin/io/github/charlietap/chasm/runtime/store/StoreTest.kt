package io.github.charlietap.chasm.runtime.store

import io.github.charlietap.chasm.fixture.runtime.store
import kotlin.test.Test
import kotlin.test.assertSame

class StoreTest {

    @Test
    fun `a copied store shares instance lifetime state`() {
        val store = store()
        val copy = store.copy()

        val actual = copy.instanceLifetimes()

        val expected = store.instanceLifetimes()
        assertSame(expected, actual)
    }
}
