package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.compiler.backend.abi.ARRAYCOPY
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.FUNCTION_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MODULE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TABLE
import io.github.charlietap.chasm.compiler.backend.abi.REQUEST_TAIL_INDIRECT_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.REQUEST_TAIL_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.REQUEST_TAIL_REFERENCE_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.tailCallArguments
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.dexter.ArrayKind

internal fun LowerContext.lowerTailCall(
    instruction: ControlInstruction,
) {
    val arguments = checkNotNull(instruction.tailCallArguments())
    if (facts.table.restartsFunction(position)) {
        restart(arguments)
        return
    }
    tailCallObjects(instruction, arguments)
}

private fun LowerContext.tailCallObjects(
    instruction: ControlInstruction,
    arguments: List<Operand>,
) {

    val type = when (instruction) {
        is ControlInstruction.ReturnCall -> declarations.functionType(declarations.functionTypeIndex(instruction.function))
        is ControlInstruction.ReturnCallIndirectI -> declarations.functionType(instruction.type)
        is ControlInstruction.ReturnCallIndirectS -> declarations.functionType(instruction.type)
        is ControlInstruction.ReturnCallRefS -> declarations.functionType(instruction.type)
        else -> throw Unsupported(instruction::class.simpleName ?: "tail call")
    }
    newLanes(maxOf(arguments.size, resultCount, 1), words = true, references = (type.params.types + type.results.types).any { value -> value is ValueType.Reference })
    arguments.forEachIndexed { index, argument ->
        emit.const(Registers.I1, index)
        if (type.params.types[index] is ValueType.Reference) {
            loadReference(Registers.OBJECT, argument)
            emit.arrayPut(ArrayKind.Object, Registers.OBJECT, Registers.THIRD_OBJECT, Registers.I1)
        } else {
            loadWide(Registers.W0, argument)
            emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.SECOND_OBJECT, Registers.I1)
        }
    }

    when (instruction) {
        is ControlInstruction.ReturnCall -> {
            loadObject(Registers.W1, Resource.Instance, MODULE_INSTANCE)
            loadObject(Registers.W1 + 1, Resource.Function(instruction.function.index), FUNCTION_INSTANCE)
            emit.invoke(REQUEST_TAIL_OBJECTS, Registers.CONTEXT, Registers.W1, Registers.W1 + 1, Registers.SECOND_OBJECT, Registers.THIRD_OBJECT)
        }
        is ControlInstruction.ReturnCallIndirectI ->
            recordIndirectObjects(instruction.table.index, instruction.type.index, Operand.Immediate(instruction.elementIndex.toLong()))
        is ControlInstruction.ReturnCallIndirectS ->
            recordIndirectObjects(instruction.table.index, instruction.type.index, Operand.Slot(instruction.elementIndexSlot))
        is ControlInstruction.ReturnCallRefS -> {
            loadObject(Registers.W1, Resource.Instance, MODULE_INSTANCE)
            loadReference(Registers.OBJECT, instruction.functionSlot)
            emit.invoke(REQUEST_TAIL_REFERENCE_OBJECTS, Registers.CONTEXT, Registers.W1, Registers.OBJECT, Registers.SECOND_OBJECT, Registers.THIRD_OBJECT)
        }
        else -> throw Unsupported(instruction::class.simpleName ?: "tail call")
    }
    returnUnreachable()
}

private fun LowerContext.recordIndirectObjects(
    table: Int,
    type: Int,
    elementIndex: Operand,
) {
    val window = registers.window + 1
    emit.moveObject(window, Registers.CONTEXT)
    emit.moveObject(window + 5, Registers.SECOND_OBJECT)
    emit.moveObject(window + 6, Registers.THIRD_OBJECT)
    loadValueInt(Registers.I0, Resource.RuntimeType(type))
    emit.move(window + 3, Registers.I0)
    loadObject(Registers.W1, Resource.Instance, MODULE_INSTANCE)
    emit.moveObject(window + 1, Registers.W1)
    loadObject(Registers.W1, Resource.ReferenceTable(table), REFERENCE_TABLE)
    emit.moveObject(window + 2, Registers.W1)
    loadInt(Registers.I0, Registers.W0, elementIndex)
    emit.move(window + 4, Registers.I0)
    emit.invokeRange(REQUEST_TAIL_INDIRECT_OBJECTS, window, REQUEST_TAIL_INDIRECT_OBJECTS.words)
}

private const val OBJECT_ARRAY = "[Ljava/lang/Object;"

private fun LowerContext.restart(
    arguments: List<Operand>,
) {

    if (chunk != null) return chunkRestart(arguments)

    parallelCopy(arguments, arguments.indices.toList())
    initializeLocals()
    emit.gotoLabel(label(0))
}

private fun LowerContext.chunkRestart(
    arguments: List<Operand>,
) {
    emit.invoke(FRAME, Registers.CONTEXT)
    emit.moveResultObject(Registers.OBJECT)
    arguments.forEachIndexed { index, argument ->
        if (facts.frame.type.params.types[index] is ValueType.Reference) {
            loadReference(Registers.THIRD_OBJECT, argument)
            spillReference(index, Registers.THIRD_OBJECT)
            return@forEachIndexed
        }
        loadWide(Registers.W0, argument)
        frameIndex(Registers.I2, index)
        emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
    }
    resetFrame(Registers.OBJECT)
    if (parameterCount < frameSlots) {
        emit.moveObject(Registers.SECOND_OBJECT, registers.objectFrame)
        emit.const(Registers.I0, parameterCount)
        emit.const(Registers.I1, frameSlots)
        emit.const(Registers.THIRD_OBJECT, 0)
        emit.invoke(FILL_OBJECT_RANGE, Registers.SECOND_OBJECT, Registers.I0, Registers.I1, Registers.THIRD_OBJECT)
    }
    returnCode(0)
}

private val FILL_OBJECT_RANGE = MethodRef("Ljava/util/Arrays;", "fill", listOf(OBJECT_ARRAY, "I", "I", OBJECT_TYPE), "V")

internal fun LowerContext.resetFrame(
    frame: Int,
) {
    if (parameterCount >= frameSlots) return
    loadLongs(Registers.W1, facts.frame.initialValues.copyOfRange(parameterCount, frameSlots))
    emit.const(Registers.I0, 0)
    emit.moveObject(Registers.W1 + 1, frame)
    frameIndex(Registers.I2, parameterCount)
    emit.const(Registers.I3, frameSlots - parameterCount)
    emit.invoke(ARRAYCOPY, Registers.W1, Registers.I0, Registers.W1 + 1, Registers.I2, Registers.I3)
}
