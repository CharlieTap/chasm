package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.fixture.ir.instruction.catchAllRefHandler
import io.github.charlietap.chasm.fixture.ir.instruction.catchRefHandler
import io.github.charlietap.chasm.fixture.ir.module.labelIndex
import io.github.charlietap.chasm.fixture.ir.module.tagIndex
import io.github.charlietap.chasm.fixture.runtime.instance.exceptionInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tagAddress
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
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
    fun `writes matched catch payloads and returns the continuation address`() {
        val exceptionRef = ReferenceValue.Exception(Address.Exception(0)).toLong()
        val store = store(
            exceptions = mutableListOf(
                exceptionInstance(
                    tagAddress = tagAddress(0),
                    fields = longArrayOf(22L, 11L),
                ),
            ),
        )
        val cstack = cstack(
            frames = listOf(
                frame(
                    instance = moduleInstance(tagAddresses = mutableListOf(tagAddress(0))),
                ),
            ),
            handlers = listOf(
                ExceptionHandler(
                    handlers = listOf(catchRefHandler(tagIndex(0), labelIndex(0))),
                    payloadDestinationSlots = listOf(listOf(2, 3, 4)),
                    continuationIps = intArrayOf(42),
                    framesDepth = 1,
                    framePointer = 0,
                    valueDepth = 5,
                ),
            ),
        )
        val vstack = vstack().apply {
            reserveFrame(5)
            push(exceptionRef)
        }

        val continuationIp = ThrowRefExecutor(
            vstack = vstack,
            cstack = cstack,
            store = store,
            instruction = ControlInstruction.ThrowRef,
        )

        assertEquals(42, continuationIp)
        assertEquals(11L, vstack.getFrameSlot(2))
        assertEquals(22L, vstack.getFrameSlot(3))
        assertEquals(exceptionRef, vstack.getFrameSlot(4))
        assertEquals(0, cstack.handlersDepth())
    }

    @Test
    fun `continues through non-matching handlers without scheduling instructions`() {
        val exceptionRef = ReferenceValue.Exception(Address.Exception(0)).toLong()
        val store = store(
            exceptions = mutableListOf(exceptionInstance(tagAddress = tagAddress(0))),
        )
        val module = moduleInstance(tagAddresses = mutableListOf(tagAddress(1)))
        val cstack = cstack(
            frames = listOf(frame(instance = module)),
            handlers = listOf(
                ExceptionHandler(
                    handlers = listOf(catchAllRefHandler(labelIndex(0))),
                    payloadDestinationSlots = listOf(listOf(1)),
                    continuationIps = intArrayOf(73),
                    framesDepth = 1,
                    framePointer = 0,
                    valueDepth = 2,
                ),
                ExceptionHandler(
                    handlers = listOf(catchRefHandler(tagIndex(0), labelIndex(0))),
                    payloadDestinationSlots = listOf(listOf(1)),
                    continuationIps = intArrayOf(51),
                    framesDepth = 1,
                    framePointer = 0,
                    valueDepth = 8,
                ),
            ),
        )
        val vstack = vstack().apply { reserveDepth(12) }

        val continuationIp = ThrowRefValueExecutor(vstack, cstack, store, exceptionRef)

        assertEquals(73, continuationIp)
        assertEquals(exceptionRef, vstack.getFrameSlot(1))
        assertEquals(2, vstack.depth())
        assertEquals(0, cstack.handlersDepth())
    }
}
