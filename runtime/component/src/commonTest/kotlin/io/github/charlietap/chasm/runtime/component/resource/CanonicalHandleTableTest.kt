package io.github.charlietap.chasm.runtime.component.resource

import io.github.charlietap.chasm.fixture.runtime.component.address.componentCallToken
import io.github.charlietap.chasm.fixture.runtime.component.address.runtimeResourceTypeAddress
import io.github.charlietap.chasm.fixture.runtime.component.resource.canonicalHandleTable
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CanonicalHandleTableTest {

    @Test
    fun `own handles retain lending state and reuse released slots`() {
        val type = runtimeResourceTypeAddress()
        val otherType = runtimeResourceTypeAddress(1)
        val subject = canonicalHandleTable()
        val firstHandle = subject.insertOwn(type, 42)

        val representation = subject.ownRepresentation(firstHandle, type)
        val lend = subject.lend(firstHandle, type)
        val lentRemoval = assertFailsWith<ResourceTableException> {
            subject.removeOwn(firstHandle, type)
        }
        subject.undoLend(firstHandle)
        val removedRepresentation = subject.removeOwn(firstHandle, type)
        val reusedHandle = subject.insertOwn(type, 84)
        val typeMismatch = assertFailsWith<ResourceTableException> {
            subject.ownRepresentation(reusedHandle, otherType)
        }
        val actual = OwnHandleObservation(
            firstHandle = firstHandle,
            representation = representation,
            lentRepresentation = lend.representation,
            ownsLender = lend.ownsLender,
            lentRemoval = lentRemoval.error,
            removedRepresentation = removedRepresentation,
            reusedHandle = reusedHandle,
            typeMismatch = typeMismatch.error,
            size = subject.size,
        )

        val expected = OwnHandleObservation(
            firstHandle = 1,
            representation = 42,
            lentRepresentation = 42,
            ownsLender = true,
            lentRemoval = ResourceTableError.ResourceLent,
            removedRepresentation = 42,
            reusedHandle = 1,
            typeMismatch = ResourceTableError.TypeMismatch,
            size = 1,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `borrow handles retain their originating call while allowing reborrowing`() {
        val type = runtimeResourceTypeAddress()
        val callToken = componentCallToken()
        val subject = canonicalHandleTable()
        val handle = subject.insertBorrow(type, 42, callToken)

        val representation = subject.borrowRepresentation(handle, type)
        val lend = subject.lend(handle, type)
        val origin = subject.removeBorrow(handle, type)
        val staleHandle = assertFailsWith<ResourceTableException> {
            subject.borrowRepresentation(handle, type)
        }
        val actual = BorrowHandleObservation(
            handle = handle,
            representation = representation,
            lentRepresentation = lend.representation,
            ownsLender = lend.ownsLender,
            origin = origin,
            staleHandle = staleHandle.error,
            size = subject.size,
        )

        val expected = BorrowHandleObservation(
            handle = 1,
            representation = 42,
            lentRepresentation = 42,
            ownsLender = false,
            origin = callToken,
            staleHandle = ResourceTableError.InvalidHandle,
            size = 0,
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `cleanup visits owned resources in handle order and resets the table`() {
        val firstType = runtimeResourceTypeAddress()
        val secondType = runtimeResourceTypeAddress(1)
        val callToken = componentCallToken()
        val subject = canonicalHandleTable()
        subject.insertOwn(firstType, 10)
        subject.insertBorrow(firstType, 20, callToken)
        subject.insertOwn(secondType, 30)
        val cleaned = mutableListOf<Pair<Int, Int>>()
        var callbackSize = -1
        var callbackAllocationError: ResourceTableError? = null

        subject.cleanup { type, representation ->
            cleaned += type.address to representation
            callbackSize = subject.size
            callbackAllocationError = (
                runCatching { subject.insertOwn(firstType, 40) }
                    .exceptionOrNull() as? ResourceTableException
            )?.error
        }
        val actual = CleanupObservation(
            cleaned = cleaned,
            callbackSize = callbackSize,
            callbackAllocationError = callbackAllocationError,
            nextHandle = subject.insertOwn(firstType, 40),
            size = subject.size,
        )

        val expected = CleanupObservation(
            cleaned = listOf(0 to 10, 1 to 30),
            callbackSize = 0,
            callbackAllocationError = ResourceTableError.TableUnavailable,
            nextHandle = 1,
            size = 1,
        )
        assertEquals(expected, actual)
    }
}

private data class OwnHandleObservation(
    val firstHandle: Int,
    val representation: Int,
    val lentRepresentation: Int,
    val ownsLender: Boolean,
    val lentRemoval: ResourceTableError,
    val removedRepresentation: Int,
    val reusedHandle: Int,
    val typeMismatch: ResourceTableError,
    val size: Int,
)

private data class BorrowHandleObservation(
    val handle: Int,
    val representation: Int,
    val lentRepresentation: Int,
    val ownsLender: Boolean,
    val origin: io.github.charlietap.chasm.runtime.address.ComponentCallToken,
    val staleHandle: ResourceTableError,
    val size: Int,
)

private data class CleanupObservation(
    val cleaned: List<Pair<Int, Int>>,
    val callbackSize: Int,
    val callbackAllocationError: ResourceTableError?,
    val nextHandle: Int,
    val size: Int,
)
