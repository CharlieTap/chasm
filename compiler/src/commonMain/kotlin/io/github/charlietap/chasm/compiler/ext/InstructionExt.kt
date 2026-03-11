package io.github.charlietap.chasm.compiler.ext

import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.AggregateSuperInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction

internal fun Instruction.isAllocating(): Boolean = when (this) {
    is AggregateInstruction.StructNew,
    is AggregateInstruction.StructNewDefault,
    is AggregateInstruction.ArrayNew,
    is AggregateInstruction.ArrayNewData,
    is AggregateInstruction.ArrayNewFixed,
    is AggregateInstruction.ArrayNewElement,
    is AggregateInstruction.ArrayNewDefault,
    is AggregateSuperInstruction.StructNew,
    is AggregateSuperInstruction.StructNewDefault,
    is AggregateSuperInstruction.ArrayNew,
    is AggregateSuperInstruction.ArrayNewData,
    is AggregateSuperInstruction.ArrayNewDefault,
    is AggregateSuperInstruction.ArrayNewElement,
    is AggregateSuperInstruction.ArrayNewFixed,
    -> true
    else -> false
}
