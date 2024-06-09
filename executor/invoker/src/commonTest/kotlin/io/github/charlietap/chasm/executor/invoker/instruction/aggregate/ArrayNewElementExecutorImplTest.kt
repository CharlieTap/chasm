package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.elementAddress
import io.github.charlietap.chasm.fixture.instance.elementInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.elementIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.fixture.value.referenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayNewElementExecutorImplTest {

    @Test
    fun `can execute the ArrayNewElement instruction and return a correct result`() {

        val stack = stack()
        val size = 2u
        val typeIndex = typeIndex(0u)
        val elementIndex = elementIndex(0u)
        val elementAddress = elementAddress(0)

        val offset = 4
        val arrayElem1 = ReferenceValue.I31(117u)
        val arrayElem2 = ReferenceValue.I31(118u)

        val elementInstance = elementInstance(
            elements = arrayOf(
                referenceValue(),
                referenceValue(),
                referenceValue(),
                referenceValue(),
                arrayElem1,
                arrayElem2,
            ),
        )
        val store = store(
            elements = mutableListOf(
                elementInstance,
            ),
        )

        val frame = frame(
            state = frameState(
                moduleInstance = moduleInstance(
                    elemAddresses = mutableListOf(elementAddress),
                ),
            ),
        )

        stack.push(frame)

        stack.pushValue(i32(offset))
        stack.pushValue(i32(size.toInt()))

        val arrayNewFixedExecutor: ArrayNewFixedExecutor = { _store, _stack, _typeIndex, _size ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)
            assertEquals(size, _size)

            Ok(Unit)
        }

        val actual = ArrayNewElementExecutorImpl(store, stack, typeIndex, elementIndex, arrayNewFixedExecutor)

        assertEquals(Ok(Unit), actual)
        assertEquals(2, stack.valuesDepth())
        assertEquals(arrayElem2, stack.popValueOrNull()?.value)
        assertEquals(arrayElem1, stack.popValueOrNull()?.value)
    }
}
