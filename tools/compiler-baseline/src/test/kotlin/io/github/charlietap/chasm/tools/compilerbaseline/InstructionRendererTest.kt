package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericInstruction
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import kotlin.test.Test
import kotlin.test.assertEquals

class InstructionRendererTest {

    private val renderer = InstructionRenderer(ModuleSymbols(ModuleInstance(RuntimeTypeMap.Empty), Store()))

    @Test
    fun `targets are offsets within the function`() {
        val fields = renderer.immediates(AdminInstruction.Jump(targetIp = 107), entryIp = 100)

        assertEquals("""{"target_ip":"@7"}""", fields.toString())
    }

    @Test
    fun `operand transfers render the moves they perform`() {
        val swap = OperandTransfer(arrayOf(TransferSource.Slot(1), TransferSource.Slot(0)), destinationSlotBase = 0)

        val fields = renderer.immediates(AdminInstruction.JumpCopies(swap, destinationSlotBase = 0, targetIp = 0), entryIp = 0)

        assertEquals(
            """{"operands":{"sources":[{"kind":"slot","slot":1},{"kind":"slot","slot":0}],""" +
                """"moves":["scratch = slot 1","+1 = slot 0","+0 = scratch"]},"destination_slot_base":0,"target_ip":"@0"}""",
            fields.toString(),
        )
    }

    @Test
    fun `floating point values keep their bits`() {
        val fields = renderer.immediates(NumericInstruction.F32NegI(Float.fromBits(0x7fc00001), destinationSlot = 2), entryIp = 0)

        assertEquals("""{"operand":"NaN (0x7fc00001)","destination_slot":2}""", fields.toString())
    }
}
