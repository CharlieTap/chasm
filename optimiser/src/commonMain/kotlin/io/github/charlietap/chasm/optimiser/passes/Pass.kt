package io.github.charlietap.chasm.optimiser.passes

import io.github.charlietap.chasm.ir.instruction.Instruction

internal typealias Pass = (List<Instruction>) -> List<Instruction>
