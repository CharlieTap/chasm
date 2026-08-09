package io.github.charlietap.chasm.compiler.ext

import io.github.charlietap.chasm.ir.instruction.Instruction
import io.github.charlietap.chasm.ir.module.Function

fun Function.traverseInstructions(): Sequence<Instruction> = body.instructions.asSequence()
