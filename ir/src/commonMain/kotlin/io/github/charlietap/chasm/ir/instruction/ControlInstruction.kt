package io.github.charlietap.chasm.ir.instruction

import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.type.BlockType
import io.github.charlietap.chasm.type.ReferenceType
import kotlin.jvm.JvmInline

sealed interface ControlInstruction : Instruction {

    sealed interface CatchHandler {

        val labelIndex: Index.LabelIndex

        data class Catch(val tagIndex: Index.TagIndex, override val labelIndex: Index.LabelIndex) : CatchHandler

        data class CatchRef(val tagIndex: Index.TagIndex, override val labelIndex: Index.LabelIndex) : CatchHandler

        @JvmInline
        value class CatchAll(override val labelIndex: Index.LabelIndex) : CatchHandler

        @JvmInline
        value class CatchAllRef(override val labelIndex: Index.LabelIndex) : CatchHandler
    }

    data object Unreachable : ControlInstruction

    data object Nop : ControlInstruction

    data class Block(val blockType: BlockType, val instructions: List<Instruction>) : ControlInstruction

    data class Loop(val blockType: BlockType, val instructions: List<Instruction>) : ControlInstruction

    data class If(val blockType: BlockType, val thenInstructions: List<Instruction>, val elseInstructions: List<Instruction>?) : ControlInstruction

    data class TryTable(val blockType: BlockType, val handlers: List<CatchHandler>, val instructions: List<Instruction>) : ControlInstruction

    @JvmInline
    value class Throw(val tagIndex: Index.TagIndex) : ControlInstruction

    data object ThrowRef : ControlInstruction

    @JvmInline
    value class Br(val labelIndex: Index.LabelIndex) : ControlInstruction

    @JvmInline
    value class BrIf(val labelIndex: Index.LabelIndex) : ControlInstruction

    data class BrTable(
        val labelIndices: List<Index.LabelIndex>,
        val defaultLabelIndex: Index.LabelIndex,
    ) : ControlInstruction

    @JvmInline
    value class BrOnNull(val labelIndex: Index.LabelIndex) : ControlInstruction

    @JvmInline
    value class BrOnNonNull(val labelIndex: Index.LabelIndex) : ControlInstruction

    data class BrOnCast(
        val labelIndex: Index.LabelIndex,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
    ) : ControlInstruction

    data class BrOnCastFail(
        val labelIndex: Index.LabelIndex,
        val srcReferenceType: ReferenceType,
        val dstReferenceType: ReferenceType,
    ) : ControlInstruction

    data object Return : ControlInstruction

    data object ReturnExpression : ControlInstruction

    @JvmInline
    value class ReturnCall(val functionIndex: Index.FunctionIndex) : ControlInstruction

    @JvmInline
    value class ReturnCallRef(val typeIndex: Index.TypeIndex) : ControlInstruction

    @JvmInline
    value class Call(val functionIndex: Index.FunctionIndex) : ControlInstruction

    @JvmInline
    value class CallRef(val typeIndex: Index.TypeIndex) : ControlInstruction

    data class CallIndirect(
        val typeIndex: Index.TypeIndex,
        val tableIndex: Index.TableIndex,
    ) : ControlInstruction

    data class ReturnCallIndirect(
        val typeIndex: Index.TypeIndex,
        val tableIndex: Index.TableIndex,
    ) : ControlInstruction
}
