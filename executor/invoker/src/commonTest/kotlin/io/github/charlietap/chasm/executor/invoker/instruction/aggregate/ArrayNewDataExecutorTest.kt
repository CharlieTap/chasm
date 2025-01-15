package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.memory.ext.copyInto
import io.github.charlietap.chasm.executor.runtime.execution.Executor
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.fixture.ast.module.dataIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.ast.type.arrayType
import io.github.charlietap.chasm.fixture.ast.type.definedType
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import io.github.charlietap.chasm.fixture.ast.type.mutableFieldType
import io.github.charlietap.chasm.fixture.ast.type.valueStorageType
import io.github.charlietap.chasm.fixture.executor.runtime.instance.dataAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
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
            instance = moduleInstance(
                types = mutableListOf(definedType),
                dataAddresses = mutableListOf(dataAddress),
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

        assertEquals(Unit, actual)
        assertEquals(2, stack.valuesDepth())
        assertEquals(i32(arrayElem2), stack.popValue())
        assertEquals(i32(arrayElem1), stack.popValue())
    }
}
