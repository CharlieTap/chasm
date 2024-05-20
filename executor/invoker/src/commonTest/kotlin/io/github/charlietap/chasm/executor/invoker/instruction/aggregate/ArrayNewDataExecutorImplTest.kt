package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.memory.ext.copyInto
import io.github.charlietap.chasm.executor.runtime.ext.push
import io.github.charlietap.chasm.executor.type.expansion.DefinedTypeExpander
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
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayNewDataExecutorImplTest {

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

        val frame = frame(
            state = frameState(
                moduleInstance = moduleInstance(
                    types = mutableListOf(definedType),
                    dataAddresses = mutableListOf(dataAddress),
                ),
            ),
        )

        stack.push(frame)

        stack.push(i32(offset))
        stack.push(i32(size.toInt()))

        val definedTypeExpander: DefinedTypeExpander = { _definedType ->
            assertEquals(definedType, _definedType)

            arrayType
        }

        val arrayNewFixedExecutor: ArrayNewFixedExecutor = { _store, _stack, _typeIndex, _size ->
            assertEquals(store, _store)
            assertEquals(stack, _stack)
            assertEquals(typeIndex, _typeIndex)
            assertEquals(size, _size)

            Ok(Unit)
        }

        val actual = ArrayNewDataExecutorImpl(store, stack, typeIndex, dataIndex, definedTypeExpander, arrayNewFixedExecutor)

        assertEquals(Ok(Unit), actual)
        assertEquals(2, stack.valuesDepth())
        assertEquals(i32(arrayElem2), stack.popValueOrNull()?.value)
        assertEquals(i32(arrayElem1), stack.popValueOrNull()?.value)
    }
}
