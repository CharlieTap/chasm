package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.form.select
import org.objectweb.asm.Opcodes

internal fun lowerParametricInstruction(
    instruction: ParametricInstruction,
    context: LowerContext,
) {
    val select = instruction.select()
    val second = context.emit.label()
    val done = context.emit.label()
    context.branchOnInt(select.condition, zero = true, second)
    context.copyToSlot(select.destination, select.first)
    context.emit.jump(Opcodes.GOTO, done)
    context.emit.place(second)
    context.copyToSlot(select.destination, select.second)
    context.emit.place(done)
}
