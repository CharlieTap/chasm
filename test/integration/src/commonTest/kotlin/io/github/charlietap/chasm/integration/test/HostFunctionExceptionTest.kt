package io.github.charlietap.chasm.integration.test

import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.function
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.host.HostFunction
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.integration.integrationTest
import io.github.charlietap.chasm.integration.runner
import io.github.charlietap.chasm.runtime.error.InvocationError
import kotlin.test.Test
import kotlin.test.assertEquals

class HostFunctionExceptionTest {

    @Test
    fun `can run a host function that throws an exception and return a chasm error`() = integrationTest(FIXTURE) {

        val store = store()

        val functionType = functionType(
            results = resultType(
                listOf(
                    i32ValueType(),
                ),
            ),
        )
        val reason = "Fail gracefully"
        val exception = HostFunctionException(reason)
        val hostFunction = HostFunction { _, _ ->
            throw exception
        }
        val functionExternal = function(store, functionType, hostFunction)
        val functionImport = Import(
            "env",
            "host_function",
            functionExternal,
        )
        val imports = listOf(
            functionImport,
        )

        val result = runner(store, imports).invoke(
            name = "call_host_function",
        )

        val expected = ChasmError.ExecutionError(
            InvocationError.HostFunctionError(reason).toString(),
        )

        assertEquals(ChasmResult.Error(expected), result, engine.name())
    }

    private companion object {
        const val FIXTURE = "host_function"
    }
}
