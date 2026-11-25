package io.github.charlietap.chasm.embedding.codegen

import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.fixture.ALLOCATOR_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.internal.InterpreterInstance
import io.github.charlietap.chasm.embedding.shapes._payload
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class AllocatorTest {

    @Test
    fun `prepares allocator functions once`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, ALLOCATOR_MODULE)
        val freed = assertNotNull(exports(instance).global("freed"))
        val allocator = Wasm32Allocator(
            instance = instance,
            allocFunction = "alloc",
            freeFunction = "free",
        )
        val holder = instance._payload() as InterpreterInstance

        holder.module.exports.clear()
        holder.prepared.clear()

        val address = allocator.alloc(17)
        allocator.free(address)

        assertEquals(117, address)
        assertEquals(117L, readGlobal(freed))
    }
}
