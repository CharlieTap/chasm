package io.github.charlietap.chasm.compiler.context

import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType

internal class FunctionCompilerWorkspace {

    val operandPool = ArrayList<Operand>()
    val controlPool = ArrayList<BlockContext>()
    private val valueBlockTypes = ArrayList<ValueType>()
    private val valueBlockFunctionTypes = ArrayList<FunctionType>()

    fun blockType(
        compiler: CompilerContext,
        type: BlockType,
    ): FunctionType = when (type) {
        BlockType.Empty -> compiler.emptyBlockType
        is BlockType.SignedTypeIndex -> compiler.indexedBlockType(type)
        is BlockType.ValType -> valueBlockType(compiler, type.valueType)
    }

    private fun valueBlockType(
        compiler: CompilerContext,
        valueType: ValueType,
    ): FunctionType {
        for (index in valueBlockTypes.indices) {
            if (valueBlockTypes[index] == valueType) return valueBlockFunctionTypes[index]
        }
        return compiler.types.blockType(BlockType.ValType(valueType)).also { functionType ->
            valueBlockTypes.add(valueType)
            valueBlockFunctionTypes.add(functionType)
        }
    }
}
