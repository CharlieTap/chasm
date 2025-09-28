package io.github.charlietap.chasm.compiler.ext

import io.github.charlietap.chasm.ir.instruction.AggregateInstruction
import io.github.charlietap.chasm.ir.instruction.FusedAggregateInstruction
import io.github.charlietap.chasm.ir.module.Export
import io.github.charlietap.chasm.ir.module.Module

internal fun Module.containsGcInstructions(): Boolean = functions.any { function ->
    function.traverseInstructions().any { instruction ->
        instruction is AggregateInstruction ||
            instruction is FusedAggregateInstruction
    }
}

internal val Module.exportedFunctions
    get() = exports.map(Export::descriptor)
        .filterIsInstance<Export.Descriptor.Function>()
        .mapTo(mutableSetOf(), Export.Descriptor.Function::functionIndex)
