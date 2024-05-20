package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ChasmResult
import io.github.charlietap.chasm.error.ChasmError
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.instance.exportInstance
import io.github.charlietap.chasm.fixture.instance.functionAddress
import io.github.charlietap.chasm.fixture.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.fixture.value.nameValue
import kotlin.test.Test
import kotlin.test.assertEquals

class InvokeTest {

    @Test
    fun `calling invoke on an instance with a function that is not exported returns FunctionNotFound`() {

        val function = "missing"

        val store = store()
        val instance = moduleInstance()

        val expected = ChasmError.ExecutionError(InvocationError.FunctionNotFound(function))

        val actual = invoke(store, instance, function)

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke on a function that fails propagates the invocation error`() {

        val function = "fail"

        val store = store()
        val args = emptyList<ExecutionValue>()
        val address = functionAddress(0)
        val externalValue = functionExternalValue(address)
        val exportInstance = exportInstance(
            name = nameValue(function),
            value = externalValue,
        )
        val instance = moduleInstance(
            exports = mutableListOf(
                exportInstance,
            ),
        )

        val error = InvocationError.Trap.TrapEncountered
        val functionInvoker: FunctionInvoker = { _store, _address, _args ->
            assertEquals(store, _store)
            assertEquals(address, _address)
            assertEquals(args, _args)

            Err(error)
        }

        val expected = ChasmError.ExecutionError(error)

        val actual = invoke(store, instance, function, args, functionInvoker)

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke on a function that succeeds propagates the results`() {

        val function = "succeed"

        val store = store()
        val args = emptyList<ExecutionValue>()
        val address = functionAddress(0)
        val externalValue = functionExternalValue(address)
        val exportInstance = exportInstance(
            name = nameValue(function),
            value = externalValue,
        )
        val instance = moduleInstance(
            exports = mutableListOf(
                exportInstance,
            ),
        )

        val results = listOf(i32(117))
        val functionInvoker: FunctionInvoker = { _store, _address, _args ->
            assertEquals(store, _store)
            assertEquals(address, _address)
            assertEquals(args, _args)

            Ok(results)
        }

        val expected = ChasmResult.Success(results)

        val actual = invoke(store, instance, function, args, functionInvoker)

        assertEquals(expected, actual)
    }
}
