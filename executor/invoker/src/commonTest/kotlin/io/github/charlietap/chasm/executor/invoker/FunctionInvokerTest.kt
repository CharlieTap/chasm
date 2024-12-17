package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.thread.ThreadExecutor
import io.github.charlietap.chasm.executor.runtime.error.InvocationError
import io.github.charlietap.chasm.executor.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.executor.runtime.value.ExecutionValue
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.frame
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instruction.wasmFunctionCallRuntimeInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.returnArity
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.thread
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class FunctionInvokerTest {

    @Test
    fun `can invoke a function and return a result`() {

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
        val store = store(
            functions = mutableListOf(functionInstance),
        )

        val callDispatchable = dispatchableInstruction()
        val callDispatcher: Dispatcher<ControlInstruction.WasmFunctionCall> = { instruction ->
            assertEquals(wasmFunctionCallRuntimeInstruction(functionInstance), instruction)
            callDispatchable
        }

        val thread = thread(
            frame(
                arity = returnArity(functionType.results.types.size),
                locals = locals,
                instance = moduleInstance,
            ),
            arrayOf(
                callDispatchable,
            ),
        )

        val threadExecutor: ThreadExecutor = { config ->
            Ok(listOf(i32(117)))
        }

        val actual = FunctionInvoker(
            store = store,
            address = address,
            values = locals,
            callDispatcher = callDispatcher,
            threadExecutor = threadExecutor,
        )

        assertEquals(Ok(listOf(i32(117))), actual)
    }

    @Test
    fun `throws error if address does not exist`() {

        val locals = mutableListOf<ExecutionValue>(i32(117))
        val address = functionAddress(0)
        val moduleInstance = moduleInstance()
        val functionType = functionType()
        val definedType = functionType.definedType()
        val function = runtimeFunction()
        val functionInstance = wasmFunctionInstance(
            definedType,
            functionType,
            moduleInstance,
            function,
        )

        val store = store(
            functions = mutableListOf(functionInstance),
        )

        val callDispatcher: Dispatcher<ControlInstruction.WasmFunctionCall> = { _ ->
            fail("call dispatcher should not be called")
        }
        val threadExecutor: ThreadExecutor = { _ ->
            fail("thread executor should not be called")
        }

        val actual = FunctionInvoker(
            store = store,
            address = address,
            values = locals,
            callDispatcher = callDispatcher,
            threadExecutor = threadExecutor,
        )

        assertEquals(Err(InvocationError.InvalidAddress), actual)
    }
}
