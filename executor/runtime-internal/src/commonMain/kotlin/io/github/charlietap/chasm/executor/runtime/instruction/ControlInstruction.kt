package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.ast.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.ast.module.Index
import io.github.charlietap.chasm.ast.type.FunctionType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.instance.TableInstance
import kotlin.jvm.JvmInline

sealed interface ControlInstruction : ExecutionInstruction {

    data object Unreachable : ControlInstruction

    data object Nop : ControlInstruction

    data class Block(val functionType: FunctionType, val instructions: Array<DispatchableInstruction>) : ControlInstruction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Block

            if (functionType != other.functionType) return false
            if (!instructions.contentEquals(other.instructions)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = functionType.hashCode()
            result = 31 * result + instructions.contentHashCode()
            return result
        }
    }

    data class Loop(val functionType: FunctionType, val instructions: Array<DispatchableInstruction>) : ControlInstruction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Loop

            if (functionType != other.functionType) return false
            if (!instructions.contentEquals(other.instructions)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = functionType.hashCode()
            result = 31 * result + instructions.contentHashCode()
            return result
        }
    }

    data class If(val functionType: FunctionType, val thenInstructions: Array<DispatchableInstruction>, val elseInstructions: Array<DispatchableInstruction>?) : ControlInstruction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as If

            if (functionType != other.functionType) return false
            if (!thenInstructions.contentEquals(other.thenInstructions)) return false
            if (!elseInstructions.contentEquals(other.elseInstructions)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = functionType.hashCode()
            result = 31 * result + thenInstructions.contentHashCode()
            result = 31 * result + (elseInstructions?.contentHashCode() ?: 0)
            return result
        }
    }

    data class TryTable(val functionType: FunctionType, val handlers: List<CatchHandler>, val instructions: Array<DispatchableInstruction>) : ControlInstruction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as TryTable

            if (functionType != other.functionType) return false
            if (handlers != other.handlers) return false
            if (!instructions.contentEquals(other.instructions)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = functionType.hashCode()
            result = 31 * result + handlers.hashCode()
            result = 31 * result + instructions.contentHashCode()
            return result
        }
    }

    @JvmInline
    value class Throw(val tagIndex: Index.TagIndex) : ControlInstruction

    data object ThrowRef : ControlInstruction

    @JvmInline
    value class Br(val labelIndex: Index.LabelIndex) : ControlInstruction

    @JvmInline
    value class BrIf(val labelIndex: Index.LabelIndex) : ControlInstruction

    data class BrTable(val labelIndices: List<Index.LabelIndex>, val defaultLabelIndex: Index.LabelIndex) : ControlInstruction

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

    @JvmInline
    value class ReturnWasmFunctionCall(val instance: FunctionInstance.WasmFunction) : ControlInstruction

    @JvmInline
    value class ReturnHostFunctionCall(val instance: FunctionInstance.HostFunction) : ControlInstruction

    @JvmInline
    value class ReturnCallRef(val typeIndex: Index.TypeIndex) : ControlInstruction

    @JvmInline
    value class WasmFunctionCall(val instance: FunctionInstance.WasmFunction) : ControlInstruction

    @JvmInline
    value class HostFunctionCall(val instance: FunctionInstance.HostFunction) : ControlInstruction

    @JvmInline
    value class CallRef(val typeIndex: Index.TypeIndex) : ControlInstruction

    data class CallIndirect(
        val typeIndex: Index.TypeIndex,
        val table: TableInstance,
    ) : ControlInstruction

    data class ReturnCallIndirect(
        val typeIndex: Index.TypeIndex,
        val table: TableInstance,
    ) : ControlInstruction
}
