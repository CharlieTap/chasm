package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.fixture.INSTANCE_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.runtime.error.InvocationError
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class InvokeTest {

    @Test
    fun `calling invoke with a function by name on a deallocated instance returns InvocationOfADeinstantiatedInstance`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)
        dropInstance(instance)

        val expected = ExecutionError.Rejected(InvocationError.InvocationOfADeinstantiatedInstance.toString())

        val actual = invoke(instance, "identity", listOf(NumberValue.I32(1)))

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke on an instance with a function by name that is not exported returns FunctionNotFound`() = runTest {

        val function = "missing"
        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        val expected = ExecutionError.Rejected(InvocationError.FunctionNotFound(function).toString())

        val actual = invoke(instance, function)

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke with a function by name that succeeds propagates the results`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        val expected = ChasmResult.Success(listOf(NumberValue.I32(117)))

        val actual = invoke(instance, "identity", listOf(NumberValue.I32(117)))

        assertEquals(expected, actual)
    }

    @Test
    fun `calling invoke with a function by name that fails propagates the invocation error`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        val actual = invoke(instance, "trap")

        assertTrapReason(TrapReason.UNREACHABLE, actual)
    }

    @Test
    fun `calling invoke with the wrong number of arguments is rejected before the function runs`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        val actual = invoke(instance, "trap", listOf(NumberValue.I32(1)))

        assertEquals(ChasmResult.Error(ExecutionError.Rejected("ArgumentCountMismatch(expected=0, actual=1)")), actual)
    }

    @Test
    fun `calling invoke with an argument of the wrong type is rejected before the function runs`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        val actual = invoke(instance, "identity", listOf(NumberValue.I64(1L)))

        val error = assertIs<ExecutionError.Rejected>(assertIs<ChasmResult.Error<*>>(actual).error)
        assertTrue(error.message.startsWith("ArgumentTypeMismatch(index=0"))
    }

    private fun assertTrapReason(
        reason: TrapReason,
        result: ChasmResult<*, *>,
    ) {

        val error = assertIs<ChasmResult.Error<*>>(result).error

        assertEquals(reason, assertIs<ExecutionError.Trapped>(error).trap.reason)
    }
}
