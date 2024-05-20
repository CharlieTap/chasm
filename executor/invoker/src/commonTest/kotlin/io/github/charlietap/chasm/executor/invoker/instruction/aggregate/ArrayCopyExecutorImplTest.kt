package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.gc.weakReference
import io.github.charlietap.chasm.executor.runtime.ext.push
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.arrayAddress
import io.github.charlietap.chasm.fixture.instance.arrayInstance
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
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

class ArrayCopyExecutorImplTest {

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

        val srcArrayAddress = arrayAddress(0)
        val srcArrayInstance = arrayInstance(
            definedType = definedType,
            fields = MutableList(4) { fillFieldValue },
        )
        val dstArrayAddress = arrayAddress(1)
        val dstArrayInstance = arrayInstance(
            definedType = definedType,
            fields = MutableList(4) { fieldValue },
        )
        val store = store(
            arrays = mutableListOf(
                weakReference(srcArrayInstance),
                weakReference(dstArrayInstance),
            ),
        )
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

        stack.push(arrayReferenceValue(dstArrayAddress))
        stack.push(i32(0))
        stack.push(arrayReferenceValue(srcArrayAddress))
        stack.push(i32(0))
        stack.push(i32(4))

        val expectedInstance = arrayInstance(
            definedType = definedType,
            fields = MutableList(4) { fillFieldValue },
        )

        val actual = ArrayCopyExecutorImpl(store, stack, srcTypeIndex, dstTypeIndex)

        assertEquals(Ok(Unit), actual)
        assertEquals(store.arrays[1].value, expectedInstance)
        assertEquals(0, stack.valuesDepth())
    }
}
