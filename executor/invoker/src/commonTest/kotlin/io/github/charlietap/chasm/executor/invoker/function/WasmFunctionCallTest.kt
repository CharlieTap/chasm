package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.fixture.runtime.execution.executionContext
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.runtime.stack.NO_RESULT_SLOT_BASE
import kotlin.test.Test
import kotlin.test.assertEquals

class WasmFunctionCallTest {

    @Test
    fun `call enters the function and records its return address`() {
        val module = moduleInstance()
        val function = wasmFunctionInstance(
            module = module,
            functionType = functionType(
                params = resultType(listOf(i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
            function = runtimeFunction(
                locals = longArrayOf(0),
                body = runtimeExpression(entryIp = 37),
                frameSlots = 2,
            ),
        )
        val vstack = vstack().apply { push(11L) }
        val cstack = cstack()
        val store = store()

        val entryIp = WasmFunctionCall(
            vstack,
            cstack,
            store,
            executionContext(cstack, vstack, store, module),
            function,
            returnIp = 19,
        )

        assertEquals(37, entryIp)
        assertEquals(19, cstack.peekFrame().returnIp)
        assertEquals(NO_RESULT_SLOT_BASE, cstack.peekFrame().resultSlotBase)
        assertEquals(0, vstack.framePointer)
        assertEquals(2, vstack.depth())
        assertEquals(11L, vstack.getFrameSlot(0))
        assertEquals(0L, vstack.getFrameSlot(1))
    }

    @Test
    fun `tail call reuses the current activation frame`() {
        val callerModule = moduleInstance()
        val calleeModule = moduleInstance()
        val function = wasmFunctionInstance(
            module = calleeModule,
            functionType = functionType(params = resultType(listOf(i32ValueType()))),
            function = runtimeFunction(body = runtimeExpression(53), frameSlots = 1),
        )
        val cstack = cstack(
            frames = listOf(
                frame(
                    instance = callerModule,
                    valueDepth = 0,
                    returnIp = 71,
                ),
            ),
        )
        val vstack = vstack().apply { push(29L) }
        val store = store()

        val entryIp = ReturnWasmFunctionCall(
            vstack,
            cstack,
            store,
            executionContext(cstack, vstack, store, callerModule),
            function,
        )

        assertEquals(53, entryIp)
        assertEquals(1, cstack.framesDepth())
        assertEquals(71, cstack.peekFrame().returnIp)
        assertEquals(calleeModule, cstack.peekFrame().instance)
        assertEquals(29L, vstack.getFrameSlot(0))
    }
}
