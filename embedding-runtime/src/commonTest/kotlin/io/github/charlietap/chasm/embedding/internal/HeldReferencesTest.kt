package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.embedding.collect
import io.github.charlietap.chasm.embedding.dropStore
import io.github.charlietap.chasm.embedding.fixture.collectPlatformGarbage
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.reference.createException
import io.github.charlietap.chasm.embedding.reference.payloadSize
import io.github.charlietap.chasm.embedding.reference.readPayloadReference
import io.github.charlietap.chasm.embedding.shapes.Tag
import io.github.charlietap.chasm.embedding.tag
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.reference.ExceptionReference
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotSame
import kotlin.test.assertTrue

class HeldReferencesTest {

    @Test
    fun `a claim past the high water mark reuses the slots of references the host dropped`() {

        val store = interpreterStore()
        val tag = tag(store, tagType())

        claimAndDrop(tag, HIGH_WATER)
        collectPlatformGarbage()

        val reference = createException(tag, LongArray(0), arrayOfNulls(0)) as HeldException

        assertTrue(reference.slot < HIGH_WATER, "slot ${reference.slot} was not reused")
    }

    @Test
    fun `a host reading many references without guest allocation keeps the held table bounded`() {

        val store = interpreterStore()
        val exnref = referenceValueType(refNullReferenceType(AbstractHeapType.Exception))
        val inner = createException(tag(store, tagType()), LongArray(0), arrayOfNulls(0))
        val outerTag = tag(store, tagType(functionType = functionType(params = resultType(listOf(exnref)))))
        val outer = createException(outerTag, LongArray(1), arrayOf(inner))
        var highestSlot = 0

        repeat(READS) { read ->

            val reference = readPayloadReference(outer, 0) as HeldException
            highestSlot = maxOf(highestSlot, reference.slot)

            if (read % COLLECTION_INTERVAL == 0) {
                collectPlatformGarbage()
            }
        }

        assertTrue(highestSlot < READS / 10, "highest slot $highestSlot")
    }

    @Test
    fun `a held reference keeps its object alive across a collection`() {

        val runtime = store()
        val store = interpreterStore(runtime)
        val tag = tag(store, tagType())
        val reference = createException(tag, LongArray(0), arrayOfNulls(0)) as HeldException

        collect(store)

        assertTrue(runtime.heap.exceptionTagAddressOrNegative(reference.raw) >= 0)
        assertEquals(0, payloadSize(reference))
    }

    @Test
    fun `a reference read twice is two holders of one object`() {

        val store = interpreterStore()
        val exnref = referenceValueType(refNullReferenceType(AbstractHeapType.Exception))
        val inner = createException(tag(store, tagType()), LongArray(0), arrayOfNulls(0))
        val outerTag = tag(store, tagType(functionType = functionType(params = resultType(listOf(exnref)))))
        val outer = createException(outerTag, LongArray(1), arrayOf(inner))

        val first = readPayloadReference(outer, 0)
        val second = readPayloadReference(outer, 0)

        assertNotSame(first, second)
        assertEquals(first, second)
        assertEquals(inner, first)
    }

    @Test
    fun `a held reference of a dropped store throws illegal state`() {

        val store = interpreterStore()
        val reference = createException(tag(store, tagType()), LongArray(0), arrayOfNulls(0))

        dropStore(store)

        assertFailsWith<IllegalStateException> {
            payloadSize(reference)
        }
    }

    private fun claimAndDrop(
        tag: Tag,
        count: Int,
    ) {
        repeat(count) {
            val reference: ExceptionReference = createException(tag, LongArray(0), arrayOfNulls(0))
            assertEquals(0, payloadSize(reference))
        }
    }

    private companion object {
        const val HIGH_WATER = 64
        const val READS = 100_000
        const val COLLECTION_INTERVAL = 2_000
    }
}
