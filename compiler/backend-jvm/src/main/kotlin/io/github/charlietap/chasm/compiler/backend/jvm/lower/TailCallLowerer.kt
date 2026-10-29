package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.backend.abi.ARRAYCOPY
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.TAIL_CALL
import io.github.charlietap.chasm.compiler.backend.abi.TAIL_CALL_INDIRECT
import io.github.charlietap.chasm.compiler.backend.abi.TAIL_CALL_REF
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.tailCallArguments
import io.github.charlietap.chasm.type.FunctionType
import org.objectweb.asm.Opcodes

internal fun LowerContext.lowerTailCall(
    instruction: ControlInstruction,
) {
    val arguments = checkNotNull(instruction.tailCallArguments())
    when (instruction) {
        is ControlInstruction.ReturnCall -> {
            if (instruction.function == function.index && regionAt(position) < 0) return selfTailCall(arguments)
            requestTailCall(arguments, instruction.function.let { callee -> declarations.functionType(callee) }) {
                pushResource(Resource.Function(instruction.function.index))
                TAIL_CALL
            }
        }
        is ControlInstruction.ReturnCallIndirectI -> requestTailCall(arguments, declarations.functionType(instruction.type)) {
            pushResource(Resource.Table(instruction.table.index))
            pushResource(Resource.RuntimeType(instruction.type.index))
            emit.int(instruction.elementIndex)
            TAIL_CALL_INDIRECT
        }
        is ControlInstruction.ReturnCallIndirectS -> requestTailCall(arguments, declarations.functionType(instruction.type)) {
            pushResource(Resource.Table(instruction.table.index))
            pushResource(Resource.RuntimeType(instruction.type.index))
            pushInt(Operand.Slot(instruction.elementIndexSlot))
            TAIL_CALL_INDIRECT
        }
        is ControlInstruction.ReturnCallRefS -> requestTailCall(arguments, declarations.functionType(instruction.type)) {
            pushLong(Operand.Slot(instruction.functionSlot))
            TAIL_CALL_REF
        }
        else -> unsupported(instruction)
    }
}

private fun LowerContext.selfTailCall(
    arguments: List<Operand>,
) {
    if (chunk != null) return chunkSelfTailCall(arguments)
    transfer(arguments, 0)
    for (slot in parameterCount until frame.frameSlots) initialize(slot, frame.initialValues[slot])
    emit.jump(Opcodes.GOTO, label(0))
}

private fun LowerContext.chunkSelfTailCall(
    arguments: List<Operand>,
) {
    pushContext()
    emit.invokeStatic(FRAME)
    emit.store(Opcodes.ASTORE, locals.scratchValues)
    arguments.forEachIndexed { index, argument ->
        emit.load(Opcodes.ALOAD, locals.scratchValues)
        emit.load(Opcodes.ILOAD, locals.rootBase)
        emit.int(index)
        emit.insn(Opcodes.IADD)
        pushLong(argument)
        emit.insn(Opcodes.LASTORE)
    }
    resetSlots(locals.scratchValues)
    emit.int(0)
    emit.insn(Opcodes.IRETURN)
}

internal fun LowerContext.resetSlots(
    frame: Int,
) {
    if (parameterCount >= this.frame.frameSlots) return
    pushResource(Resource.Longs(this.frame.initialValues.copyOfRange(parameterCount, this.frame.frameSlots)))
    emit.int(0)
    emit.load(Opcodes.ALOAD, frame)
    emit.load(Opcodes.ILOAD, locals.rootBase)
    emit.int(parameterCount)
    emit.insn(Opcodes.IADD)
    emit.int(this.frame.frameSlots - parameterCount)
    emit.invokeStatic(ARRAYCOPY)
}

private inline fun LowerContext.requestTailCall(
    arguments: List<Operand>,
    type: FunctionType,
    target: () -> MethodRef,
) {
    packArguments(type, arguments)
    pushContext()
    pushResource(Resource.Instance)
    val method = target()
    emit.load(Opcodes.ALOAD, locals.scratchValues)
    emit.invokeStatic(method)
    closeRoots()
    returnNothing()
}
