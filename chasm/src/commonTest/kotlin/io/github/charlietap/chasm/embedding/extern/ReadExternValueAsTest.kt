package io.github.charlietap.chasm.embedding.extern

import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.fixture.runtime.instance.hostAddress
import io.github.charlietap.chasm.fixture.runtime.instance.hostInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.externReferenceValue
import io.github.charlietap.chasm.fixture.runtime.value.hostReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ReadExternValueAsTest {

    @Test
    fun `can read a typed host value from an extern ref`() {

        val value = "hello world"
        val store = publicStore(
            store(
                hosts = mutableListOf(hostInstance(value)),
            ),
        )
        val reference = externReferenceValue(hostReferenceValue(hostAddress(0)))

        val expected = ChasmResult.Success(value)

        val actual = readExternValueAs<String>(
            store = store,
            reference = reference,
        )

        assertEquals(expected, actual)
    }

    @Test
    fun `returns an error when the host value has a different type`() {

        val store = publicStore(
            store(
                hosts = mutableListOf(hostInstance(117)),
            ),
        )
        val reference = externReferenceValue(hostReferenceValue(hostAddress(0)))

        val actual = readExternValueAs<String>(
            store = store,
            reference = reference,
        )

        assertEquals(true, actual is ChasmResult.Error)
    }
}
