package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.AggregateInstruction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.TableInstruction
import io.github.charlietap.chasm.cir.VariableInstruction
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.chasm.compiler.backend.analysis.form.helperCall

internal fun lowerInstruction(
    instruction: Instruction,
    context: LowerContext,
) {
    when (instruction) {
        is NumericInstruction -> lowerNumericInstruction(instruction, context)
        is VariableInstruction -> lowerVariableInstruction(instruction, context)
        is ParametricInstruction -> lowerParametricInstruction(instruction, context)
        is ControlInstruction -> lowerControlInstruction(instruction, context)
        is AdminInstruction -> lowerAdminInstruction(instruction, context)
        is MemoryInstruction -> lowerMemoryInstruction(instruction, context)
        is TableInstruction -> context.lowerHelperCall(instruction.helperCall() ?: unsupported(instruction))
        is ReferenceInstruction -> lowerReferenceInstruction(instruction, context)
        is AggregateInstruction -> lowerAggregateInstruction(instruction, context)
    }
}

internal fun LowerContext.lowerFunctionBody(
    frameCost: Int,
) {
    lowerPrologue(frameCost)
    function.instructions.forEachIndexed { position, instruction ->
        this.position = position
        val label = placement(position)
        emit.place(label)
        if (module.options.lineNumbers) emit.line(position + 1, label)
        lowerInstruction(instruction, this)
    }
    emit.place(bodyEnd)
    trapUnreachable()
    lowerExceptionHandling()
    outOfLine.forEach { path -> path() }
}

private fun LowerContext.trapUnreachable() = trap(UNREACHABLE)
