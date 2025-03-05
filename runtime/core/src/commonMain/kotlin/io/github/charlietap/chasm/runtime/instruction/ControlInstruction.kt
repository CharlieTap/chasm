package io.github.charlietap.chasm.runtime.instruction

import io.github.charlietap.chasm.ir.instruction.ControlInstruction.CatchHandler
import io.github.charlietap.chasm.ir.module.Index
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.runtime.instance.TableInstance
import io.github.charlietap.chasm.type.DefinedType
import io.github.charlietap.chasm.type.ReferenceType
import kotlin.jvm.JvmInline

sealed interface ControlInstruction : LinkedInstruction {

    data object Unreachable : ControlInstruction

    data object Nop : ControlInstruction

    data class Block(
        val params: Int,
        val results: Int,
        val instructions: Array<DispatchableInstruction>,
    ) : ControlInstruction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Block

            if (params != other.params) return false
            if (results != other.results) return false
            if (!instructions.contentEquals(other.instructions)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = params
            result = 31 * result + results
            result = 31 * result + instructions.contentHashCode()
            return result
        }
    }

    data class Loop(
        val params: Int,
        val instructions: Array<DispatchableInstruction>,
    ) : ControlInstruction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as Loop

            if (params != other.params) return false
            if (!instructions.contentEquals(other.instructions)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = params
            result = 31 * result + instructions.contentHashCode()
            return result
        }
    }

    data class If(
        val params: Int,
        val results: Int,
        val instructions: Array<Array<DispatchableInstruction>>,
    ) : ControlInstruction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as If

            if (params != other.params) return false
            if (results != other.results) return false
            if (!instructions.contentEquals(other.instructions)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = params
            result = 31 * result + results
            result = 31 * result + instructions.contentHashCode()
            return result
        }
    }

    data class TryTable(
        val params: Int,
        val results: Int,
        val handlers: List<CatchHandler>,
        val instructions: Array<DispatchableInstruction>,
    ) : ControlInstruction {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as TryTable

            if (params != other.params) return false
            if (results != other.results) return false
            if (handlers != other.handlers) return false
            if (!instructions.contentEquals(other.instructions)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = params
            result = 31 * result + results
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
        val type: DefinedType,
        val table: TableInstance,
    ) : ControlInstruction

    data class ReturnCallIndirect(
        val type: DefinedType,
        val table: TableInstance,
    ) : ControlInstruction
}
