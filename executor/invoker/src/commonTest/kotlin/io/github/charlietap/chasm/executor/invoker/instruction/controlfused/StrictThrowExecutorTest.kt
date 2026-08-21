package io.github.charlietap.chasm.executor.invoker.instruction.controlfused

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.ast.instruction.catchCatchHandler
import io.github.charlietap.chasm.fixture.ast.module.labelIndex
import io.github.charlietap.chasm.fixture.ast.module.tagIndex
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
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
import io.github.charlietap.chasm.runtime.instruction.ControlSuperInstruction
import kotlin.test.Test
import kotlin.test.assertEquals

class StrictThrowExecutorTest {

    @Test
    fun `strict throw copies a contiguous frame payload in semantic order`() {
        val store = store()
        val tagAddress = store.heap.registerTag(
            rtt(),
            tagType(
                functionType = functionType(
                    params = resultType(listOf(i64ValueType(), i64ValueType())),
                ),
            ),
        )
        val cstack = cstack(
            frames = listOf(frame(instance = moduleInstance(tagAddresses = mutableListOf(tagAddress)))),
            handlers = listOf(
                ExceptionHandler(
                    handlers = listOf(catchCatchHandler(tagIndex(0u), labelIndex(0u))),
                    payloadDestinationSlots = listOf(intArrayOf(2, 3)),
                    continuationIps = intArrayOf(64),
                    framesDepth = 1,
                    framePointer = 0,
                    valueDepth = 5,
                ),
            ),
        )
        val vstack = vstack().apply {
            reserveFrame(5)
            setFrameSlot(0, 51)
            setFrameSlot(1, 52)
        }

        val continuationIp = ThrowExecutor(
            vstack,
            cstack,
            store,
            executionContext(store = store, vstack = vstack, cstack = cstack),
            ControlSuperInstruction.Throw(tagIndex(0u), firstPayloadSlot = 0),
        )

        assertEquals(64, continuationIp)
        assertEquals(51, vstack.getFrameSlot(2))
        assertEquals(52, vstack.getFrameSlot(3))
    }
}
