package io.github.charlietap.chasm.fixture.instruction

import io.github.charlietap.chasm.ast.instruction.Instruction
import io.github.charlietap.chasm.executor.runtime.instruction.ModuleInstruction

fun moduleInstruction(
    instruction: Instruction = instruction(),
) = ModuleInstruction(instruction)
