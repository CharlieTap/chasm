package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.resultType
import io.github.charlietap.chasm.fixture.type.valueType
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockTypeExpanderTest {

    @Test
    fun `can expand an empty block type`() {

        val instance = moduleInstance()

        val blockType = ControlInstruction.BlockType.Empty

        val actual = BlockTypeExpander(instance, blockType)

        assertEquals(Ok(null), actual)
    }

    @Test
    fun `can expand a value block type`() {
        val instance = moduleInstance()
        val valueType = valueType()

        val blockType = ControlInstruction.BlockType.ValType(valueType)

        val expected = FunctionType(resultType(emptyList()), resultType(listOf(valueType)))

        val actual = BlockTypeExpander(instance, blockType)

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can expand a signed type index block type`() {
        val typeIndex = Index.TypeIndex(0u)
        val type = functionType()
        val definedType = type.definedType()
        val instance = moduleInstance(
            types = listOf(definedType),
        )

        val blockType = ControlInstruction.BlockType.SignedTypeIndex(typeIndex)

        val actual = BlockTypeExpander(instance, blockType)

        assertEquals(Ok(type), actual)
    }
}
