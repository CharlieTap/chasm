package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.fixture.HOST_IDENTITY_MODULE
import io.github.charlietap.chasm.embedding.fixture.INSTANCE_MODULE
import io.github.charlietap.chasm.embedding.fixture.instantiate
import io.github.charlietap.chasm.embedding.fixture.interpreterStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostModuleInstance
import io.github.charlietap.chasm.host.readI32
import io.github.charlietap.chasm.host.writeI32
import io.github.charlietap.chasm.runtime.error.InvocationError
import kotlinx.coroutines.test.runTest
import kotlin.contextOf
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertSame

class PrepareFunctionTest {

    @Test
    fun `can prepare and repeatedly invoke an exported host function`() = runTest {

        val store = interpreterStore()
        val callers = mutableListOf<Instance>()
        val type = functionType(
            params = resultType(listOf(i32ValueType())),
            results = resultType(listOf(i32ValueType())),
        )
        val hostFunction = HostFunction { parameters, results ->
            callers += contextOf<HostModuleInstance>().instance
            results.writeI32(0, parameters.readI32(0))
        }
        val identity = function(store, type, hostFunction)
        val instance = instantiate(store, HOST_IDENTITY_MODULE, listOf(Import("host", "identity", identity)))
        val prepared = assertNotNull(prepareFunction(instance, "identity").getOrNull())

        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(1))), prepared(listOf(NumberValue.I32(1))))
        assertEquals(ChasmResult.Success(listOf(NumberValue.I32(2))), prepared(listOf(NumberValue.I32(2))))
        assertEquals(2, callers.size)
        callers.forEach { caller -> assertSame(instance, caller) }
    }

    @Test
    fun `preparing the same export twice returns the same prepared function`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        val first = prepareFunction(instance, "identity").getOrNull()
        val second = prepareFunction(instance, "identity").getOrNull()

        assertSame(assertNotNull(first), second)
    }

    @Test
    fun `preparing a missing or non-function export returns FunctionNotFound`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)

        assertEquals(notFound("missing"), prepareFunction(instance, "missing"))
        assertEquals(notFound("memory"), prepareFunction(instance, "memory"))
    }

    @Test
    fun `cannot prepare a function from a deallocated instance`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)
        dropInstance(instance)

        val expected = ChasmResult.Error(
            ExecutionError.Rejected(InvocationError.InvocationOfADeinstantiatedInstance.toString()),
        )

        assertEquals(expected, prepareFunction(instance, "identity"))
    }

    @Test
    fun `prepared function cannot execute after its instance is dropped`() = runTest {

        val store = interpreterStore()
        val instance = instantiate(store, INSTANCE_MODULE)
        val prepared = assertNotNull(prepareFunction(instance, "trap").getOrNull())

        dropInstance(instance)

        val expected = ChasmResult.Error(
            ExecutionError.Rejected(InvocationError.InvocationOfADeinstantiatedInstance.toString()),
        )
        assertEquals(expected, prepared())
    }

    @Test
    fun `prepared function cannot execute after its store is dropped or reused`() = runTest {

        val runtime = store()
        val store = interpreterStore(runtime)
        val instance = instantiate(store, INSTANCE_MODULE)
        val prepared = assertNotNull(prepareFunction(instance, "trap").getOrNull())

        dropStore(store)

        val expected = ChasmResult.Error(ExecutionError.Rejected(InvocationError.StoreDropped.toString()))
        assertEquals(expected, prepared())

        runtime.functions += hostFunctionInstance()
        assertEquals(expected, prepared())
    }

    private fun notFound(name: String): ChasmResult<Nothing, ExecutionError.Rejected> {
        return ChasmResult.Error(ExecutionError.Rejected(InvocationError.FunctionNotFound(name).toString()))
    }
}
