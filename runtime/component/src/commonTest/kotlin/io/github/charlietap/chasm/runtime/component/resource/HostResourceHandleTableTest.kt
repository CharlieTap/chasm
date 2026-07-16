package io.github.charlietap.chasm.runtime.component.resource

import io.github.charlietap.chasm.fixture.runtime.component.address.componentCallToken
import io.github.charlietap.chasm.fixture.runtime.component.address.runtimeResourceTypeAddress
import io.github.charlietap.chasm.fixture.runtime.component.resource.hostResourceHandleTable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class HostResourceHandleTableTest {

    @Test
    fun `reused host slots reject stale generations`() {
        val type = runtimeResourceTypeAddress()
        val subject = hostResourceHandleTable()
        val firstHandle = subject.insertOwn(type, 42)
        val removedRepresentation = subject.removeOwn(firstHandle, type)
        val secondHandle = subject.insertOwn(type, 84)

        val staleHandle = assertFailsWith<ResourceTableException> {
            subject.ownRepresentation(firstHandle, type)
        }
        val actual = HostGenerationObservation(
            firstHandle = firstHandle.id,
            removedRepresentation = removedRepresentation,
            secondHandle = secondHandle.id,
            reusedSlot = firstHandle.id.toUInt() == secondHandle.id.toUInt(),
            changedGeneration = (firstHandle.id shr 32) != (secondHandle.id shr 32),
            staleHandle = staleHandle.error,
            representation = subject.ownRepresentation(secondHandle, type),
        )

        val expected = HostGenerationObservation(
            firstHandle = 0x0000000100000001uL,
            removedRepresentation = 42,
            secondHandle = 0x0000000200000001uL,
            reusedSlot = true,
            changedGeneration = true,
            staleHandle = ResourceTableError.InvalidHandle,
            representation = 84,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `host borrows retain their call token`() {
        val type = runtimeResourceTypeAddress()
        val callToken = componentCallToken()
        val otherCallToken = componentCallToken(2uL)
        val subject = hostResourceHandleTable()
        val handle = subject.insertBorrow(type, 42, callToken)

        val otherCall = assertFailsWith<ResourceTableException> {
            subject.borrowRepresentation(handle, type, otherCallToken)
        }
        val actual = HostBorrowObservation(
            active = subject.isBorrow(handle, callToken),
            otherCall = otherCall.error,
            removedRepresentation = subject.removeBorrow(handle, type, callToken),
            activeAfterRemoval = subject.isBorrow(handle, callToken),
        )

        val expected = HostBorrowObservation(
            active = true,
            otherCall = ResourceTableError.BorrowScopeMismatch,
            removedRepresentation = 42,
            activeAfterRemoval = false,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `cleanup invalidates handles before callbacks and preserves generations`() {
        val type = runtimeResourceTypeAddress()
        val subject = hostResourceHandleTable()
        val handle = subject.insertOwn(type, 42)
        var callbackHandleError: ResourceTableError? = null
        var callbackAllocationError: ResourceTableError? = null

        subject.cleanup { _, _ ->
            callbackHandleError = (
                runCatching { subject.ownRepresentation(handle, type) }
                    .exceptionOrNull() as? ResourceTableException
            )?.error
            callbackAllocationError = (
                runCatching { subject.insertOwn(type, 84) }
                    .exceptionOrNull() as? ResourceTableException
            )?.error
        }
        val nextHandle = subject.insertOwn(type, 84)
        val actual = HostCleanupObservation(
            callbackHandleError = callbackHandleError,
            callbackAllocationError = callbackAllocationError,
            changedGeneration = (handle.id shr 32) != (nextHandle.id shr 32),
            representation = subject.ownRepresentation(nextHandle, type),
        )

        val expected = HostCleanupObservation(
            callbackHandleError = ResourceTableError.InvalidHandle,
            callbackAllocationError = ResourceTableError.TableUnavailable,
            changedGeneration = true,
            representation = 84,
        )
        assertEquals(expected, actual)
    }
}

private data class HostGenerationObservation(
    val firstHandle: ULong,
    val removedRepresentation: Int,
    val secondHandle: ULong,
    val reusedSlot: Boolean,
    val changedGeneration: Boolean,
    val staleHandle: ResourceTableError,
    val representation: Int,
)

private data class HostBorrowObservation(
    val active: Boolean,
    val otherCall: ResourceTableError,
    val removedRepresentation: Int,
    val activeAfterRemoval: Boolean,
)

private data class HostCleanupObservation(
    val callbackHandleError: ResourceTableError?,
    val callbackAllocationError: ResourceTableError?,
    val changedGeneration: Boolean,
    val representation: Int,
)
