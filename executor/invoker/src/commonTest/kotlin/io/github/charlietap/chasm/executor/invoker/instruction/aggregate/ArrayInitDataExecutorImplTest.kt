package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.gc.weakReference
import io.github.charlietap.chasm.executor.memory.ext.copyInto
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.arrayAddress
import io.github.charlietap.chasm.fixture.instance.arrayInstance
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
import io.github.charlietap.chasm.fixture.type.finalSubType
import io.github.charlietap.chasm.fixture.type.i32ValueType
import io.github.charlietap.chasm.fixture.type.mutableFieldType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.fixture.type.varMutability
import io.github.charlietap.chasm.fixture.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.value.executionFieldValue
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayInitDataExecutorImplTest {

    @Test
    fun `can execute the ArrayInitData instruction and return a correct result`() {

        val stack = stack()
        val typeIndex = typeIndex(0u)
        val dataIndex = dataIndex(0u)
        val fieldValue = executionFieldValue(i32(0))
        val definedType = definedType(
            recursiveType = recursiveType(
                subTypes = listOf(
                    finalSubType(
                        compositeType = arrayCompositeType(
                            arrayType = arrayType(
                                fieldType = mutableFieldType(
                                    storageType = valueStorageType(
                                        valueType = i32ValueType(),
                                    ),
                                    mutability = varMutability(),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val arrayAddress = arrayAddress(0)
        val arrayInstance = arrayInstance(
            definedType = definedType,
            fields = MutableList(4) { fieldValue },
        )
        val dataValue = 117
        val data = UByteArray(4)
        dataValue.copyInto(data.asByteArray(), 0)
        val dataAddress = dataAddress(0)
        val dataInstance = dataInstance(
            bytes = data,
        )
        val store = store(
            arrays = mutableListOf(
                weakReference(arrayInstance),
            ),
            data = mutableListOf(
                dataInstance,
            ),
        )
        val frame = frame(
            state = frameState(
                moduleInstance = moduleInstance(
                    types = mutableListOf(
                        definedType,
                    ),
                    dataAddresses = mutableListOf(
                        dataAddress,
                    ),
                ),
            ),
        )

        stack.push(frame)

        stack.pushValue(arrayReferenceValue(arrayAddress))
        stack.pushValue(i32(0))
        stack.pushValue(i32(0))
        stack.pushValue(i32(1))

        val expectedInstance = arrayInstance(
            definedType = definedType,
            fields = mutableListOf(
                executionFieldValue(i32(117)),
                fieldValue,
                fieldValue,
                fieldValue,
            ),
        )

        val actual = ArrayInitDataExecutorImpl(store, stack, typeIndex, dataIndex)

        assertEquals(Ok(Unit), actual)
        assertEquals(store.arrays[0].value, expectedInstance)
        assertEquals(0, stack.valuesDepth())
    }
}
