package io.github.charlietap.chasm.executor.invoker.function

import io.github.charlietap.chasm.fixture.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.wasmFunctionInstance
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import io.github.charlietap.chasm.runtime.stack.activationHeader
import kotlin.test.Test
import kotlin.test.assertEquals

class WasmFunctionCallTest {

    @Test
    fun `call activates a compiled frame and writes its activation header`() {
        val function = wasmFunction(
            params = 1,
            results = 1,
            locals = longArrayOf(0),
            entryIp = 37,
            frameSlots = 3,
        )
        val vstack = vstack().apply {
            reserveDepth(4)
            fp = 1
            setFrameSlot(0, 11)
        }

        val entryIp = WasmFunctionCall(
            vstack = vstack,
            strategy = function.callStrategy,
            operands = OperandTransfer(arrayOf(TransferSource.Slot(0)), destinationSlotBase = 3),
            callFrameOffset = 3,
            activationHeader = activationHeader(returnIp = 19, callerFrameDelta = 3),
        )

        assertEquals(37, entryIp)
        assertEquals(4, vstack.fp)
        assertEquals(7, vstack.sp)
        assertEquals(11L, vstack.getFrameSlot(0))
        assertEquals(0L, vstack.getFrameSlot(2))
        assertEquals(19, vstack.restoreCallerFrame(resultCount = 1, activationHeaderSlot = 1))
        assertEquals(1, vstack.fp)
    }

    @Test
    fun `tail call preserves the activation header while reusing the frame`() {
        val function = wasmFunction(params = 1, entryIp = 53, frameSlots = 2)
        val vstack = vstack().apply {
            reserveDepth(3)
            writeActivationHeader(
                calleeFp = 1,
                activationHeaderSlot = 0,
                activationHeader = activationHeader(71, 1),
            )
            activateFrame(fp = 1, frameSlots = 2)
            setFrameSlot(1, 29)
        }

        val entryIp = ReturnWasmFunctionCall(
            vstack = vstack,
            strategy = function.callStrategy,
            operands = OperandTransfer(
                arrayOf(TransferSource.Slot(1)),
                destinationSlotBase = 0,
            ),
            callerActivationHeaderSlot = 0,
        )

        assertEquals(53, entryIp)
        assertEquals(1, vstack.fp)
        assertEquals(29L, vstack.getFrameSlot(0))
        assertEquals(71, vstack.restoreCallerFrame(resultCount = 0, activationHeaderSlot = 1))
    }

    @Test
    fun `tail call stages cyclic operand moves in the reused frame`() {
        val function = wasmFunction(params = 2, entryIp = 53, frameSlots = 3)
        val vstack = vstack().apply {
            reserveDepth(4)
            writeActivationHeader(
                calleeFp = 1,
                activationHeaderSlot = 2,
                activationHeader = activationHeader(71, 1),
            )
            activateFrame(fp = 1, frameSlots = 3)
            setFrameSlot(0, 11)
            setFrameSlot(1, 22)
        }

        ReturnWasmFunctionCall(
            vstack = vstack,
            strategy = function.callStrategy,
            operands = OperandTransfer(
                arrayOf(TransferSource.Slot(1), TransferSource.Slot(0)),
                destinationSlotBase = 0,
            ),
            callerActivationHeaderSlot = 2,
        )

        assertEquals(1, vstack.fp)
        assertEquals(22L, vstack.getFrameSlot(0))
        assertEquals(11L, vstack.getFrameSlot(1))
        assertEquals(71, vstack.restoreCallerFrame(resultCount = 0, activationHeaderSlot = 2))
    }

    private fun wasmFunction(
        params: Int = 0,
        results: Int = 0,
        locals: LongArray = longArrayOf(),
        entryIp: Int,
        frameSlots: Int,
    ) = wasmFunctionInstance(
        module = moduleInstance(),
        functionType = functionType(
            params = resultType(List(params) { i32ValueType() }),
            results = resultType(List(results) { i32ValueType() }),
        ),
        function = runtimeFunction(
            locals = locals,
            body = runtimeExpression(entryIp),
            frameSlots = frameSlots,
        ),
    )
}
