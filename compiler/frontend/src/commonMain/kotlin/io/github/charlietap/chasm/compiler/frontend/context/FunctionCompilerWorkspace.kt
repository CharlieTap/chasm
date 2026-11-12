package io.github.charlietap.chasm.compiler.frontend.context

import io.github.charlietap.chasm.compiler.frontend.emptyIntArray
import io.github.charlietap.chasm.compiler.frontend.operand.Operand
import io.github.charlietap.chasm.compiler.frontend.program.ProgramBuilder
import io.github.charlietap.chasm.compiler.frontend.program.ProgramTarget
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType

internal class FunctionCompilerWorkspace {

    val program = ProgramBuilder()
    val operandPool = ObjectPool<Operand>()
    private var body = arrayOfNulls<Any?>(INITIAL_BODY_CAPACITY)

    fun body(
        instructions: List<Any?>,
    ): Array<Any?> {
        if (instructions.size > body.size) body = arrayOfNulls(maxOf(instructions.size, body.size * 2))
        val body = body
        for (index in instructions.indices) body[index] = instructions[index]
        return body
    }

    private var chainElses = emptyIntArray
    private var rejectedChains = BooleanArray(0)

    fun chainElses(
        size: Int,
    ): IntArray {
        if (size > chainElses.size) chainElses = IntArray(maxOf(size, chainElses.size * 2))
        return chainElses
    }

    fun rejectedChains(
        size: Int,
    ): BooleanArray {
        if (size > rejectedChains.size) rejectedChains = BooleanArray(maxOf(size, rejectedChains.size * 2))
        return rejectedChains
    }

    private var chainCases = arrayOfNulls<ProgramTarget>(0)
    private var chainDefaults = arrayOfNulls<ProgramTarget>(0)

    fun chainCases(
        size: Int,
    ): Array<ProgramTarget?> {
        if (size > chainCases.size) chainCases = arrayOfNulls(maxOf(size, chainCases.size * 2))
        return chainCases
    }

    fun chainDefaults(
        size: Int,
    ): Array<ProgramTarget?> {
        if (size > chainDefaults.size) chainDefaults = arrayOfNulls(maxOf(size, chainDefaults.size * 2))
        return chainDefaults
    }

    val controlPool = ObjectPool<BlockContext>()
    private val valueBlockTypes = ArrayList<ValueType>()
    private val valueBlockFunctionTypes = ArrayList<FunctionType>()

    fun blockType(
        compiler: CompilerContext,
        type: BlockType,
    ): FunctionType = when (type) {
        is BlockType.Empty -> compiler.emptyBlockType
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

    private companion object {
        const val INITIAL_BODY_CAPACITY = 256
    }
}
