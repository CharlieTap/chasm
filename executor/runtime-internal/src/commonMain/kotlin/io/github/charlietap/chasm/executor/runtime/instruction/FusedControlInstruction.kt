package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.executor.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.ir.module.Index

sealed interface FusedControlInstruction : LinkedInstruction {

    data class BrIf(
        val operand: LoadOp,
        val labelIndex: Index.LabelIndex,
    ) : FusedControlInstruction

    data class If(
        val operand: LoadOp,
        val params: Int,
        val results: Int,
        val instructions: Array<Array<DispatchableInstruction>>,
    ) : FusedControlInstruction {
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

    data class WasmFunctionCall(
        val operands: List<LoadOp>,
        val instruction: DispatchableInstruction,
    ) : FusedControlInstruction
}
