package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.AggregateInstruction
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.arrayInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.finalSubType
import io.github.charlietap.chasm.fixture.type.mutableFieldType
import io.github.charlietap.chasm.fixture.type.recursiveType
import io.github.charlietap.chasm.fixture.type.valueStorageType
import io.github.charlietap.chasm.fixture.type.varMutability
import io.github.charlietap.chasm.fixture.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.value.executionFieldValue
import io.github.charlietap.chasm.fixture.value.fieldValue
import io.github.charlietap.chasm.fixture.value.i32
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayCopyExecutorTest {

    @Test
    fun `can execute the ArrayCopy instruction and return a correct result`() {

        val stack = stack()
        val srcTypeIndex = typeIndex(0u)
        val dstTypeIndex = typeIndex(1u)
        val definedType = definedType(
            recursiveType = recursiveType(
                subTypes = listOf(
                    finalSubType(
                        compositeType = arrayCompositeType(
                            arrayType = arrayType(
                                fieldType = mutableFieldType(
                                    storageType = valueStorageType(),
                                    mutability = varMutability(),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )

        val fieldValue = fieldValue()
        val fillValue = i32(117)
        val fillFieldValue = executionFieldValue(fillValue)

        val srcArrayInstance = arrayInstance(
            definedType = definedType,
            fields = MutableList(4) { fillFieldValue },
        )
        val dstArrayInstance = arrayInstance(
            definedType = definedType,
            fields = MutableList(4) { fieldValue },
        )

        val context = executionContext(stack)
        val frame = frame(
            state = frameState(
                moduleInstance = moduleInstance(
                    types = mutableListOf(
                        definedType,
                        definedType,
                    ),
                ),
            ),
        )

        stack.push(frame)

        stack.pushValue(arrayReferenceValue(dstArrayInstance))
        stack.pushValue(i32(0))
        stack.pushValue(arrayReferenceValue(srcArrayInstance))
        stack.pushValue(i32(0))
        stack.pushValue(i32(4))

        val expectedInstance = arrayInstance(
            definedType = definedType,
            fields = MutableList(4) { fillFieldValue },
        )

        val actual = ArrayCopyExecutor(context, AggregateInstruction.ArrayCopy(srcTypeIndex, dstTypeIndex))

        assertEquals(Ok(Unit), actual)
        assertEquals(expectedInstance, dstArrayInstance)
        assertEquals(0, stack.valuesDepth())
    }
}
