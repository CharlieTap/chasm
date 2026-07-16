package io.github.charlietap.chasm.runtime.component.resource

import io.github.charlietap.chasm.fixture.runtime.component.resource.hostResourcePayloadTable
import kotlin.test.Test
import kotlin.test.assertEquals

class HostResourcePayloadTableTest {

    @Test
    fun `payload slots are optional and reused without coupling them to host handles`() {
        val subject = hostResourcePayloadTable()
        val first = subject.insert("first")
        val optional = subject.insert(null)
        val removed = subject.remove(first)
        val reused = subject.insert("second")
        val actual = PayloadObservation(
            first = first,
            optional = optional,
            optionalPayload = subject[optional],
            removed = removed,
            reused = reused,
            reusedPayload = subject[reused],
            size = subject.size,
        )

        val expected = PayloadObservation(
            first = 0,
            optional = 1,
            optionalPayload = null,
            removed = "first",
            reused = 0,
            reusedPayload = "second",
            size = 2,
        )
        assertEquals(expected, actual)
    }
}

private data class PayloadObservation(
    val first: Int,
    val optional: Int,
    val optionalPayload: Any?,
    val removed: Any?,
    val reused: Int,
    val reusedPayload: Any?,
    val size: Int,
)
