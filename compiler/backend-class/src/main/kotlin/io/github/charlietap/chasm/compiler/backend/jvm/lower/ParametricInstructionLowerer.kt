package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.ParametricInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.select
import org.objectweb.asm.Opcodes

internal fun lowerParametricInstruction(
    instruction: ParametricInstruction,
    context: LowerContext,
) {

    val select = instruction.select()
    val condition = select.condition
    if (condition is Operand.Immediate) {
        context.copyToSlot(select.destination, if (condition.bits != 0L) select.first else select.second)
        return
    }

    val second = context.emit.label()
    val done = context.emit.label()
    context.branchOnInt(condition, zero = true, second)
    context.copyToSlot(select.destination, select.first)
    context.emit.jump(Opcodes.GOTO, done)
    context.emit.place(second)
    context.copyToSlot(select.destination, select.second)
    context.emit.place(done)
}
