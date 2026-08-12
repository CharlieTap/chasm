package io.github.charlietap.chasm.compiler.instruction

import io.github.charlietap.chasm.compiler.context.FunctionCompilationContext
import io.github.charlietap.chasm.executor.invoker.dispatch.referencefused.ReferenceSuperInstructionDispatcher
import io.github.charlietap.chasm.runtime.instruction.ReferenceSuperInstruction
import io.github.charlietap.chasm.runtime.type.ReferenceTypeTest

internal fun FunctionCompilationContext.emitReferenceInstruction(
    instruction: ReferenceSuperInstruction,
) {
    emit(instruction, ::ReferenceSuperInstructionDispatcher)
}

internal fun FunctionCompilationContext.emitRefNull(reference: Long, destinationSlot: Int) =
    emitReferenceInstruction(ReferenceSuperInstruction.RefNullS(reference, destinationSlot))

internal fun FunctionCompilationContext.emitRefFunc(reference: Long, destinationSlot: Int) =
    emitReferenceInstruction(ReferenceSuperInstruction.RefFuncS(reference, destinationSlot))

internal fun FunctionCompilationContext.emitRefIsNull(sourceSlot: Int, destinationSlot: Int) =
    emitReferenceInstruction(ReferenceSuperInstruction.RefIsNullS(sourceSlot, destinationSlot))

internal fun FunctionCompilationContext.emitRefAsNonNull(sourceSlot: Int, destinationSlot: Int) =
    emitReferenceInstruction(ReferenceSuperInstruction.RefAsNonNullS(sourceSlot, destinationSlot))

internal fun FunctionCompilationContext.emitRefEq(firstSlot: Int, secondSlot: Int, destinationSlot: Int) =
    emitReferenceInstruction(ReferenceSuperInstruction.RefEqSs(firstSlot, secondSlot, destinationSlot))

internal fun FunctionCompilationContext.emitRefTest(
    sourceSlot: Int,
    destinationSlot: Int,
    typeTest: ReferenceTypeTest,
) = emitReferenceInstruction(ReferenceSuperInstruction.RefTestS(sourceSlot, destinationSlot, typeTest))

internal fun FunctionCompilationContext.emitRefCast(
    sourceSlot: Int,
    destinationSlot: Int,
    typeTest: ReferenceTypeTest,
) = emitReferenceInstruction(ReferenceSuperInstruction.RefCastS(sourceSlot, destinationSlot, typeTest))
