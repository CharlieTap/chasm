package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.fixture.publicComponentInstance
import io.github.charlietap.chasm.embedding.fixture.publicInstance
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.invoker.drop.ComponentInstanceDropper
import io.github.charlietap.chasm.executor.invoker.drop.ModuleInstanceDropper
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class DropInstanceTest {

    @Test
    fun `passes the store and instance to the module instance dropper`() {
        val store = publicStore()
        val instance = publicInstance()
        val instanceDropper: ModuleInstanceDropper = { actualStore, actualInstance ->
            assertSame(store.store, actualStore)
            assertSame(instance.instance, actualInstance)
            Ok(Unit)
        }

        val actual = dropInstance(
            store = store,
            instance = instance,
            instanceDropper = instanceDropper,
        )

        val expected = ChasmResult.Success(Unit)
        assertEquals(expected, actual)
    }

    @Test
    fun `passes the store and component root to the component instance dropper`() {
        val store = publicStore()
        val instance = publicComponentInstance(store = store)
        val componentStore = store.componentStore()
        val instanceDropper: ComponentInstanceDropper = { actualStore, actualComponentStore, actualRoot ->
            assertSame(store.store, actualStore)
            assertSame(componentStore, actualComponentStore)
            assertEquals(instance.root, actualRoot)
            Ok(Unit)
        }

        val actual = dropInstance(
            store = store,
            instance = instance,
            instanceDropper = instanceDropper,
        )

        val expected = ChasmResult.Success(Unit)
        assertEquals(expected, actual)
    }
}
