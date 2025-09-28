package io.github.charlietap.chasm.compiler.ext

import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction

internal fun Instruction.isAllocating(): Boolean = when (this) {
    is AggregateInstruction.StructNew,
    is AggregateInstruction.StructNewDefault,
    is AggregateInstruction.ArrayNew,
    is AggregateInstruction.ArrayNewData,
    is AggregateInstruction.ArrayNewFixed,
    is AggregateInstruction.ArrayNewElement,
    is AggregateInstruction.ArrayNewDefault,
    is FusedAggregateInstruction.StructNew,
    is FusedAggregateInstruction.StructNewDefault,
    is FusedAggregateInstruction.ArrayNew,
    is FusedAggregateInstruction.ArrayNewFixed,
    -> true
    else -> false
}
