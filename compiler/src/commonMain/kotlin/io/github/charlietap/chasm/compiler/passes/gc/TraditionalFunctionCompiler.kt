package io.github.charlietap.chasm.compiler.passes.gc

import io.github.charlietap.chasm.compiler.ext.isAllocating
import io.github.charlietap.chasm.compiler.passes.PassContext
import io.github.charlietap.chasm.ir.instruction.AdminInstruction
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.module.Module

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
            add(instruction)

            if (instruction.isAllocating()) {
                add(AdminInstruction.PauseIf)
            }
        }
    }
}
