package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.ir.instruction.catchAllRefHandler
import io.github.charlietap.chasm.fixture.ir.instruction.catchRefHandler
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.ir.module.tagIndex
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.instance.exceptionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tagAddress
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.label
import io.github.charlietap.chasm.fixture.runtime.stack.stackDepths
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.ext.toLong
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import io.github.charlietap.chasm.runtime.value.ReferenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ThrowRefExecutorTest {

    @Test
    fun `can write matched catch payloads into frame slots`() {
        val exceptionRef = ReferenceValue.Exception(Address.Exception(0)).toLong()
        val store = store(
            exceptions = mutableListOf(
                exceptionInstance(
                    tagAddress = tagAddress(0),
                    // Stored in throw order, so ThrowRef reverses this back to 11, 22.
                    fields = longArrayOf(22L, 11L),
                ),
            ),
        )
        val cstack = cstack(
            frames = listOf(
                frame(
                    instance = moduleInstance(
                        tagAddresses = mutableListOf(tagAddress(0)),
                    ),
                    frameSlotMode = true,
                ),
            ),
            handlers = listOf(
                ExceptionHandler(
                    instructions = listOf(
                        catchRefHandler(
                            tagIndex = tagIndex(0),
                            labelIndex = labelIndex(0),
                        ),
                    ),
                    payloadDestinationSlots = listOf(listOf(2, 3, 4)),
                    framesDepth = 1,
                    instructionsDepth = 0,
                    labelsDepth = 1,
                    framePointer = 0,
                ),
            ),
            labels = listOf(
                label(
                    arity = 3,
                    depths = stackDepths(values = 0),
                ),
            ),
        )
        val vstack = vstack().apply {
            reserveFrame(5)
            push(exceptionRef)
        }
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        ThrowRefExecutor(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instruction = ControlInstruction.ThrowRef,
        )

        assertEquals(5, vstack.depth())
        assertEquals(11L, vstack.getFrameSlot(2))
        assertEquals(22L, vstack.getFrameSlot(3))
        assertEquals(exceptionRef, vstack.getFrameSlot(4))
        assertEquals(0, cstack.handlersDepth())
        assertEquals(1, cstack.labelsDepth())
        assertEquals(1, cstack.instructionsDepth())
    }

    @Test
    fun `can continue to a patched handler continuation without labels`() {
        val exceptionRef = ReferenceValue.Exception(Address.Exception(0)).toLong()
        val continuation = dispatchableInstruction { vstack, _, _, _ ->
            vstack.setFrameSlot(3, 99L)
            Ok(Unit)
        }
        val store = store(
            exceptions = mutableListOf(
                exceptionInstance(
                    tagAddress = tagAddress(0),
                    fields = longArrayOf(),
                ),
            ),
        )
        val cstack = cstack(
            frames = listOf(
                frame(
                    instance = moduleInstance(),
                    frameSlotMode = true,
                ),
            ),
            handlers = listOf(
                ExceptionHandler(
                    instructions = listOf(
                        catchAllRefHandler(labelIndex = labelIndex(0)),
                    ),
                    payloadDestinationSlots = listOf(listOf(2)),
                    continuations = listOf(arrayOf(continuation)),
                    framesDepth = 1,
                    instructionsDepth = 0,
                    labelsDepth = 0,
                    framePointer = 0,
                ),
            ),
        )
        val vstack = vstack().apply {
            reserveFrame(4)
            push(exceptionRef)
        }
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        ThrowRefExecutor(
            vstack = vstack,
            cstack = cstack,
            store = store,
            context = context,
            instruction = ControlInstruction.ThrowRef,
        )

        assertEquals(exceptionRef, vstack.getFrameSlot(2))
        assertEquals(0, cstack.handlersDepth())
        assertEquals(1, cstack.instructionsDepth())

        cstack.popInstruction()(vstack, cstack, store, context)

        assertEquals(99L, vstack.getFrameSlot(3))
    }
}
