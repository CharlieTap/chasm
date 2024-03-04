package io.github.charlietap.chasm.ast.instruction

import io.github.charlietap.chasm.ast.type.ValueType
import kotlin.jvm.JvmInline

sealed interface ControlInstruction : Instruction {

    sealed interface BlockType {
        data object Empty : BlockType

        @JvmInline
        value class ValType(val valueType: ValueType) : BlockType

        @JvmInline
        value class SignedTypeIndex(val typeIndex: Index.TypeIndex) : BlockType
    }

    data object Unreachable : ControlInstruction

    data object Nop : ControlInstruction

    data class Block(val blockType: BlockType, val instructions: List<Instruction>) : ControlInstruction

    data class Loop(val blockType: BlockType, val instructions: List<Instruction>) : ControlInstruction

    data class If(val blockType: BlockType, val thenInstructions: List<Instruction>, val elseInstructions: List<Instruction>?) : ControlInstruction

    @JvmInline
    value class Br(val labelIndex: Index.LabelIndex) : ControlInstruction

    @JvmInline
    value class BrIf(val labelIndex: Index.LabelIndex) : ControlInstruction

    data class BrTable(val labelIndices: List<Index.LabelIndex>, val defaultLabelIndex: Index.LabelIndex) : ControlInstruction

    data object Return : ControlInstruction

    @JvmInline
    value class Call(val functionIndex: Index.FunctionIndex) : ControlInstruction

    data class CallIndirect(val typeIndex: Index.TypeIndex, val tableIndex: Index.TableIndex = Index.TableIndex(0u)) : ControlInstruction
}
