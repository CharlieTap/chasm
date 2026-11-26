package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.backend.abi.ARRAYCOPY
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.REQUEST_TAIL_INDIRECT_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.REQUEST_TAIL_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.REQUEST_TAIL_REFERENCE_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.tailCallArguments
import io.github.charlietap.chasm.compiler.backend.analysis.isSelfTailCall
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import org.objectweb.asm.Opcodes

internal fun LowerContext.lowerTailCall(
    instruction: ControlInstruction,
) {
    val arguments = checkNotNull(instruction.tailCallArguments())
    when (instruction) {
        is ControlInstruction.ReturnCall -> {
            if (instruction.isSelfTailCall(function, regionAt(position))) return selfTailCall(arguments)
            requestTailCall(arguments, declarations.functionType(instruction.function)) {
                pushResource(Resource.Function(instruction.function.index))
                REQUEST_TAIL_OBJECTS
            }
        }
        is ControlInstruction.ReturnCallIndirectI -> requestTailCall(arguments, declarations.functionType(instruction.type)) {
            pushResource(Resource.ReferenceTable(instruction.table.index))
            pushResource(Resource.RuntimeType(instruction.type.index))
            emit.int(instruction.elementIndex)
            REQUEST_TAIL_INDIRECT_OBJECTS
        }
        is ControlInstruction.ReturnCallIndirectS -> requestTailCall(arguments, declarations.functionType(instruction.type)) {
            pushResource(Resource.ReferenceTable(instruction.table.index))
            pushResource(Resource.RuntimeType(instruction.type.index))
            pushInt(Operand.Slot(instruction.elementIndexSlot))
            REQUEST_TAIL_INDIRECT_OBJECTS
        }
        is ControlInstruction.ReturnCallRefS -> requestTailCall(arguments, declarations.functionType(instruction.type)) {
            pushObject(Operand.Slot(instruction.functionSlot))
            REQUEST_TAIL_REFERENCE_OBJECTS
        }
        else -> unsupported(instruction)
    }
}

private fun LowerContext.selfTailCall(
    arguments: List<Operand>,
) {

    val chunk = chunk
    if (chunk != null) {
        chunkSelfTailCall(chunk, arguments)
        return
    }

    transfer(arguments, 0)
    for (slot in parameterCount until frame.frameSlots) initialize(slot, frame.initialValues[slot])
    emit.jump(Opcodes.GOTO, label(0))
}

private fun LowerContext.chunkSelfTailCall(
    chunk: Chunk,
    arguments: List<Operand>,
) {

    pushContext()
    emit.invokeStatic(FRAME)
    emit.store(Opcodes.ASTORE, locals.frameArray)

    val parameters = type.params.types
    arguments.forEachIndexed { index, argument ->
        if (parameters[index] is ValueType.Reference) {
            emit.load(Opcodes.ALOAD, locals.objectFrame)
            emit.int(index)
            pushObject(argument)
            emit.insn(Opcodes.AASTORE)
        } else {
            emit.load(Opcodes.ALOAD, locals.frameArray)
            emit.load(Opcodes.ILOAD, locals.frameBase)
            emit.int(index)
            emit.insn(Opcodes.IADD)
            pushLong(argument)
            emit.insn(Opcodes.LASTORE)
        }
    }

    if (chunk.resetsSlots) {
        resetSlots(locals.frameArray)
    }

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
    emit.load(Opcodes.ILOAD, locals.frameBase)
    emit.int(parameterCount)
    emit.insn(Opcodes.IADD)
    emit.int(this.frame.frameSlots - parameterCount)
    emit.invokeStatic(ARRAYCOPY)

    if (locals.holdsObjects) {
        emit.load(Opcodes.ALOAD, locals.objectFrame)
        emit.int(parameterCount)
        emit.int(this.frame.frameSlots)
        emit.insn(Opcodes.ACONST_NULL)
        emit.invokeStatic(FILL_OBJECT_RANGE)
    }
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
    emit.load(Opcodes.ALOAD, locals.scratchReferences)
    emit.invokeStatic(method)
    returnNothing()
}

private val FILL_OBJECT_RANGE = MethodRef("Ljava/util/Arrays;", "fill", listOf("[Ljava/lang/Object;", "I", "I", OBJECT_TYPE), "V")
