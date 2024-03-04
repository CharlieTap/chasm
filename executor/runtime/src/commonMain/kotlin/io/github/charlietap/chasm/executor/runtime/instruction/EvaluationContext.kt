package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.ast.instruction.Instruction

data class EvaluationContext(
    val before: List<Instruction>,
    val after: List<Instruction>,
)
