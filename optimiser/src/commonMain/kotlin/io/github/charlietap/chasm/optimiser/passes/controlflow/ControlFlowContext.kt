package io.github.charlietap.chasm.optimiser.passes.controlflow

import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.StackAdjustment
import io.github.charlietap.chasm.optimiser.passes.PassContextt

internal data class ControlFlowContext(
    val context: PassContextt,
    var sp: Int = 0,
    var ip: Int = 0,
    var blocks: ArrayDeque<Block> = ArrayDeque(),
) : PassContextt by context

internal data class Block(
    val adjustment: StackAdjustment,
    val forwards: Boolean = true,
    val jumps: MutableList<AdminInstruction.JumpInstruction> = mutableListOf(),
    val jumpTables: MutableList<Pair<Int, AdminInstruction.JumpInstruction>> = mutableListOf(),
)
