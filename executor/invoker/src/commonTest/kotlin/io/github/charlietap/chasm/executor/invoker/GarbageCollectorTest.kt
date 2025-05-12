package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.fixture.runtime.instance.arrayAddress
import io.github.charlietap.chasm.fixture.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.runtime.instance.structAddress
import io.github.charlietap.chasm.fixture.runtime.instance.structInstance
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.structReferenceValue
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GarbageCollectorTest {

    @Test
    fun `can sweep an unreferenced struct`() {

        val structInstance = structInstance()
        val store = store()
        store.structs.add(structInstance)

        val stack = vstack()

        val result = GarbageCollector(store, stack)

        assertEquals(Ok(Unit), result)
        assertNull(store.structs[0])
    }

    @Test
    fun `can sweep an unreferenced array`() {

        val arrayInstance = arrayInstance()
        val store = store()
        store.arrays.add(arrayInstance)
        val stack = vstack()

        val result = GarbageCollector(store, stack)

        assertEquals(Ok(Unit), result)
        assertNull(store.arrays[0])
    }

    @Test
    fun `preserves a struct referenced in the stack`() {

        val structInstance = structInstance()
        val store = store()
        store.structs.add(structInstance)

        val structRef = structReferenceValue(structAddress(0))
        val stack = vstack(listOf(structRef))

        val result = GarbageCollector(store, stack)

        assertEquals(Ok(Unit), result)
        assertEquals(structInstance, store.structs[0])
    }

    @Test
    fun `preserves an array referenced in the stack`() {

        val arrayInstance = arrayInstance()
        val store = store()
        store.arrays.add(arrayInstance)

        val arrayRef = arrayReferenceValue(arrayAddress(0))
        val stack = vstack(listOf(arrayRef))

        val result = GarbageCollector(store, stack)

        assertEquals(Ok(Unit), result)
        assertEquals(arrayInstance, store.arrays[0])
    }

    @Test
    fun `preserves a struct referenced in an array`() {

        val structInstance = structInstance()
        val store = store()
        store.structs.add(structInstance)

        val structRef = structReferenceValue(structAddress(0))
        val arrayInstance = arrayInstance(
            fields = longArrayOf(structRef.toLongFromBoxed()),
        )
        store.arrays.add(arrayInstance)

        val arrayRef = arrayReferenceValue(arrayAddress(0))
        val stack = vstack(listOf(arrayRef))

        val result = GarbageCollector(store, stack)

        assertEquals(Ok(Unit), result)
        assertEquals(arrayInstance, store.arrays[0])
        assertEquals(structInstance, store.structs[0])
    }

    @Test
    fun `preserves an array referenced in a struct field`() {

        val arrayInstance = arrayInstance()
        val store = store()
        store.arrays.add(arrayInstance)

        val arrayRef = arrayReferenceValue(arrayAddress(0))
        val structInstance = structInstance(
            fields = longArrayOf(arrayRef.toLongFromBoxed()),
        )
        store.structs.add(structInstance)

        val structRef = structReferenceValue(structAddress(0))
        val stack = vstack(listOf(structRef))

        val result = GarbageCollector(store, stack)

        assertEquals(Ok(Unit), result)
        assertEquals(structInstance, store.structs[0])
        assertEquals(arrayInstance, store.arrays[0])
    }

    @Test
    fun `can collect multiple unreferenced objects`() {

        val store = store()

        val struct0 = structInstance()
        val struct1 = structInstance()
        val struct2 = structInstance()
        store.structs.add(struct0)
        store.structs.add(struct1)
        store.structs.add(struct2)

        val array0 = arrayInstance()
        val array1 = arrayInstance()
        val array2 = arrayInstance()
        store.arrays.add(array0)
        store.arrays.add(array1)
        store.arrays.add(array2)

        val structRef = structReferenceValue(structAddress(1))
        val arrayRef = arrayReferenceValue(arrayAddress(2))
        val stack = vstack(listOf(structRef, arrayRef))

        val result = GarbageCollector(store, stack)

        assertEquals(Ok(Unit), result)

        assertNull(store.structs[0])
        assertNotNull(store.structs[1])
        assertNull(store.structs[2])

        assertNull(store.arrays[0])
        assertNull(store.arrays[1])
        assertNotNull(store.arrays[2])
    }

    @Test
    fun `can collect unreferenced objects in a cyclic reference`() {

        val store = store()

        store.structs.add(structInstance(fields = longArrayOf(0L)))
        store.arrays.add(arrayInstance(fields = longArrayOf(0L)))

        val structRef = structReferenceValue(structAddress(0))
        val arrayRef = arrayReferenceValue(arrayAddress(0))

        store.structs[0] = structInstance(fields = longArrayOf(arrayRef.toLongFromBoxed()))
        store.arrays[0] = arrayInstance(fields = longArrayOf(structRef.toLongFromBoxed()))

        val stack = vstack()

        val result = GarbageCollector(store, stack)

        assertEquals(Ok(Unit), result)
        assertNull(store.structs[0])
        assertNull(store.arrays[0])
    }
}
