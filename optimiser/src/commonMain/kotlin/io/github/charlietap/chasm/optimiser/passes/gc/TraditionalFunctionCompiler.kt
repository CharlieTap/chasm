package io.github.charlietap.chasm.optimiser.passes.gc

import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.ControlInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.FusedControlInstruction
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.module.Module
import io.github.charlietap.chasm.optimiser.ext.isAllocating
import io.github.charlietap.chasm.optimiser.passes.PassContext

internal typealias TraditionalFunctionCompiler = (PassContext, Module) -> Module

internal fun TraditionalFunctionCompiler(
    context: PassContext,
    module: Module,
): Module {
    return module.copy(
        functions = module.functions.map { function ->
            function.copy(
                body = Expression(
                    instructions = compileInstructions(function.body.instructions),
                ),
            )
        },
    )
}

private fun compileInstructions(
    instructions: List<Instruction>,
): List<Instruction> {
    return buildList {
        instructions.forEach { instruction ->
            add(
                when (instruction) {
                    is ControlInstruction.Block -> {
                        ControlInstruction.Block(
                            blockType = instruction.blockType,
                            instructions = compileInstructions(instruction.instructions),
                        )
                    }
                    is ControlInstruction.Loop -> {
                        ControlInstruction.Loop(
                            blockType = instruction.blockType,
                            instructions = compileInstructions(instruction.instructions),
                        )
                    }
                    is ControlInstruction.If -> {
                        ControlInstruction.If(
                            blockType = instruction.blockType,
                            thenInstructions = compileInstructions(instruction.thenInstructions),
                            elseInstructions = instruction.elseInstructions?.let { compileInstructions(it) },
                        )
                    }
                    is ControlInstruction.TryTable -> {
                        ControlInstruction.TryTable(
                            blockType = instruction.blockType,
                            handlers = instruction.handlers,
                            instructions = compileInstructions(instruction.instructions),
                        )
                    }
                    is FusedControlInstruction.If -> {
                        FusedControlInstruction.If(
                            operand = instruction.operand,
                            blockType = instruction.blockType,
                            thenInstructions = compileInstructions(instruction.thenInstructions),
                            elseInstructions = instruction.elseInstructions?.let { compileInstructions(it) },
                        )
                    }
                    else -> instruction
                },
            )

            if (instruction.isAllocating()) {
                add(AdminInstruction.PauseIf)
            }
        }
    }
}
