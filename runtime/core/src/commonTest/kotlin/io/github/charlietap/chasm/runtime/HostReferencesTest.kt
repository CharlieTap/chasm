package io.github.charlietap.chasm.runtime

import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.type.CompositeType
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.RecursiveType
import io.github.charlietap.chasm.type.StructType
import io.github.charlietap.chasm.type.SubType
import io.github.charlietap.chasm.type.factory.DefinedTypeFactory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class HostReferencesTest {

    @Test
    fun `scoped roots keep references alive until their scope ends`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(emptyStructTypes())[0]
        val outer = store.heap.allocateStruct(runtimeType, longArrayOf())
        val inner = store.heap.allocateStruct(runtimeType, longArrayOf())
        val outerMarker = store.heap.beginScope(capacity = 2)
        store.heap.rootScoped(outer)
        val innerMarker = store.heap.beginScope()
        store.heap.rootScoped(inner)

        store.heap.collectGarbage(store)
        assertNotEquals(-1, store.heap.structRuntimeTypeIdOrNegative(outer))
        assertNotEquals(-1, store.heap.structRuntimeTypeIdOrNegative(inner))

        store.heap.endScope(innerMarker)
        store.heap.collectGarbage(store)
        assertNotEquals(-1, store.heap.structRuntimeTypeIdOrNegative(outer))
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(inner))

        store.heap.endScope(outerMarker)
        store.heap.collectGarbage(store)
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(outer))
    }

    @Test
    fun `retained roots keep references alive until every root is released`() {
        val store = Store()
        val runtimeType = store.heap.registerRuntimeTypes(emptyStructTypes())[0]
        val reference = store.heap.allocateStruct(runtimeType, longArrayOf())
        val firstRoot = store.heap.retain(reference)
        val secondRoot = store.heap.retain(reference)

        assertEquals(reference, store.heap.reference(firstRoot))
        store.heap.release(firstRoot)
        store.heap.collectGarbage(store)
        assertNotEquals(-1, store.heap.structRuntimeTypeIdOrNegative(reference))

        store.heap.release(secondRoot)
        store.heap.collectGarbage(store)
        assertEquals(-1, store.heap.structRuntimeTypeIdOrNegative(reference))

        val reused = store.heap.retain(0L)
        assertEquals(secondRoot.slot, reused.slot)
    }
}

private fun emptyStructTypes(): List<DefinedType> = DefinedTypeFactory(
    listOf(
        RecursiveType(
            subTypes = listOf(
                SubType.Final(
                    superTypes = emptyList(),
                    compositeType = CompositeType.Struct(StructType(emptyList())),
                ),
            ),
            state = RecursiveType.State.SYNTAX,
        ),
    ),
)
