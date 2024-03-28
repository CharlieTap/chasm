package io.github.charlietap.chasm.executor.invoker.instruction.control

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ResultType
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.valueType
import kotlin.test.Test
import kotlin.test.assertEquals

class BlockTypeExpanderImplTest {

    @Test
    fun `can expand an empty block type`() {

        val instance = moduleInstance()

        val blockType = ControlInstruction.BlockType.Empty

        val actual = BlockTypeExpanderImpl(instance, blockType)

        assertEquals(Ok(null), actual)
    }

    @Test
    fun `can expand a value block type`() {
        val instance = moduleInstance()
        val valueType = valueType()

        val blockType = ControlInstruction.BlockType.ValType(valueType)

        val expected = FunctionType(ResultType(emptyList()), ResultType(listOf(valueType)))

        val actual = BlockTypeExpanderImpl(instance, blockType)

        assertEquals(Ok(expected), actual)
    }

    @Test
    fun `can expand a signed type index block type`() {
        val typeIndex = Index.TypeIndex(0u)
        val type = functionType()
        val instance = moduleInstance(
            types = listOf(type),
        )

        val blockType = ControlInstruction.BlockType.SignedTypeIndex(typeIndex)

        val actual = BlockTypeExpanderImpl(instance, blockType)

        assertEquals(Ok(type), actual)
    }
}
