package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.gc.weakReference
import io.github.charlietap.chasm.executor.runtime.ext.push
import io.github.charlietap.chasm.executor.runtime.value.NumberValue
import io.github.charlietap.chasm.fixture.instance.arrayAddress
import io.github.charlietap.chasm.fixture.instance.arrayInstance
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.value.arrayReferenceValue
import io.github.charlietap.chasm.fixture.value.fieldValue
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayLenExecutorImplTest {

    @Test
    fun `can execute the ArrayLen instruction and return a correct result`() {

        val stack = stack()
        val fieldValue = fieldValue()

        val arrayAddress = arrayAddress(0)
        val arrayInstance = arrayInstance(
            definedType = definedType(),
            fields = mutableListOf(fieldValue, fieldValue, fieldValue),
        )
        val store = store(
            arrays = mutableListOf(weakReference(arrayInstance)),
        )

        val referenceValue = arrayReferenceValue(arrayAddress)
        stack.push(referenceValue)

        val actual = ArrayLenExecutorImpl(store, stack)

        assertEquals(Ok(Unit), actual)
        assertEquals(NumberValue.I32(3), stack.popValueOrNull()?.value)
    }
}
