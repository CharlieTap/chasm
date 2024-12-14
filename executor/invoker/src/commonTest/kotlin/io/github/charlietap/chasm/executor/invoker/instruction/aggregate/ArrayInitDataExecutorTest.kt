package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.memory.ext.copyInto
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.executor.runtime.instruction.AggregateInstruction
import io.github.charlietap.chasm.fixture.ast.module.dataIndex
import io.github.charlietap.chasm.fixture.ast.module.typeIndex
import io.github.charlietap.chasm.fixture.ast.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.ast.type.arrayType
import io.github.charlietap.chasm.fixture.ast.type.definedType
import io.github.charlietap.chasm.fixture.ast.type.finalSubType
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import io.github.charlietap.chasm.fixture.ast.type.mutableFieldType
import io.github.charlietap.chasm.fixture.ast.type.recursiveType
import io.github.charlietap.chasm.fixture.ast.type.valueStorageType
import io.github.charlietap.chasm.fixture.ast.type.varMutability
import io.github.charlietap.chasm.fixture.executor.runtime.frame
import io.github.charlietap.chasm.fixture.executor.runtime.frameState
import io.github.charlietap.chasm.fixture.executor.runtime.instance.arrayInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.dataAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.executionFieldValue
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayInitDataExecutorTest {

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
            data = mutableListOf(
                dataInstance,
            ),
        )
        val context = executionContext(stack, store)
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

        stack.pushValue(arrayReferenceValue(arrayInstance))
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

        val actual = ArrayInitDataExecutor(context, AggregateInstruction.ArrayInitData(typeIndex, dataIndex))

        assertEquals(Ok(Unit), actual)
        assertEquals(expectedInstance, arrayInstance)
        assertEquals(0, stack.valuesDepth())
    }
}
