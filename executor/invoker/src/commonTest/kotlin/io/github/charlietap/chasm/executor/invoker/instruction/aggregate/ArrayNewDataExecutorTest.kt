package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.Executor
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.memory.ext.copyInto
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.dataAddress
import io.github.charlietap.chasm.fixture.instance.dataInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.dataIndex
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.mutableFieldType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayNewDataExecutorTest {

    @Test
    fun `can execute the ArrayNewData instruction and return a correct result`() {

        val stack = stack()
        val size = 2u
        val typeIndex = typeIndex(0u)
        val dataIndex = dataIndex(0u)
        val dataAddress = dataAddress(0)
        val definedType = definedType()

        val fieldType = mutableFieldType(
            storageType = valueStorageType(
                valueType = i32ValueType(),
            ),
        )
        val arrayType = arrayCompositeType(
            arrayType = arrayType(
                fieldType,
            ),
        )

        val offset = 8
        val arrayElem1 = 117
        val arrayElem2 = 118
        val data = UByteArray(offset + (size.toInt() * 4))

        arrayElem1.copyInto(data.asByteArray(), offset)
        arrayElem2.copyInto(data.asByteArray(), offset + 4)

        val dataInstance = dataInstance(
            bytes = data,
        )
        val store = store(
            data = mutableListOf(
                dataInstance,
            ),
        )
        val context = executionContext(stack, store)

        val frame = frame(
            state = frameState(
                moduleInstance = moduleInstance(
                    types = mutableListOf(definedType),
                    dataAddresses = mutableListOf(dataAddress),
                ),
            ),
        )

        stack.push(frame)

        stack.pushValue(i32(offset))
        stack.pushValue(i32(size.toInt()))

        val definedTypeExpander: DefinedTypeExpander = { _definedType ->
            assertEquals(definedType, _definedType)

            arrayType
        }

        val arrayNewFixedExecutor: Executor<AggregateInstruction.ArrayNewFixed> = { _context, _instruction ->
            assertEquals(context, _context)
            assertEquals(AggregateInstruction.ArrayNewFixed(typeIndex, size), _instruction)
            Ok(Unit)
        }

        val actual =
            ArrayNewDataExecutor(
                context,
                AggregateInstruction.ArrayNewData(typeIndex, dataIndex),
                definedTypeExpander,
                arrayNewFixedExecutor,
            )

        assertEquals(Ok(Unit), actual)
        assertEquals(2, stack.valuesDepth())
        assertEquals(i32(arrayElem2), stack.popValueOrNull()?.value)
        assertEquals(i32(arrayElem1), stack.popValueOrNull()?.value)
    }
}
