package io.github.charlietap.chasm.executor.invoker.instruction.aggregate

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.pushValue
import io.github.charlietap.chasm.fixture.frame
import io.github.charlietap.chasm.fixture.frameState
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.typeIndex
import io.github.charlietap.chasm.fixture.stack
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.arrayCompositeType
import io.github.charlietap.chasm.fixture.type.arrayType
import io.github.charlietap.chasm.fixture.type.definedType
import io.github.charlietap.chasm.fixture.type.fieldType
import io.github.charlietap.chasm.fixture.value.i32
import io.github.charlietap.chasm.type.expansion.DefinedTypeExpander
import kotlin.test.Test
import kotlin.test.assertEquals

class ArrayNewDefaultExecutorImplTest {

    @Test
    fun `can execute the ArrayNewDefault instruction and return a correct result`() {

        val store = store()
        val stack = stack()
        val size = 2u
        val typeIndex = typeIndex(0u)
        val definedType = definedType()

        val fieldType = fieldType()
        val default = fieldType.default().value
        val arrayType = arrayCompositeType(
            arrayType = arrayType(
                fieldType,
            ),
        )

        val frame = frame(
            state = frameState(
                moduleInstance = moduleInstance(
                    types = mutableListOf(definedType),
                ),
            ),
        )

        stack.push(frame)

        stack.pushValue(i32(size.toInt()))

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

        val actual = ArrayNewDefaultExecutorImpl(store, stack, typeIndex, definedTypeExpander, arrayNewFixedExecutor)

        assertEquals(Ok(Unit), actual)
        assertEquals(2, stack.valuesDepth())
        assertEquals(default, stack.popValueOrNull()?.value)
        assertEquals(default, stack.popValueOrNull()?.value)
    }
}
