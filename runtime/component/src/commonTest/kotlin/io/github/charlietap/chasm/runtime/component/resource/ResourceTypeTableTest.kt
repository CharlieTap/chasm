package io.github.charlietap.chasm.runtime.component.resource

import io.github.charlietap.chasm.fixture.runtime.component.resource.guestRuntimeResourceType
import io.github.charlietap.chasm.fixture.runtime.component.resource.hostRuntimeResourceType
import io.github.charlietap.chasm.fixture.runtime.component.resource.resourceTypeTable
import kotlin.test.Test
import kotlin.test.assertEquals

class ResourceTypeTableTest {

    @Test
    fun `each resource definition receives a fresh store address`() {
        val guestType = guestRuntimeResourceType()
        val hostType = hostRuntimeResourceType()
        val subject = resourceTypeTable()
        val first = subject.define(guestType)
        val second = subject.define(guestType)
        val host = subject.define(hostType)
        val actual = ResourceTypesObservation(
            addresses = listOf(first.address, second.address, host.address),
            types = listOf(subject[first], subject[second], subject[host]),
            size = subject.size,
        )

        val expected = ResourceTypesObservation(
            addresses = listOf(0, 1, 2),
            types = listOf(guestType, guestType, hostType),
            size = 3,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `discard clears only the supplied resource addresses`() {
        val firstType = guestRuntimeResourceType()
        val secondType = guestRuntimeResourceType()
        val subject = resourceTypeTable()
        val first = subject.define(firstType)
        val second = subject.define(secondType)

        subject.discard(intArrayOf(first.address))
        val actual = listOf(subject[first], subject[second])

        val expected = listOf(null, secondType)
        assertEquals(expected, actual)
    }
}

private data class ResourceTypesObservation(
    val addresses: List<Int>,
    val types: List<RuntimeResourceType?>,
    val size: Int,
)
