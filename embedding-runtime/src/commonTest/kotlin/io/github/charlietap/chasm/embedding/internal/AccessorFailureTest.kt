package io.github.charlietap.chasm.embedding.internal

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class AccessorFailureTest {

    @Test
    fun `bounds failures throw index out of bounds`() {

        val errors = listOf(
            InvocationError.MemoryOperationOutOfBounds,
            InvocationError.TableOperationOutOfBounds,
            InvocationError.ArrayOperationOutOfBounds,
            InvocationError.TableElementLookupFailed(1),
            InvocationError.ArrayFieldLookupFailed(2),
            InvocationError.StructFieldLookupFailed(3),
        )

        errors.forEach { error ->

            val failure = assertFailsWith<IndexOutOfBoundsException> {
                throwAccessorFailure(error)
            }

            assertEquals(error.toString(), failure.message)
        }
    }

    @Test
    fun `lifetime and collection failures throw illegal state`() {

        val errors = listOf(
            InvocationError.StoreDropped,
            InvocationError.InvocationOfADeinstantiatedInstance,
            InvocationError.GarbageCollectionFailed("Test error"),
        )

        errors.forEach { error ->

            val failure = assertFailsWith<IllegalStateException> {
                throwAccessorFailure(error)
            }

            assertEquals(error.toString(), failure.message)
        }
    }

    @Test
    fun `every other failure throws illegal argument`() {

        val error = InvocationError.FailedToCastReference

        val failure = assertFailsWith<IllegalArgumentException> {
            throwAccessorFailure(error)
        }

        assertEquals(error.toString(), failure.message)
    }

    @Test
    fun `an invocation exception inside an accessor is mapped to its accessor failure`() {

        val failure = assertFailsWith<IndexOutOfBoundsException> {
            withAccessorFailures {
                throw InvocationException(InvocationError.TableOperationOutOfBounds)
            }
        }

        assertEquals(InvocationError.TableOperationOutOfBounds.toString(), failure.message)
    }

    @Test
    fun `an accessor that succeeds returns its value`() {

        val actual = withAccessorFailures { 117 }

        assertEquals(117, actual)
    }

    @Test
    fun `indices and ranges outside their size throw index out of bounds`() {

        checkIndex(0, 1)
        checkRange(0, 1, 1)
        checkRange(1, 0, 1)

        assertFailsWith<IndexOutOfBoundsException> { checkIndex(-1, 1) }
        assertFailsWith<IndexOutOfBoundsException> { checkIndex(1, 1) }
        assertFailsWith<IndexOutOfBoundsException> { checkRange(-1, 1, 1) }
        assertFailsWith<IndexOutOfBoundsException> { checkRange(0, -1, 1) }
        assertFailsWith<IndexOutOfBoundsException> { checkRange(1, 1, 1) }
        assertFailsWith<IndexOutOfBoundsException> { checkRange(Int.MAX_VALUE, 1, Int.MAX_VALUE) }
    }
}
