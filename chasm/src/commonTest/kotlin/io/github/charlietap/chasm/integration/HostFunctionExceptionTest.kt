package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.fixture.publicImport
import io.github.charlietap.chasm.embedding.fixture.publicStore
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.HostFunction
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import io.github.charlietap.chasm.fixture.ast.type.resultType
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.host.HostFunctionException
import kotlin.test.Test
import kotlin.test.assertEquals

class HostFunctionExceptionTest {

    @Test
    fun `can run a host function that throws an exception and return a chasm error`() {

        val store = publicStore(store())

        val functionType = functionType(
            results = resultType(
                listOf(
                    i32ValueType(),
                ),
            ),
        )
        val reason = "Fail gracefully"
        val exception = HostFunctionException(reason)
        val hostFunction: HostFunction = {
            throw exception
        }
        val functionExternal = function(store, functionType, hostFunction)
        val functionImport = publicImport(
            "env",
            "host_function",
            functionExternal,
        )
        val result = testRunner(
            fileName = "host_function.wasm",
            fileDirectory = FILE_DIR,
            functionName = "call_host_function",
            store = store,
            imports = listOf(
                functionImport,
            ),
        )

        val expected = ChasmError.ExecutionError(
            InvocationError.HostFunctionError(reason).toString(),
        )

        assertEquals(ChasmResult.Error(expected), result)
    }

    companion object {
        private const val FILE_DIR = "src/commonTest/resources/integration/"
    }
}
