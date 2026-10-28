package io.github.charlietap.chasm.executor.invoker

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.getError
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.executor.invoker.compiled.CompiledFunctionInstallation
import io.github.charlietap.chasm.executor.invoker.compiled.callFunction
import io.github.charlietap.chasm.executor.invoker.compiled.installCompiledFunctions
import io.github.charlietap.chasm.executor.invoker.compiled.requestTailCall
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.gc.GuestHeapOutOfMemoryException
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.function.CompiledFunction
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.program.Program
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.value.NumberValue.I32
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

class CompiledFunctionInvokerTest {

    @Test
    fun `a compiled function runs through its compiled entry alone`() {
        val store = store(program = Program())
        val function = compiledFunction(store) { _, frame, fp -> frame[fp] = frame[fp] + 1 }

        val actual = CompiledFunctionInvoker(runtimeConfig(), store, function.module, function, listOf(I32(41)))

        assertEquals(Ok(listOf(I32(42))), actual)
    }

    @Test
    fun `an uncompiled function is rejected before any of it runs`() {
        val program = Program()
        var ran = false
        val entryIp = program.append(
            DispatchableInstruction { _, _, nextIp ->
                ran = true
                nextIp
            },
        )
        val store = store(program = program)
        val function = wasmFunctionInstance(
            functionType = I32_TO_I32,
            function = runtimeFunction(body = runtimeExpression(entryIp), frameSlots = 2),
        )

        val actual = CompiledFunctionInvoker(runtimeConfig(), store, function.module, function, listOf(I32(41)))

        assertEquals(Err(InvocationError.InterpreterUnavailable), actual)
        assertFalse(ran)
    }

    @Test
    fun `a call from compiled code to interpreted code fails without an interpreter`() {
        val store = store(program = Program())
        val interpreted = interpretedFunction(store)
        val function = compiledFunction(store) { context, frame, fp ->
            val values = longArrayOf(frame[fp])
            callFunction(context, context.instance, interpreted, values, 0)
            frame[fp] = values[0]
        }

        val actual = CompiledFunctionInvoker(runtimeConfig(), store, function.module, function, listOf(I32(41)))

        assertEquals(Err(InvocationError.InterpreterUnavailable), actual)
    }

    @Test
    fun `a tail call from compiled code to interpreted code fails without an interpreter`() {
        val store = store(program = Program())
        val interpreted = interpretedFunction(store)
        val function = compiledFunction(store) { context, frame, fp ->
            requestTailCall(context, context.instance, interpreted, longArrayOf(frame[fp]))
        }

        val actual = CompiledFunctionInvoker(runtimeConfig(), store, function.module, function, listOf(I32(41)))

        assertEquals(Err(InvocationError.InterpreterUnavailable), actual)
    }

    @Test
    fun `running out of guest heap traps with a snapshot when debugging`() {
        val store = store(program = Program())
        val function = compiledFunction(store) { _, _, _ -> throw GuestHeapOutOfMemoryException("full") }

        val actual = CompiledFunctionInvoker(runtimeConfig(debugInfo = true), store, function.module, function, listOf(I32(41)))

        val trapped = assertIs<InvocationError.Trapped>(actual.getError())
        assertEquals(InvocationError.GuestHeapOutOfMemory, trapped.error)
    }

    @Test
    fun `an interrupt requested before the outermost call is cleared and the depth restored`() {
        val store = store(program = Program(), config = StoreConfig(interruptible = true))
        store.interrupt.requested = true
        var seen: Pair<Boolean, Int>? = null
        val function = compiledFunction(store) { context, frame, fp ->
            seen = context.store.interrupt.requested to context.store.interrupt.depth
            frame[fp] = frame[fp] + 1
        }

        val actual = CompiledFunctionInvoker(runtimeConfig(), store, function.module, function, listOf(I32(41)))

        assertEquals(Ok(listOf(I32(42))), actual)
        assertEquals(false to 1, seen)
        assertEquals(0, store.interrupt.depth)
    }

    private fun compiledFunction(
        store: Store,
        body: (ExecutionContext, LongArray, Int) -> Unit,
    ): FunctionInstance.WasmFunction {
        val function = wasmFunctionInstance(
            functionType = I32_TO_I32,
            module = moduleInstance(),
            function = runtimeFunction(body = runtimeExpression(UNINSTALLED)),
        )
        installCompiledFunctions(store, listOf(CompiledFunctionInstallation(function, FakeCompiledFunction(body))))
        return function
    }

    private fun interpretedFunction(
        store: Store,
    ): FunctionInstance.WasmFunction = wasmFunctionInstance(
        functionType = I32_TO_I32,
        function = runtimeFunction(body = runtimeExpression(store.program.append(DispatchableInstruction { _, _, _ -> error("interpreted") })), frameSlots = 2),
    )

    private class FakeCompiledFunction(
        private val body: (ExecutionContext, LongArray, Int) -> Unit,
    ) : CompiledFunction {
        override fun invoke(
            context: ExecutionContext,
            frame: LongArray,
            fp: Int,
        ) = body(context, frame, fp)

        override fun call(
            context: ExecutionContext,
            values: LongArray,
            depth: Int,
        ) = error("only entered from the root here")
    }

    private companion object {
        const val UNINSTALLED = -1
        val I32_TO_I32 = functionType(
            params = resultType(listOf(i32ValueType())),
            results = resultType(listOf(i32ValueType())),
        )
    }
}
