package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.fixture.ir.type.functionType
import io.github.charlietap.chasm.fixture.ir.type.i32ValueType
import io.github.charlietap.chasm.fixture.ir.type.resultType
import io.github.charlietap.chasm.type.ir.ext.definedType
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
        val definedType = functionType.definedType()
        val function = runtimeFunction()
        val functionInstance = wasmFunctionInstance(
            definedType,
            functionType,
            moduleInstance,
            function,
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
