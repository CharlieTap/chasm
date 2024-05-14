package io.github.charlietap.chasm.executor.runtime.instruction

import io.github.charlietap.chasm.ast.instruction.Instruction
import kotlin.jvm.JvmInline

@JvmInline
value class ModuleInstruction(val instruction: Instruction) : ExecutionInstruction
