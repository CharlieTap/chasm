package io.github.charlietap.chasm.embedding.extern

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.fixture.runtime.instance.arrayAddress
import io.github.charlietap.chasm.fixture.runtime.instance.hostAddress
import io.github.charlietap.chasm.fixture.runtime.instance.hostInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.externReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.hostReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.nullReferenceValue
import io.github.charlietap.chasm.fixture.type.externHeapType
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.type.AbstractHeapType
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadExternValueTest {

    @Test
    fun `can read a host value from an extern ref`() {

        val value = "hello world"
        val store = publicStore(
            store(
                hosts = mutableListOf(hostInstance(value)),
            ),
        )
        val reference = externReferenceValue(hostReferenceValue(hostAddress(0)))

        val expected = Ok(value)

        val actual = internalReadExternValue(
            store = store,
            reference = reference,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `can read null from a null extern ref`() {

        val store = publicStore(store())
        val reference = nullReferenceValue(externHeapType())

        val expected = ChasmResult.Success(null)

        val actual = readExternValue(
            store = store,
            reference = reference,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `rejects direct host refs`() {

        val store = publicStore(store())
        val reference = hostReferenceValue(hostAddress(0))

        val expected = Err(InvocationError.ExternReferenceExpected)

        val actual = internalReadExternValue(
            store = store,
            reference = reference,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `rejects extern refs wrapping non host refs`() {

        val store = publicStore(store())
        val reference = externReferenceValue(arrayReferenceValue(arrayAddress(0)))

        val expected = Err(InvocationError.UnexpectedReferenceValue)

        val actual = internalReadExternValue(
            store = store,
            reference = reference,
        )

        assertEquals(expected, actual)
    }
}
