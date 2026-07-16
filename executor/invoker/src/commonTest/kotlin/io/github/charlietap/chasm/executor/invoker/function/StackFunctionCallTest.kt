package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.stackFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.label
import io.github.charlietap.chasm.fixture.runtime.stack.stackDepths
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.runtime.function.StackFunctionBody
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class StackFunctionCallTest {

    @Test
    fun `can execute a stack function call and return results`() {
        val store = store()
        val cstack = cstack(
            frames = listOf(frame(instance = moduleInstance())),
        )
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
                    i64ValueType(),
                    i32ValueType(),
                ),
            ),
        )
        var actualParams = emptyList<Long>()
        val body = StackFunctionBody { stack, _, _, _ ->
            actualParams = listOf(
                stack.getFrameSlot(0),
                stack.getFrameSlot(1),
            )
            stack.setFrameSlot(0, 117L)
            stack.setFrameSlot(1, 118L)
        }
        val function = stackFunctionInstance(
            functionType = functionType,
            body = body,
        )

        vstack.pushI32(115)
        vstack.pushI64(116)

        val actual = StackFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            function = function,
        )

        assertEquals(Unit, actual)
        assertEquals(listOf(115L, 116L), actualParams)
        assertEquals(2, vstack.depth())
        assertEquals(118L, vstack.pop())
        assertEquals(117L, vstack.pop())
    }

    @Test
    fun `can execute a strict stack function call through interface slots`() {
        val store = store()
        val cstack = cstack(
            frames = listOf(
                frame(
                    instance = moduleInstance(),
                    frameSlotMode = true,
                ),
            ),
        )
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )
        val body = StackFunctionBody { stack, _, _, _ ->
            stack.setFrameSlot(0, stack.getFrameSlot(0) + 1)
        }
        val function = stackFunctionInstance(
            functionType = functionType(
                params = resultType(listOf(i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
            body = body,
        )

        vstack.reserveFrame(2)
        vstack.setFrameSlot(0, 11L)
        vstack.setFrameSlot(1, 41L)

        val actual = StackFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            function = function,
            resultSlots = listOf(0),
            callFrameSlot = 1,
        )

        assertEquals(Unit, actual)
        assertEquals(0, vstack.framePointer)
        assertEquals(2, vstack.depth())
        assertEquals(42L, vstack.getFrameSlot(0))
        assertEquals(42L, vstack.getFrameSlot(1))
    }

    @Test
    fun `can execute a strict tail stack function call through the visible result region`() {
        val store = store()
        val cstack = cstack(
            frames = listOf(
                frame(
                    arity = 1,
                    depths = stackDepths(values = 2),
                    previousFramePointer = 0,
                    instance = moduleInstance(),
                    frameSlotMode = true,
                    visibleResultBase = 1,
                ),
            ),
            labels = listOf(label()),
        )
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )
        val body = StackFunctionBody { stack, _, _, _ ->
            stack.setFrameSlot(0, stack.getFrameSlot(0) + 1)
        }
        val function = stackFunctionInstance(
            functionType = functionType(
                params = resultType(listOf(i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
            body = body,
        )

        vstack.reserveFrame(5)
        vstack.setFrameSlot(0, 41L)
        vstack.setFrameSlot(4, 123L)

        val actual = ReturnStackFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            function = function,
            operands = listOf(
                ControlSuperInstruction.CallOperand.Slot(0),
            ),
        )

        assertEquals(Unit, actual)
        assertEquals(0, cstack.framesDepth())
        assertEquals(0, vstack.framePointer)
        assertEquals(2, vstack.depth())
        assertEquals(42L, vstack.getFrameSlot(1))
        assertEquals(123L, vstack.getFrameSlot(4))
    }

    @Test
    fun `a tail stack function call removes the caller frame before executing`() {
        val store = store()
        val cstack = cstack(
            frames = listOf(
                frame(
                    arity = 1,
                    depths = stackDepths(values = 1),
                    previousFramePointer = 0,
                    instance = moduleInstance(),
                ),
            ),
            labels = listOf(label()),
        )
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )
        var bodyFrames = -1
        val body = StackFunctionBody { stack, control, _, _ ->
            bodyFrames = control.framesDepth()
            stack.setFrameSlot(0, stack.getFrameSlot(0) + 1)
        }
        val function = stackFunctionInstance(
            functionType = functionType(
                params = resultType(listOf(i32ValueType())),
                results = resultType(listOf(i32ValueType())),
            ),
            body = body,
        )
        vstack.pushI32(99)
        vstack.pushI32(41)

        ReturnStackFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            function = function,
        )
        val actual = listOf(bodyFrames.toLong(), cstack.framesDepth().toLong(), vstack.pop(), vstack.pop())

        val expected = listOf(0L, 0L, 42L, 99L)
        assertEquals(expected, actual)
    }
}
