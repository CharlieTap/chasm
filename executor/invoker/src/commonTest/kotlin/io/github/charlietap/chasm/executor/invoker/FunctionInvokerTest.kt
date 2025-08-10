package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionInvokerTest {

    @Test
    fun `can invoke a function and return a result`() {

        val config = runtimeConfig()
        val params = mutableListOf<ExecutionValue>(i32(117))
        val address = functionAddress(0)
        val moduleInstance = moduleInstance(
            functionAddresses = mutableListOf(address),
        )
        val functionType = functionType(
            results = resultType(
                listOf(i32ValueType()),
            ),
        )
        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            module = moduleInstance,
        )
        val functionInstruction = dispatchableInstruction()
        val store = store(
            functions = mutableListOf(functionInstance),
            instructions = mutableListOf(functionInstruction),
        )

        val threadExecutor: ThreadExecutor = { _config, _params ->
            assertEquals(config, _config.config)
            assertEquals(params, _params)
            Ok(listOf(117L))
        }

        val actual = FunctionInvoker(
            config = config,
            store = store,
            address = address,
            values = params,
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(listOf(i32(117))), actual)
    }
}
