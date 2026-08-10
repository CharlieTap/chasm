package io.github.charlietap.chasm.compiler.context

import io.github.charlietap.chasm.ast.module.Export
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.compiler.operand.Operand
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.runtime.store.Store
import io.github.charlietap.chasm.runtime.type.ModuleTypeResolver
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.RTT
import io.github.charlietap.chasm.type.ValueType

internal class CompilerContext(
    val config: RuntimeConfig,
    val module: Module,
    val types: ModuleTypeResolver,
    val store: Store,
    val instance: ModuleInstance,
    val runtimeTypes: List<RTT>,
) {
    val operandPool = ArrayList<Operand>()
    val controlPool = ArrayList<BlockContext>()
    private val emptyBlockType = types.blockType(BlockType.Empty)
    private val valueBlockTypes = ArrayList<ValueType>()
    private val valueBlockFunctionTypes = ArrayList<FunctionType>()

    var containsGcInstructions = false

    val exportedFunctions = BooleanArray(instance.functionAddresses.size).also { exportedFunctions ->
        for (index in module.exports.indices) {
            val export = module.exports[index]
            val descriptor = export.descriptor
            if (descriptor is Export.Descriptor.Function) {
                exportedFunctions[descriptor.functionIndex.toInt()] = true
            }
        }
    }

    fun blockType(type: BlockType): FunctionType = when (type) {
        BlockType.Empty -> emptyBlockType
        is BlockType.SignedTypeIndex -> types.blockType(type)
        is BlockType.ValType -> valueBlockType(type.valueType)
    }

    private fun valueBlockType(valueType: ValueType): FunctionType {
        for (index in valueBlockTypes.indices) {
            if (valueBlockTypes[index] == valueType) return valueBlockFunctionTypes[index]
        }
        return types.blockType(BlockType.ValType(valueType)).also { functionType ->
            valueBlockTypes.add(valueType)
            valueBlockFunctionTypes.add(functionType)
        }
    }
}
