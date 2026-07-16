package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.invoker.drop.ComponentResourceDropper
import io.github.charlietap.chasm.fixture.runtime.component.value.ownComponentResourceValue
import io.github.charlietap.chasm.runtime.component.store.ComponentStore
import io.github.charlietap.chasm.runtime.value.component.ComponentValue
import kotlin.test.Test
import kotlin.test.assertEquals
import io.github.charlietap.chasm.runtime.store.Store as RuntimeStore

class ComponentResourceTest {

    @Test
    fun `passes the store and owned resource to the resource dropper`() {
        val store = publicStore()
        val componentStore = store.componentStore()
        val resource = ownComponentResourceValue(store.identity)
        var call: ResourceDropCall? = null
        val resourceDropper: ComponentResourceDropper = { actualStore, actualComponentStore, actualResource ->
            call = ResourceDropCall(actualStore, actualComponentStore, actualResource)
            Ok(Unit)
        }

        val result = dropResource(
            store = store,
            resource = resource,
            resourceDropper = resourceDropper,
        )
        val actual = ResourceDropObservation(result, call)

        val expected = ResourceDropObservation(
            result = ChasmResult.Success(Unit),
            call = ResourceDropCall(store.store, componentStore, resource),
        )
        assertEquals(expected, actual)
    }
}

private data class ResourceDropCall(
    val store: RuntimeStore,
    val componentStore: ComponentStore,
    val resource: ComponentValue.Resource.Own,
)

private data class ResourceDropObservation(
    val result: ChasmResult<Unit, *>,
    val call: ResourceDropCall?,
)
