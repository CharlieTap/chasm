package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals

class FunctionInvokerTest {

    @Test
    fun `can invoke a function and return a result`() {

        val config = runtimeConfig()
        val locals = mutableListOf<ExecutionValue>(i32(117))
        val address = functionAddress(0)
        val moduleInstance = moduleInstance(
            functionAddresses = mutableListOf(address),
        )
        val functionType = functionType()
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

        val threadExecutor: ThreadExecutor = { config ->
            Ok(listOf(i32(117)))
        }

        val actual = FunctionInvoker(
            config = config,
            store = store,
            address = address,
            values = locals,
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(listOf(i32(117))), actual)
    }
}
