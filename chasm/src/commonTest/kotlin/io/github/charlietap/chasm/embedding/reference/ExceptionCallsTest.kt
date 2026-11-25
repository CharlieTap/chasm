package io.github.charlietap.chasm.embedding.reference

import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.tag
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.type.referenceValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class ExceptionCallsTest {

    @Test
    fun `an exception carries its tag and payload`() {

        val store = interpreterStore()
        val host = Any()
        val type = functionType(params = resultType(listOf(i64ValueType(), EXTERNREF)))
        val tag = tag(store, tagType(functionType = type))

        val exception = createException(tag, longArrayOf(117L, 0L), arrayOf(null, host))

        assertEquals(tag, exceptionTag(exception))
        assertEquals(2, payloadSize(exception))
        assertEquals(117L, readPayload(exception, 0))
        assertSame(host, readPayloadReference(exception, 1))
    }

    @Test
    fun `payloads refuse the other lane and indices past their end`() {

        val store = interpreterStore()
        val type = functionType(params = resultType(listOf(i64ValueType(), EXTERNREF)))
        val exception = createException(tag(store, tagType(functionType = type)), LongArray(2), arrayOfNulls(2))

        assertFailsWith<IllegalArgumentException> { readPayloadReference(exception, 0) }
        assertFailsWith<IllegalArgumentException> { readPayload(exception, 1) }
        assertFailsWith<IndexOutOfBoundsException> { readPayload(exception, 2) }
    }

    @Test
    fun `a payload shorter than the tag's parameters is rejected`() {

        val store = interpreterStore()
        val type = functionType(params = resultType(listOf(i64ValueType(), EXTERNREF)))
        val tag = tag(store, tagType(functionType = type))

        assertFailsWith<IllegalArgumentException> {
            createException(tag, LongArray(1), arrayOfNulls(1))
        }
    }

    private companion object {
        val EXTERNREF = referenceValueType(refNullReferenceType(AbstractHeapType.Extern))
    }
}
