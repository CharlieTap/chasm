package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.TrapReason
import io.github.charlietap.chasm.embedding.fixture.publicFunction
import io.github.charlietap.chasm.embedding.fixture.publicInstance
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.instance.exportInstance
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.runtime.value.i64
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.fail

class InvokeTest {

    @Test
    fun `calling invoke with a function by name on an a deallocated instance returns InvocationOfADeinstantiatedInstance`() {

        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance())))
        val instance = publicInstance(
            store = store.store,
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

        val expected = ExecutionError.Rejected(InvocationError.InvocationOfADeinstantiatedInstance.toString())

        val actual = invoke(store, instance, "deallocated")

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke on an instance with a function by name is not exported returns FunctionNotFound`() {

        val function = "missing"

        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance())))
        val instance = publicInstance(store = store.store)

        val expected = ExecutionError.Rejected(InvocationError.FunctionNotFound(function).toString())

        val actual = invoke(store, instance, function)

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke with a function reference on a deallocated instance returns InvocationOfADeinstantiatedInstance`() {

        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance())))
        val address = functionAddress(117)
        val functionExtern = functionExternalValue(address)
        val function = publicFunction(functionExtern, store.store)
        val instance = publicInstance(
            store = store.store,
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

        val expected = ExecutionError.Rejected(InvocationError.InvocationOfADeinstantiatedInstance.toString())

        val actual = invoke(store, instance, function)

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke with a function reference that is not exported returns FunctionNotFound`() {

        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance())))
        val instance = publicInstance(store = store.store)
        val address = functionAddress(117)
        val function = publicFunction(functionExternalValue(address), store.store)

        val expected = ExecutionError.Rejected(InvocationError.FunctionNotFound("Function with address: ${address.address}").toString())

        val actual = invoke(store, instance, function)

        assertEquals(ChasmResult.Error(expected), actual)
    }

    @Test
    fun `calling invoke with a function by name that succeeds propagates the results`() {

        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance())))
        val args = emptyList<ExecutionValue>()
        val name = "foo"
        val address = functionAddress(0)

        val config = runtimeConfig()
        val instance = publicInstance(
            store = store.store,
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
        val functionInvoker: FunctionInvoker = { _config, _store, _instance, _address, _args ->
            assertEquals(config, _config)
            assertEquals(store.store, _store)
            assertEquals(instance.instance, _instance)
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
        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance())))
        val args = emptyList<ExecutionValue>()
        val address = functionAddress(0)
        val config = runtimeConfig()
        val instance = publicInstance(
            store = store.store,
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

        val error = InvocationError.Unreachable
        val functionInvoker: FunctionInvoker = { _config, _store, _instance, _address, _args ->
            assertEquals(config, _config)
            assertEquals(store.store, _store)
            assertEquals(instance.instance, _instance)
            assertEquals(address, _address)
            assertEquals(emptyList(), _args)

            Err(error)
        }

        val actual = invoke(store, instance, name, args, functionInvoker)

        assertTrapReason(TrapReason.UNREACHABLE, actual)
    }

    @Test
    fun `calling invoke with a function by reference that succeeds propagates the results`() {

        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance())))
        val args = emptyList<ExecutionValue>()
        val address = functionAddress(0)
        val functionExtern = functionExternalValue(address)
        val function = publicFunction(functionExtern, store.store)

        val config = runtimeConfig()
        val instance = publicInstance(
            store = store.store,
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
        val functionInvoker: FunctionInvoker = { _config, _store, _instance, _address, _args ->
            assertEquals(config, _config)
            assertEquals(store.store, _store)
            assertEquals(instance.instance, _instance)
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

        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance())))
        val args = emptyList<ExecutionValue>()
        val address = functionAddress(0)
        val functionExtern = functionExternalValue(address)
        val function = publicFunction(functionExtern, store.store)
        val config = runtimeConfig()
        val instance = publicInstance(
            store = store.store,
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

        val error = InvocationError.Unreachable
        val functionInvoker: FunctionInvoker = { _config, _store, _instance, _address, _args ->
            assertEquals(config, _config)
            assertEquals(store.store, _store)
            assertEquals(instance.instance, _instance)
            assertEquals(address, _address)
            assertEquals(emptyList(), _args)

            Err(error)
        }

        val actual = invoke(store, instance, function, args, functionInvoker)

        assertTrapReason(TrapReason.UNREACHABLE, actual)
    }

    @Test
    fun `calling invoke with a function address that succeeds propagates the results`() {

        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance())))
        val args = emptyList<ExecutionValue>()
        val address = functionAddress(0)

        val config = runtimeConfig()
        val instance = publicInstance(
            store = store.store,
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
        val functionInvoker: FunctionInvoker = { _config, _store, _instance, _address, _args ->
            assertEquals(config, _config)
            assertEquals(store.store, _store)
            assertEquals(instance.instance, _instance)
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

        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance())))
        val args = emptyList<ExecutionValue>()
        val address = functionAddress(0)
        val config = runtimeConfig()
        val instance = publicInstance(
            store = store.store,
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

        val error = InvocationError.Unreachable
        val functionInvoker: FunctionInvoker = { _config, _store, _instance, _address, _args ->
            assertEquals(config, _config)
            assertEquals(store.store, _store)
            assertEquals(instance.instance, _instance)
            assertEquals(address, _address)
            assertEquals(emptyList(), _args)

            Err(error)
        }

        val actual = invoke(store, instance, address, args, functionInvoker)

        assertTrapReason(TrapReason.UNREACHABLE, actual)
    }

    @Test
    fun `calling invoke with the wrong number of arguments is rejected before the invoker runs`() {

        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance())))
        val instance = publicInstance(
            store = store.store,
            moduleInstance = moduleInstance(
                exports = mutableListOf(exportInstance(nameValue("foo"), functionExternalValue(functionAddress(0)))),
            ),
        )
        val functionInvoker: FunctionInvoker = { _, _, _, _, _ -> fail("the invoker must not run") }

        val actual = invoke(store, instance, "foo", listOf(i32(1)), functionInvoker)

        assertEquals(ChasmResult.Error(ExecutionError.Rejected("ArgumentCountMismatch(expected=0, actual=1)")), actual)
    }

    @Test
    fun `calling invoke with an argument of the wrong type is rejected before the invoker runs`() {

        val functionType = functionType(params = resultType(listOf(i32ValueType())))
        val store = publicStore(store(functions = mutableListOf(hostFunctionInstance(functionType = functionType))))
        val instance = publicInstance(
            store = store.store,
            moduleInstance = moduleInstance(
                exports = mutableListOf(exportInstance(nameValue("foo"), functionExternalValue(functionAddress(0)))),
            ),
        )
        val functionInvoker: FunctionInvoker = { _, _, _, _, _ -> fail("the invoker must not run") }

        val actual = invoke(store, instance, "foo", listOf(i64(1L)), functionInvoker)

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
