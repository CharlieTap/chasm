package io.github.charlietap.chasm.tools.compilerbaseline

import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpCopiesDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.JumpDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.ThrowDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.control.UnreachableDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numeric.F32NegIDispatcher
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.instruction.OperandTransfer
import io.github.charlietap.chasm.runtime.instruction.TransferSource
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.RuntimeTypeMap
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DispatcherRendererTest {

    private val renderer = DispatcherRenderer(ModuleSymbols(ModuleInstance(RuntimeTypeMap.Empty), Store()))

    @Test
    fun `dispatchers are named after the factory that built them`() {
        assertEquals("JumpDispatcher", renderer.dispatcher(JumpDispatcher(targetIp = 0)))
        assertEquals("F32NegIDispatcher", renderer.dispatcher(F32NegIDispatcher(operand = 1f, destinationSlot = 0)))
    }

    @Test
    fun `factories taking value classes keep their source name`() {
        assertEquals("ThrowDispatcher", renderer.dispatcher(ThrowDispatcher(Address.Tag(0), firstPayloadSlot = 0)))
    }

    @Test
    fun `targets are offsets within the function`() {
        val fields = renderer.fields(JumpDispatcher(targetIp = 107), entryIp = 100)

        assertEquals("""{"target_ip":"@7"}""", fields.toString())
    }

    @Test
    fun `operand transfers render the moves they perform`() {
        val swap = OperandTransfer(arrayOf(TransferSource.Slot(1), TransferSource.Slot(0)), destinationSlotBase = 0)

        val fields = renderer.fields(JumpCopiesDispatcher(swap, destinationSlotBase = 0, targetIp = 0), entryIp = 0)

        assertEquals(
            """{"destination_slot_base":0,"operands":{"sources":[{"kind":"slot","slot":1},{"kind":"slot","slot":0}],""" +
                """"moves":["scratch = slot 1","+1 = slot 0","+0 = scratch"]},"target_ip":"@0"}""",
            fields.toString(),
        )
    }

    @Test
    fun `floating point values keep their bits`() {
        val fields = renderer.fields(F32NegIDispatcher(Float.fromBits(0x7fc00001), destinationSlot = 2), entryIp = 0)

        assertEquals("""{"destination_slot":2,"operand":"NaN (0x7fc00001)"}""", fields.toString())
    }

    @Test
    fun `a dispatcher that captures nothing has no fields`() {
        assertNull(renderer.fields(UnreachableDispatcher(), entryIp = 0))
    }
}
