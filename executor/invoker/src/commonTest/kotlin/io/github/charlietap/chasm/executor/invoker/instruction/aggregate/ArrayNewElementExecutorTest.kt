package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.ast.module.elementIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.executor.runtime.frame
import io.github.charlietap.chasm.fixture.executor.runtime.instance.elementAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.elementInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.fixture.executor.runtime.value.referenceValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayNewElementExecutorTest {

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
        val context = executionContext(stack, store)

        val frame = frame(
            instance = moduleInstance(
                elemAddresses = mutableListOf(elementAddress),
            ),
        )

        stack.push(frame)

        stack.pushValue(i32(offset))
        stack.pushValue(i32(size.toInt()))

        val arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(AggregateInstruction.ArrayNewFixed(typeIndex, size), _instruction)
            Ok(Unit)
        }

        val actual = ArrayNewElementExecutor(context, AggregateInstruction.ArrayNewElement(typeIndex, elementIndex), arrayNewFixedExecutor)

        assertEquals(Ok(Unit), actual)
        assertEquals(2, stack.valuesDepth())
        assertEquals(arrayElem2, stack.popValueOrNull()?.value)
        assertEquals(arrayElem1, stack.popValueOrNull()?.value)
    }
}
