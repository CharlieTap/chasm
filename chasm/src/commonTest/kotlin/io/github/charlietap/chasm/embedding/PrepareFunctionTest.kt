package io.github.charlietap.chasm.embedding

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicInstance
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.getOrNull
import io.github.charlietap.chasm.fixture.ast.value.nameValue
import io.github.charlietap.chasm.fixture.runtime.instance.exportInstance
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.globalExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.runtime.error.InvocationError
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import io.github.charlietap.chasm.fixture.runtime.store as runtimeStore

class PrepareFunctionTest {

    @Test
    fun `can prepare and repeatedly invoke an exported host function`() {
        val name = "identity"
        val address = functionAddress(0)
        val moduleInstance = moduleInstance(
            exports = mutableListOf(exportInstance(nameValue(name), functionExternalValue(address))),
        )
        val internalStore = runtimeStore(
            functions = mutableListOf(
                hostFunctionInstance { params ->
                    assertEquals(moduleInstance, instance)
                    params
                },
            ),
        )
        val publicStore = publicStore(internalStore)
        val instance = publicInstance(moduleInstance = moduleInstance)
        val prepared = assertNotNull(prepareFunction(publicStore, instance, name).getOrNull())

        assertEquals(ChasmResult.Success(listOf(i32(1))), prepared(listOf(i32(1))))
        assertEquals(ChasmResult.Success(listOf(i32(2))), prepared(listOf(i32(2))))
    }

    @Test
    fun `preparing a missing or non-function export returns FunctionNotFound`() {
        val name = "missing"
        val expected = ChasmResult.Error(
            ChasmError.ExecutionError(InvocationError.FunctionNotFound(name).toString()),
        )

        assertEquals(expected, prepareFunction(publicStore(), publicInstance(), name))

        val instance = publicInstance(
            moduleInstance = moduleInstance(
                exports = mutableListOf(exportInstance(nameValue(name), globalExternalValue())),
            ),
        )
        assertEquals(expected, prepareFunction(publicStore(), instance, name))
    }

    @Test
    fun `cannot prepare a function from a deallocated instance`() {
        val instance = publicInstance(moduleInstance = moduleInstance(deallocated = true))
        val expected = ChasmResult.Error(
            ChasmError.ExecutionError(InvocationError.InvocationOfADeinstantiatedInstance.toString()),
        )

        assertEquals(expected, prepareFunction(publicStore(), instance, "function"))
    }

    @Test
    fun `prepared function cannot execute after its instance is dropped`() {
        val address = functionAddress(0)
        val instance = publicInstance(
            moduleInstance = moduleInstance(
                exports = mutableListOf(exportInstance(nameValue("function"), functionExternalValue(address))),
                functionAddresses = mutableListOf(address),
            ),
        )
        val store = publicStore(runtimeStore(functions = mutableListOf(hostFunctionInstance())))
        val prepared = assertNotNull(prepareFunction(store, instance, "function").getOrNull())

        dropInstance(store, instance)

        val expected = ChasmResult.Error(
            ChasmError.ExecutionError(InvocationError.InvocationOfADeinstantiatedInstance.toString()),
        )
        assertEquals(expected, prepared())
    }

    @Test
    fun `prepared function cannot execute after its store is dropped or reused`() {
        val address = functionAddress(0)
        val internalStore = runtimeStore(functions = mutableListOf(hostFunctionInstance()))
        val store = publicStore(internalStore)
        val instance = publicInstance(
            moduleInstance = moduleInstance(
                exports = mutableListOf(exportInstance(nameValue("function"), functionExternalValue(address))),
            ),
        )
        val prepared = assertNotNull(prepareFunction(store, instance, "function").getOrNull())

        dropStore(store)

        val expected = ChasmResult.Error(
            ChasmError.ExecutionError(InvocationError.FunctionLookupFailed(address).toString()),
        )
        assertEquals(expected, prepared())

        internalStore.functions += hostFunctionInstance { listOf(i32(9)) }
        assertEquals(expected, prepared())
    }
}
