package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.invoker.GarbageCollector
import io.github.charlietap.chasm.fixture.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.runtime.instance.structInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.error.InvocationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GCTest {

    @Test
    fun `gc runs successfully and correctly handles collection`() {

        val internalStore = store()
        val arrayInstance = arrayInstance()
        internalStore.arrays.add(arrayInstance)
        val structInstance = structInstance()
        internalStore.structs.add(structInstance)

        val publicStore = publicStore(internalStore)

        val actual = gc(publicStore)

        assertEquals(ChasmResult.Success(Unit), actual)
        assertNull(internalStore.arrays[0])
        assertNull(internalStore.structs[0])
    }

    @Test
    fun `gc propagates the error when collection fails`() {

        val publicStore = publicStore()
        val error = InvocationError.GarbageCollectionFailed("Test error")

        val collector: GarbageCollector = { _store, _ ->
            assertEquals(publicStore.store, _store)
            Err(error)
        }

        val expected = ChasmResult.Error(ChasmError.ExecutionError(error.toString()))
        val actual = gc(publicStore, collector)

        assertEquals(expected, actual)
    }
}
