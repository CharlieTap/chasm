package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.runtime.instance.hostFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.label
import io.github.charlietap.chasm.fixture.runtime.stack.stackDepths
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.value.i32
import io.github.charlietap.chasm.fixture.runtime.value.i64
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.runtime.instance.HostFunction
import io.github.charlietap.chasm.runtime.instruction.FusedControlInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class HostFunctionCallTest {

    @Test
    fun `can execute a host function call and return a result`() {

        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )
        val frame = frame(
            instance = moduleInstance(),
        )

        cstack.push(frame)

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

        val hostFunction: HostFunction = {
            listOf(
                i32(117),
                i64(118),
            )
        }

        val functionInstance = hostFunctionInstance(
            functionType = functionType,
            function = hostFunction,
        )

        vstack.pushI32(115)
        vstack.pushI64(116)

        val actual = HostFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            function = functionInstance,
        )

        assertEquals(Unit, actual)
        assertEquals(1, cstack.framesDepth())
        assertEquals(2, vstack.depth())
        assertEquals(118, vstack.pop())
        assertEquals(117, vstack.pop())
    }

    @Test
    fun `can execute a strict host call into shared interface slots`() {
        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        cstack.push(
            frame(
                instance = moduleInstance(),
                frameSlotMode = true,
            ),
        )

        val functionInstance = hostFunctionInstance(
            functionType = functionType(
                params = resultType(
                    listOf(i32ValueType()),
                ),
                results = resultType(
                    listOf(i32ValueType()),
                ),
            ),
            function = { listOf(i32(77)) },
        )

        vstack.reserveFrame(2)
        vstack.setFrameSlot(0, 41L)
        vstack.setFrameSlot(1, 41L)

        HostFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            function = functionInstance,
            resultSlots = listOf(1),
            callFrameSlot = 1,
        )

        assertEquals(41L, vstack.getFrameSlot(0))
        assertEquals(77L, vstack.getFrameSlot(1))
        assertEquals(2, vstack.depth())
    }

    @Test
    fun `can execute a strict tail host call through the current visible result region`() {
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

        val functionInstance = hostFunctionInstance(
            functionType = functionType(
                params = resultType(
                    listOf(i32ValueType()),
                ),
                results = resultType(
                    listOf(i32ValueType()),
                ),
            ),
            function = { listOf(i32(77)) },
        )

        vstack.reserveFrame(5)
        vstack.setFrameSlot(0, 41L)
        vstack.setFrameSlot(4, 123L)

        cstack.push(
            frame(
                arity = 1,
                depths = stackDepths(values = 2),
                previousFramePointer = 0,
                instance = moduleInstance(),
                frameSlotMode = true,
                visibleResultBase = 1,
            ),
        )

        ReturnHostFunctionCall(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            function = functionInstance,
            operands = listOf(
                FusedControlInstruction.CallOperand.Slot(0),
            ),
        )

        assertEquals(0, cstack.framesDepth())
        assertEquals(0, vstack.framePointer)
        assertEquals(2, vstack.depth())
        assertEquals(77L, vstack.getFrameSlot(1))
        assertEquals(123L, vstack.getFrameSlot(4))
    }
}
