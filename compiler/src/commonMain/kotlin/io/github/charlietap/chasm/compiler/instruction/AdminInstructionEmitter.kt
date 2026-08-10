package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.admin.CopySlotsDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F32ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.F64ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I32ConstDispatcher
import io.github.charlietap.chasm.executor.invoker.dispatch.numericfused.I64ConstDispatcher
import io.github.charlietap.chasm.runtime.instruction.AdminInstruction
import io.github.charlietap.chasm.runtime.instruction.NumericSuperInstruction

internal fun FunctionCompilationContext.emitCopy(
    sourceSlot: Int,
    destinationSlot: Int,
) {
    if (sourceSlot == destinationSlot) return
    program.append(CopySlotDispatcher(sourceSlot, destinationSlot))
}

internal fun FunctionCompilationContext.emitCopies(
    sourceSlots: IntArray,
    destinationSlots: IntArray,
) {
    check(sourceSlots.size == destinationSlots.size)
    var changed = false
    for (index in sourceSlots.indices) {
        if (sourceSlots[index] != destinationSlots[index]) {
            changed = true
            break
        }
    }
    if (!changed) return
    if (sourceSlots.size == 1) {
        program.append(CopySlotDispatcher(sourceSlots[0], destinationSlots[0]))
        return
    }
    program.append(
        CopySlotsDispatcher(
            AdminInstruction.CopySlots(
                sourceSlots = sourceSlots,
                destinationSlots = destinationSlots,
            ),
        ),
    )
}

internal fun FunctionCompilationContext.emitI32Constant(value: Int, destinationSlot: Int) {
    program.append(I32ConstDispatcher(NumericSuperInstruction.I32ConstS(value, destinationSlot)))
}

internal fun FunctionCompilationContext.emitI64Constant(value: Long, destinationSlot: Int) {
    program.append(I64ConstDispatcher(NumericSuperInstruction.I64ConstS(value, destinationSlot)))
}

internal fun FunctionCompilationContext.emitF32Constant(bits: Int, destinationSlot: Int) {
    program.append(F32ConstDispatcher(NumericSuperInstruction.F32ConstS(bits, destinationSlot)))
}

internal fun FunctionCompilationContext.emitF64Constant(bits: Long, destinationSlot: Int) {
    program.append(F64ConstDispatcher(NumericSuperInstruction.F64ConstS(bits, destinationSlot)))
}
