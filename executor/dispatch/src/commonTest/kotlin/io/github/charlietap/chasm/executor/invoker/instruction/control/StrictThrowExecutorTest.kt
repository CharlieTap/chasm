package io.github.charlietap.chasm.executor.invoker.instruction.control

import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.fixture.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.runtime.exception.compiledCatch
import io.github.charlietap.chasm.fixture.runtime.exception.exceptionRegion
import io.github.charlietap.chasm.fixture.runtime.exception.functionExceptionTable
import io.github.charlietap.chasm.fixture.runtime.instruction.throwRuntimeInstruction
import io.github.charlietap.chasm.fixture.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.fixture.runtime.type.rtt
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.i64ValueType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.tagType
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
        store.program.append(Array(65) { dispatchableInstruction() })
        store.program.registerExceptionTable(
            functionExceptionTable(
                0,
                65,
                2,
                0,
                arrayOf(
                    exceptionRegion(
                        0,
                        1,
                        -1,
                        arrayOf(
                            compiledCatch(tagAddress.address, 64, intArrayOf(2, 3), false, 5),
                        ),
                    ),
                ),
                intArrayOf(),
            ),
        )
        val vstack = vstack().apply {
            reserveDepth(5)
            setFrameSlot(0, 51)
            setFrameSlot(1, 52)
        }

        val continuationIp = ThrowExecutor(
            vstack,
            executionContext(store = store, vstack = vstack),
            throwRuntimeInstruction(tagAddress = tagAddress, firstPayloadSlot = 0),
            faultIp = 0,
        )

        assertEquals(64, continuationIp)
        assertEquals(51, vstack.getFrameSlot(2))
        assertEquals(52, vstack.getFrameSlot(3))
    }
}
