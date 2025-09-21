package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicFunction
import io.github.charlietap.chasm.embedding.fixture.publicInstance
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.ir.value.nameValue
import io.github.charlietap.chasm.fixture.runtime.instance.exportInstance
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertEquals

class InvokeTest {

    @Test
    fun `calling invoke with a function by name on an a deallocated instance returns InvocationOfADeinstantiatedInstance`() {

        val store = publicStore()
        val instance = publicInstance(
            moduleInstance = moduleInstance(
                deallocated = true,
                exports = mutableListOf(
                    exportInstance(
                        nameValue("deallocated"),
                        functionExternalValue(),
                    ),
                ),
            ),
        )

        val expected = ChasmError.ExecutionError(InvocationError.InvocationOfADeinstantiatedInstance.toString())

        val actual = invoke(store, instance, "deallocated")

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke on an instance with a function by name is not exported returns FunctionNotFound`() {

        val function = "missing"

        val store = publicStore()
        val instance = publicInstance()

        val expected = ChasmError.ExecutionError(InvocationError.FunctionNotFound(function).toString())

        val actual = invoke(store, instance, function)

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke with a function reference on a deallocated instance returns InvocationOfADeinstantiatedInstance`() {

        val store = publicStore()
        val address = functionAddress(117)
        val functionExtern = functionExternalValue(address)
        val function = publicFunction(functionExtern)
        val instance = publicInstance(
            moduleInstance = moduleInstance(
                deallocated = true,
                exports = mutableListOf(
                    exportInstance(
                        nameValue("deallocated"),
                        functionExtern,
                    ),
                ),
            ),
        )

        val expected = ChasmError.ExecutionError(InvocationError.InvocationOfADeinstantiatedInstance.toString())

        val actual = invoke(store, instance, function)

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke with a function reference that is not exported returns FunctionNotFound`() {

        val store = publicStore()
        val instance = publicInstance()
        val address = functionAddress(117)
        val function = publicFunction(
            functionExternalValue(address),
        )

        val expected = ChasmError.ExecutionError(InvocationError.FunctionNotFound("Function with address: ${address.address}").toString())

        val actual = invoke(store, instance, function)

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke with a function by name that succeeds propagates the results`() {

        val store = publicStore()
        val args = emptyList<ExecutionValue>()
        val name = "foo"
        val address = functionAddress(0)

        val config = runtimeConfig()
        val instance = publicInstance(
            config = config,
            moduleInstance = moduleInstance(
                exports = mutableListOf(
                    exportInstance(
                        nameValue(name),
                        functionExternalValue(address),
                    ),
                ),
            ),
        )

        val results = listOf(i32(117))
        val functionInvoker: FunctionInvoker = { _config, _store, _address, _args ->
            assertEquals(config, _config)
            assertEquals(store.store, _store)
            assertEquals(address, _address)
            assertEquals(emptyList(), _args)

            Ok(results)
        }

        val expected = ChasmResult.Success(listOf(i32(117)))

        val actual = invoke(store, instance, name, args, functionInvoker)

        assertEquals(expected, actual)
    }

    @Test
    fun `calling invoke with a function by name that fails propagates the invocation error`() {

        val name = "foo"
        val store = publicStore()
        val args = emptyList<ExecutionValue>()
        val address = functionAddress(0)
        val config = runtimeConfig()
        val instance = publicInstance(
            config = config,
            moduleInstance = moduleInstance(
                exports = mutableListOf(
                    exportInstance(
                        nameValue(name),
                        functionExternalValue(address),
                    ),
                ),
            ),
        )

        val error = InvocationError.FunctionNotFound("function")
        val functionInvoker: FunctionInvoker = { _config, _store, _address, _args ->
            assertEquals(config, _config)
            assertEquals(store.store, _store)
            assertEquals(address, _address)
            assertEquals(emptyList(), _args)

            Err(error)
        }

        val expected = ChasmError.ExecutionError(error.toString())

        val actual = invoke(store, instance, name, args, functionInvoker)

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke with a function by reference that succeeds propagates the results`() {

        val store = publicStore()
        val args = emptyList<ExecutionValue>()
        val address = functionAddress(0)
        val functionExtern = functionExternalValue(address)
        val function = publicFunction(functionExtern)

        val config = runtimeConfig()
        val instance = publicInstance(
            config = config,
            moduleInstance = moduleInstance(
                exports = mutableListOf(
                    exportInstance(
                        nameValue(),
                        functionExtern,
                    ),
                ),
            ),
        )

        val results = listOf(i32(117))
        val functionInvoker: FunctionInvoker = { _config, _store, _address, _args ->
            assertEquals(config, _config)
            assertEquals(store.store, _store)
            assertEquals(address, _address)
            assertEquals(emptyList(), _args)

            Ok(results)
        }

        val expected = ChasmResult.Success(listOf(i32(117)))

        val actual = invoke(store, instance, function, args, functionInvoker)

        assertEquals(expected, actual)
    }

    @Test
    fun `calling invoke with a function by reference that fails propagates the invocation error`() {

        val store = publicStore()
        val args = emptyList<ExecutionValue>()
        val address = functionAddress(0)
        val functionExtern = functionExternalValue(address)
        val function = publicFunction(functionExtern)
        val config = runtimeConfig()
        val instance = publicInstance(
            config = config,
            moduleInstance = moduleInstance(
                exports = mutableListOf(
                    exportInstance(
                        nameValue(),
                        functionExtern,
                    ),
                ),
            ),
        )

        val error = InvocationError.FunctionNotFound("function")
        val functionInvoker: FunctionInvoker = { _config, _store, _address, _args ->
            assertEquals(config, _config)
            assertEquals(store.store, _store)
            assertEquals(address, _address)
            assertEquals(emptyList(), _args)

            Err(error)
        }

        val expected = ChasmError.ExecutionError(error.toString())

        val actual = invoke(store, instance, function, args, functionInvoker)

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke with a function address that succeeds propagates the results`() {

        val store = publicStore()
        val args = emptyList<ExecutionValue>()
        val address = functionAddress(0)

        val config = runtimeConfig()
        val instance = publicInstance(
            config = config,
            moduleInstance = moduleInstance(
                exports = mutableListOf(
                    exportInstance(
                        nameValue(),
                        functionExternalValue(address),
                    ),
                ),
            ),
        )

        val results = listOf(i32(117))
        val functionInvoker: FunctionInvoker = { _config, _store, _address, _args ->
            assertEquals(config, _config)
            assertEquals(store.store, _store)
            assertEquals(address, _address)
            assertEquals(emptyList(), _args)

            Ok(results)
        }

        val expected = ChasmResult.Success(listOf(i32(117)))

        val actual = invoke(store, instance, address, args, functionInvoker)

        assertEquals(expected, actual)
    }

    @Test
    fun `calling invoke on a function address that fails propagates the invocation error`() {

        val store = publicStore()
        val args = emptyList<ExecutionValue>()
        val address = functionAddress(0)
        val config = runtimeConfig()
        val instance = publicInstance(
            config = config,
            moduleInstance = moduleInstance(
                exports = mutableListOf(
                    exportInstance(
                        nameValue(),
                        functionExternalValue(address),
                    ),
                ),
            ),
        )

        val error = InvocationError.FunctionNotFound("function")
        val functionInvoker: FunctionInvoker = { _config, _store, _address, _args ->
            assertEquals(config, _config)
            assertEquals(store.store, _store)
            assertEquals(address, _address)
            assertEquals(emptyList(), _args)

            Err(error)
        }

        val expected = ChasmError.ExecutionError(error.toString())

        val actual = invoke(store, instance, address, args, functionInvoker)

        assertEquals(ChasmResult.Error(expected), actual)
    }
}
