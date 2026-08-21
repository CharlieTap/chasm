package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Err
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.reference.arrayFieldTestFixture
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.invoker.GarbageCollector
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.runtime.error.InvocationError
import kotlin.test.Test
import kotlin.test.assertEquals

class GCTest {

    @Test
    fun `gc runs successfully and correctly handles collection`() {

        val fixture = arrayFieldTestFixture(arrayType(), LongArray(0))
        val publicStore = fixture.store

        val actual = gc(publicStore)

        assertEquals(ChasmResult.Success(Unit), actual)
        assertEquals(-1, publicStore.store.heap.arrayRuntimeTypeIdOrNegative(fixture.rawReference))
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
