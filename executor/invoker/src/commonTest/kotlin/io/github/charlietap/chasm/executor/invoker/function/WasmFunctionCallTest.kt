package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.label
import io.github.charlietap.chasm.fixture.runtime.stack.stackDepths
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.nullReferenceValue
import io.github.charlietap.chasm.fixture.type.functionHeapType
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class WasmFunctionCallTest {

    @Test
    fun `can execute a function call and return a result`() {

        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        val functionType = functionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
            results = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
        )

        val function = runtimeFunction(
            locals = longArrayOf(
                nullReferenceValue(
                    heapType = functionHeapType(),
                ).toLong(),
            ),
            body = runtimeExpression(
                arrayOf(
                    dispatchableInstruction(),
                    dispatchableInstruction(),
                ),
            ),
        )

        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            function = function,
        )

        vstack.pushI32(1)
        vstack.pushI32(2)

        val actual = WasmFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instance = functionInstance,
        )

        assertEquals(Unit, actual)
        assertEquals(1, cstack.framesDepth())
    }

    @Test
    fun `reserves the full frame size on function call`() {

        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        val functionType = functionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
        )

        val function = runtimeFunction(
            locals = longArrayOf(0L),
            frameSlots = 6,
            body = runtimeExpression(
                arrayOf(
                    dispatchableInstruction(),
                ),
            ),
        )

        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            function = function,
        )

        vstack.pushI32(11)
        vstack.pushI64(22)

        WasmFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instance = functionInstance,
        )

        assertEquals(0, vstack.framePointer)
        assertEquals(6, vstack.depth())
        assertEquals(11L, vstack.getFrameSlot(0))
        assertEquals(22L, vstack.getFrameSlot(1))
        assertEquals(0L, vstack.getFrameSlot(2))
    }

    @Test
    fun `does not synthesize a visible result base for legacy calls`() {

        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        val functionType = functionType(
            params = resultType(
                listOf(i32ValueType()),
            ),
            results = resultType(
                listOf(i64ValueType()),
            ),
        )

        val function = runtimeFunction(
            frameSlots = 1,
            frameSlotMode = true,
            returnSlots = listOf(0),
            body = runtimeExpression(
                arrayOf(
                    dispatchableInstruction(),
                ),
            ),
        )

        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            function = function,
        )

        vstack.pushI32(7)

        WasmFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instance = functionInstance,
        )

        assertEquals(null, cstack.peekFrame().visibleResultBase)
    }

    @Test
    fun `can execute a strict frame slot call without pushing operands`() {

        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        val functionType = functionType(
            params = resultType(
                listOf(i32ValueType()),
            ),
            results = resultType(
                listOf(i32ValueType()),
            ),
        )

        val function = runtimeFunction(
            locals = longArrayOf(9L),
            frameSlots = 3,
            frameSlotMode = true,
            returnSlots = listOf(0),
            body = runtimeExpression(
                arrayOf(
                    dispatchableInstruction(),
                ),
            ),
        )

        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            function = function,
        )

        vstack.reserveFrame(3)
        vstack.setFrameSlot(0, 7L)
        vstack.setFrameSlot(1, 8L)
        vstack.setFrameSlot(2, 7L)
        cstack.push(
            frame(
                instance = functionInstance.module,
                frameSlotMode = true,
            ),
        )

        WasmFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instance = functionInstance,
            resultSlots = listOf(2),
            callFrameSlot = 2,
        )

        assertEquals(2, cstack.framesDepth())
        assertEquals(2, vstack.framePointer)
        assertEquals(5, vstack.depth())
        assertEquals(7L, vstack.getFrameSlot(2, 0))
        assertEquals(9L, vstack.getFrameSlot(2, 1))
        assertEquals(2, cstack.peekFrame().visibleResultBase)
    }

    @Test
    fun `normalizes strict call depth when callee frame reuses caller reserve`() {
        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        val functionType = functionType(
            params = resultType(emptyList()),
            results = resultType(emptyList()),
        )

        val function = runtimeFunction(
            frameSlots = 2,
            frameSlotMode = true,
            body = runtimeExpression(
                arrayOf(
                    dispatchableInstruction(),
                ),
            ),
        )

        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            function = function,
        )

        vstack.reserveFrame(5)
        cstack.push(
            frame(
                instance = functionInstance.module,
                frameSlotMode = true,
            ),
        )

        WasmFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instance = functionInstance,
            resultSlots = emptyList<Int>(),
            callFrameSlot = 1,
        )

        assertEquals(2, cstack.framesDepth())
        assertEquals(1, vstack.framePointer)
        assertEquals(3, vstack.depth())
    }

    @Test
    fun `can execute a tail recursive function call and return a result`() {

        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        val functionType = functionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
            results = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
        )

        val function = runtimeFunction(
            locals = longArrayOf(),
            body = runtimeExpression(
                arrayOf(
                    dispatchableInstruction(),
                    dispatchableInstruction(),
                ),
            ),
        )

        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            function = function,
        )

        vstack.pushI32(1)
        vstack.pushI32(2)

        val frame = frame(
            arity = functionType.results.types.size,
            instance = functionInstance.module,
        )

        cstack.push(frame)
        cstack.push(
            dispatchableInstruction(),
        )

        val actual = ReturnWasmFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instance = functionInstance,
        )

        assertEquals(Unit, actual)
        assertEquals(1, cstack.framesDepth())
    }

    @Test
    fun `reserves the full frame size on tail call`() {

        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        val functionType = functionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
        )

        val function = runtimeFunction(
            locals = longArrayOf(0L),
            frameSlots = 5,
            frameSlotMode = true,
            body = runtimeExpression(
                arrayOf(
                    dispatchableInstruction(),
                ),
            ),
        )

        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            function = function,
        )

        vstack.pushI32(7)
        vstack.pushI64(8)

        val frame = frame(
            arity = functionType.results.types.size,
            instance = functionInstance.module,
            frameSlotMode = false,
        )

        cstack.push(frame)
        cstack.push(
            dispatchableInstruction(),
        )

        ReturnWasmFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instance = functionInstance,
        )

        assertEquals(0, vstack.framePointer)
        assertEquals(5, vstack.depth())
        assertEquals(7L, vstack.getFrameSlot(0))
        assertEquals(8L, vstack.getFrameSlot(1))
        assertEquals(0L, vstack.getFrameSlot(2))
        assertEquals(true, cstack.peekFrame().frameSlotMode)
    }

    @Test
    fun `reuses the current visible result region on tail call`() {
        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        val functionType = functionType(
            params = resultType(emptyList()),
            results = resultType(
                listOf(i32ValueType()),
            ),
        )

        val function = runtimeFunction(
            frameSlots = 1,
            frameSlotMode = true,
            returnSlots = listOf(0),
            body = runtimeExpression(
                arrayOf(
                    dispatchableInstruction(),
                ),
            ),
        )

        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            function = function,
        )

        vstack.reserveFrame(3)
        vstack.framePointer = 2
        vstack.reserveFrame(1)

        cstack.push(
            frame(
                arity = 1,
                depths = stackDepths(values = 3),
                previousFramePointer = 0,
                instance = functionInstance.module,
                frameSlotMode = true,
                visibleResultBase = 2,
            ),
        )
        cstack.push(
            dispatchableInstruction(),
        )

        ReturnWasmFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instance = functionInstance,
        )

        assertEquals(2, cstack.peekFrame().visibleResultBase)
    }

    @Test
    fun `can execute a strict tail call without pushing operands`() {
        val store = store()
        val cstack = cstack(
            labels = listOf(label()),
        )
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        val functionType = functionType(
            params = resultType(
                listOf(i32ValueType()),
            ),
            results = resultType(
                listOf(i32ValueType()),
            ),
        )

        val function = runtimeFunction(
            locals = longArrayOf(9L),
            frameSlots = 3,
            frameSlotMode = true,
            returnSlots = listOf(0),
            body = runtimeExpression(
                arrayOf(
                    dispatchableInstruction(),
                ),
            ),
        )

        val functionInstance = wasmFunctionInstance(
            functionType = functionType,
            function = function,
        )

        vstack.reserveFrame(2)
        vstack.framePointer = 2
        vstack.reserveFrame(2)
        vstack.setFrameSlot(0, 7L)

        cstack.push(
            frame(
                arity = 1,
                depths = stackDepths(values = 2),
                previousFramePointer = 0,
                instance = functionInstance.module,
                frameSlotMode = true,
                visibleResultBase = 1,
            ),
        )
        cstack.push(
            dispatchableInstruction(),
        )

        ReturnWasmFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instance = functionInstance,
            operands = listOf(
                ControlSuperInstruction.CallOperand.Slot(0),
            ),
        )

        assertEquals(1, cstack.framesDepth())
        assertEquals(1, cstack.labelsDepth())
        assertEquals(2, vstack.framePointer)
        assertEquals(5, vstack.depth())
        assertEquals(7L, vstack.getFrameSlot(2, 0))
        assertEquals(9L, vstack.getFrameSlot(2, 1))
        assertEquals(1, cstack.peekFrame().visibleResultBase)
    }
}
