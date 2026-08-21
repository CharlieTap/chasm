package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.ast.instruction.catchAllRefHandler
import io.github.charlietap.chasm.fixture.ast.instruction.catchCatchHandler
import io.github.charlietap.chasm.fixture.ast.instruction.catchRefHandler
import io.github.charlietap.chasm.fixture.ast.module.labelIndex
import io.github.charlietap.chasm.fixture.ast.module.tagIndex
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.instance.tagAddress
import io.github.charlietap.chasm.fixture.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.runtime.stack.frame
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.type.rtt
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tagType
import io.github.charlietap.chasm.runtime.exception.ExceptionHandler
import io.github.charlietap.chasm.runtime.instruction.ControlInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class ThrowRefExecutorTest {

    @Test
    fun `throw allocates semantic order payload and catches it directly`() {
        val store = store()
        val exceptionTagAddress = store.heap.registerTag(
            rtt(),
            tagType(
                functionType = functionType(
                    params = resultType(listOf(i64ValueType(), i64ValueType())),
                ),
            ),
        )
        val cstack = cstack(
            frames = listOf(
                frame(instance = moduleInstance(tagAddresses = mutableListOf(exceptionTagAddress))),
            ),
            handlers = listOf(
                ExceptionHandler(
                    handlers = listOf(catchCatchHandler(tagIndex(0u), labelIndex(0u))),
                    payloadDestinationSlots = listOf(intArrayOf(1, 2)),
                    continuationIps = intArrayOf(37),
                    framesDepth = 1,
                    framePointer = 0,
                    valueDepth = 4,
                ),
            ),
        )
        val vstack = vstack().apply {
            reserveFrame(4)
            push(11)
            push(22)
        }

        val continuationIp = ThrowExecutor(
            vstack,
            cstack,
            store,
            executionContext(store = store, vstack = vstack, cstack = cstack),
            ControlInstruction.Throw(tagIndex(0u)),
        )

        assertEquals(37, continuationIp)
        assertEquals(11, vstack.getFrameSlot(1))
        assertEquals(22, vstack.getFrameSlot(2))
    }

    @Test
    fun `writes matched catch payloads and returns the continuation address`() {
        val store = store()
        val exceptionTagAddress = store.heap.registerTag(
            rtt(),
            tagType(
                functionType = functionType(
                    params = resultType(listOf(i64ValueType(), i64ValueType())),
                ),
            ),
        )
        val exceptionRef = store.heap.allocateException(exceptionTagAddress, longArrayOf(11L, 22L))
        val cstack = cstack(
            frames = listOf(
                frame(
                    instance = moduleInstance(tagAddresses = mutableListOf(tagAddress(0))),
                ),
            ),
            handlers = listOf(
                ExceptionHandler(
                    handlers = listOf(catchRefHandler(tagIndex(0u), labelIndex(0u))),
                    payloadDestinationSlots = listOf(intArrayOf(2, 3, 4)),
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
        val store = store()
        val exceptionTagAddress = store.heap.registerTag(rtt(), tagType())
        val exceptionRef = store.heap.allocateException(exceptionTagAddress, longArrayOf())
        val module = moduleInstance(tagAddresses = mutableListOf(tagAddress(1)))
        val cstack = cstack(
            frames = listOf(frame(instance = module)),
            handlers = listOf(
                ExceptionHandler(
                    handlers = listOf(catchAllRefHandler(labelIndex(0u))),
                    payloadDestinationSlots = listOf(intArrayOf(1)),
                    continuationIps = intArrayOf(73),
                    framesDepth = 1,
                    framePointer = 0,
                    valueDepth = 2,
                ),
                ExceptionHandler(
                    handlers = listOf(catchRefHandler(tagIndex(0u), labelIndex(0u))),
                    payloadDestinationSlots = listOf(intArrayOf(1)),
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
