package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.compiler.backend.abi.CALL_IMPORT
import io.github.charlietap.chasm.compiler.backend.abi.CALL_INDIRECT_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.CALL_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.CALL_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.CALL_REFERENCE_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.COMPLETE_TAIL_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.FUNCTION_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_INDIRECT_ENTRY
import io.github.charlietap.chasm.compiler.backend.abi.MODULE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.PENDING_TAIL_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TABLE
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TARGET
import io.github.charlietap.chasm.compiler.backend.abi.RESULT_REFERENCES
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.takesArgumentArray
import io.github.charlietap.chasm.compiler.backend.abi.takesReferenceArray
import io.github.charlietap.chasm.compiler.backend.abi.valueDescriptor
import io.github.charlietap.chasm.compiler.backend.analysis.form.CallSite
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.dex.abi.typedCall
import io.github.charlietap.chasm.compiler.backend.dex.analysis.SlotKindFlow
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.Label

internal fun LowerContext.lowerCall(
    instruction: ControlInstruction,
) {
    val call = checkNotNull(forms.callSite(position))
    val signature = facts.typedSignatures[position]
    when {
        instruction is ControlInstruction.Call && !declarations.isImported(instruction.function) -> directCall(instruction.function, call)
        signature != null -> typedIndirectCall(instruction, call, signature)
        else -> bridgeCall(instruction, call)
    }
    val callee = (instruction as? ControlInstruction.Call)?.function
    if (callee == null || declarations.isImported(callee) || module.analysis.mayGrowMemory[callee.index]) refreshMemory()
}

private fun LowerContext.directCall(
    callee: FunctionIndex,
    call: CallSite,
) {

    val words = loadWindow(call.type, call.arguments())
    val method = module.functionMethod(callee)
    emit.invokeRange(module.functionInvocation(callee), registers.window, 3 + words)

    val analysis = module.analysis
    finishCall(
        returns = method.returns,
        call = call,
        restoreBinding = false,
        checksUnwinding = analysis.usesExceptions && analysis.mayUnwind[callee.index],
        checksPendingTailCall = analysis.hasTailCalls && analysis.requestsTailCall[callee.index],
    )
}

private fun LowerContext.typedIndirectCall(
    instruction: ControlInstruction,
    call: CallSite,
    signature: String,
) {

    val bridge = emit.freshLabel()
    val done = emit.freshLabel()

    when (instruction) {
        is ControlInstruction.CallIndirectS -> {
            loadValueInt(Registers.I2, Resource.RuntimeType(instruction.type.index))
            loadIndirectTable(Registers.W1, instruction.table.index)
            loadInt(Registers.I0, Registers.W0, Operand.Slot(instruction.elementIndexSlot))
            emit.invoke(MANAGED_INDIRECT_ENTRY, Registers.CONTEXT, Registers.W1, Registers.I2, Registers.I0)
        }
        is ControlInstruction.CallIndirectI -> {
            loadValueInt(Registers.I2, Resource.RuntimeType(instruction.type.index))
            loadIndirectTable(Registers.W1, instruction.table.index)
            emit.const(Registers.I0, instruction.elementIndex)
            emit.invoke(MANAGED_INDIRECT_ENTRY, Registers.CONTEXT, Registers.W1, Registers.I2, Registers.I0)
        }
        is ControlInstruction.CallRefS -> {
            loadReference(Registers.OBJECT, instruction.functionSlot)
            emit.invoke(REFERENCE_TARGET, Registers.OBJECT)
        }
        else -> throw Unsupported(instruction::class.simpleName ?: "call")
    }
    emit.moveResultObject(Registers.OBJECT)
    conformingTarget(signature, bridge)
    invokeTypedTarget(call, signature, done)

    emit.place(bridge)
    bridgeCall(instruction, call)
    emit.place(done)
}

private fun LowerContext.invokeTypedTarget(
    call: CallSite,
    signature: String,
    done: Label,
) {

    emit.moveObject(registers.window, Registers.OBJECT)
    loadWindow(call.type, call.arguments())
    emit.move(registers.window + 2, Registers.DEPTH)
    emit.invokeInterfaceRange(typedCall(signature, call.type, module.referenceDescriptor), registers.window)

    finishCall(
        returns = returnDescriptor(call.type, module.referenceDescriptor),
        call = call,
        restoreBinding = true,
        checksUnwinding = module.analysis.usesExceptions,
        checksPendingTailCall = module.analysis.hasTailCalls,
    )
    emit.gotoLabel(done)
}

private fun LowerContext.conformingTarget(
    signature: String,
    bridge: Label,
) {
    emit.instanceOf(Registers.I3, Registers.OBJECT, signature)
    emit.branchZero(Cond.Eq, Registers.I3, bridge)
}

private fun LowerContext.finishCall(
    returns: String,
    call: CallSite,
    restoreBinding: Boolean,
    checksUnwinding: Boolean,
    checksPendingTailCall: Boolean,
) {

    val results = call.type.results.types.size
    if (results == 1 && !restoreBinding && !checksUnwinding && !checksPendingTailCall) {

        val slot = call.resultBase
        val kind = when (returns) {
            "I" -> SlotKindFlow.INT
            "F" -> SlotKindFlow.FLOAT
            "D" -> SlotKindFlow.DOUBLE
            OBJECT_TYPE -> SlotKindFlow.OBJECT
            else -> SlotKindFlow.LONG
        }
        val home = writeTarget(slot, kind)

        if (home >= 0) {
            when (returns) {
                "J", "D" -> emit.moveResultWide(home)
                OBJECT_TYPE -> emit.moveResultObject(home)
                else -> emit.moveResult(home)
            }
            if (returns == "J") {
                finishWide(slot, home, home)
            }
            return
        }
    }

    when (returns) {
        "V" -> Unit
        "I", "F" -> emit.moveResult(Registers.I1)
        "J", "D" -> emit.moveResultWide(Registers.W0)
        else -> emit.moveResultObject(Registers.OBJECT)
    }

    if (restoreBinding) restoreWindow(binding = true)
    if (checksUnwinding) {
        unwindCheck()
    }
    val referenceResults = results > 1 && call.type.results.types.any { type -> type is ValueType.Reference }
    if (referenceResults) {
        emit.const(Registers.I0, results)
        emit.invoke(RESULT_REFERENCES, Registers.CONTEXT, Registers.I0)
        emit.moveResultObject(Registers.THIRD_OBJECT)
    }
    if (checksPendingTailCall) {
        completePendingTailObjects(call.type, returns)
    }

    when (results) {
        0 -> Unit
        1 -> when (returns) {
            "I" -> storeInt(call.resultBase, Registers.I1)
            "F" -> storeFloat(call.resultBase, Registers.I1)
            "D" -> storeDouble(call.resultBase, Registers.W0)
            OBJECT_TYPE -> storeReference(call.resultBase, Registers.OBJECT)
            else -> store(call.resultBase, Registers.W0)
        }
        else -> {
            val types = call.type.results.types
            if (referenceResults) {
                types.forEachIndexed { index, type ->
                    if (type !is ValueType.Reference) return@forEachIndexed
                    emit.const(Registers.I1, index)
                    emit.arrayGet(ArrayKind.Object, Registers.SECOND_OBJECT, Registers.THIRD_OBJECT, Registers.I1)
                    storeReference(call.resultBase + index, Registers.SECOND_OBJECT)
                }
            }
            for (index in 0 until results) {
                if (referenceResults && types[index] is ValueType.Reference) continue
                emit.const(Registers.I2, index)
                emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
                store(call.resultBase + index, Registers.W0)
            }
        }
    }
}

private fun LowerContext.completePendingTailObjects(
    type: FunctionType,
    returns: String,
) {

    val results = type.results.types

    val done = emit.freshLabel()
    emit.getField(Registers.I0, Registers.CONTEXT, CONTEXT_TYPE, PENDING_TAIL_FIELD, FUNCTION_INSTANCE)
    emit.branchZero(Cond.Eq, Registers.I0, done)
    newLanes(maxOf(results.size, 1), words = true, references = results.any { result -> result is ValueType.Reference })
    emit.invoke(COMPLETE_TAIL_OBJECTS, Registers.CONTEXT, Registers.SECOND_OBJECT, Registers.THIRD_OBJECT, Registers.DEPTH)
    unwindCheck()

    if (results.size > 1) {
        emit.moveObject(Registers.OBJECT, Registers.SECOND_OBJECT)
    } else if (results.size == 1) {
        emit.const(Registers.I0, 0)
        when (returns) {
            OBJECT_TYPE -> emit.arrayGet(ArrayKind.Object, Registers.OBJECT, Registers.THIRD_OBJECT, Registers.I0)
            else -> {
                emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.SECOND_OBJECT, Registers.I0)
                when (returns) {
                    "I" -> emit.convert(Conversion.LongToInt, Registers.I1, Registers.W0)
                    "F" -> {
                        emit.convert(Conversion.LongToInt, Registers.I1, Registers.W0)
                        emit.invoke(INT_BITS_TO_FLOAT, Registers.I1)
                        emit.moveResult(Registers.I1)
                    }
                    "D" -> {
                        emit.invoke(LONG_BITS_TO_DOUBLE, Registers.W0, Registers.W0 + 1)
                        emit.moveResultWide(Registers.W0)
                    }
                }
            }
        }
    }
    emit.place(done)
}

internal fun LowerContext.newLanes(
    size: Int,
    words: Boolean,
    references: Boolean,
) {

    emit.const(Registers.I0, size)
    if (words) {
        emit.newArray(Registers.SECOND_OBJECT, Registers.I0, "[J")
    } else {
        emit.const(Registers.SECOND_OBJECT, 0)
    }
    if (references) {
        emit.newArray(Registers.THIRD_OBJECT, Registers.I0, OBJECT_ARRAY)
    } else {
        emit.const(Registers.THIRD_OBJECT, 0)
    }
}

internal fun LowerContext.loadCallBuffer() {
    val ready = emit.freshLabel()
    emit.moveObject(Registers.OBJECT, registers.callBuffer)
    emit.branchZero(Cond.Ne, Registers.OBJECT, ready)
    emit.const(Registers.I0, facts.window.callBufferWords)
    emit.newArray(Registers.OBJECT, Registers.I0, "[J")
    emit.moveObject(registers.callBuffer, Registers.OBJECT)
    emit.place(ready)
}

internal fun LowerContext.loadWindow(
    type: FunctionType,
    arguments: List<Operand>,
): Int {
    val window = registers.window
    if (takesArgumentArray(type)) {
        val referenceArray = takesReferenceArray(type, module.referenceDescriptor)
        emit.const(Registers.I0, arguments.size)
        emit.newArray(Registers.OBJECT, Registers.I0, "[J")
        arguments.forEachIndexed { index, argument ->
            if (referenceArray && type.params.types[index] is ValueType.Reference) return@forEachIndexed
            loadWide(Registers.W0, argument)
            emit.const(Registers.I2, index)
            emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
        }
        emit.moveObject(window + 3, Registers.OBJECT)
        if (!referenceArray) return 1
        emit.const(Registers.I0, arguments.size)
        emit.newArray(Registers.SECOND_OBJECT, Registers.I0, OBJECT_ARRAY)
        arguments.forEachIndexed { index, argument ->
            if (type.params.types[index] !is ValueType.Reference) return@forEachIndexed
            loadReference(Registers.OBJECT, argument)
            emit.const(Registers.I1, index)
            emit.arrayPut(ArrayKind.Object, Registers.OBJECT, Registers.SECOND_OBJECT, Registers.I1)
        }
        emit.moveObject(window + 4, Registers.SECOND_OBJECT)
        return 2
    }
    var words = 0
    arguments.forEachIndexed { index, argument ->
        val descriptor = valueDescriptor(type.params.types[index], module.referenceDescriptor)
        loadArgument(window + 3 + words, argument, descriptor)
        words += descriptorWords(descriptor)
    }
    return words
}

internal fun LowerContext.loadArgument(
    register: Int,
    argument: Operand,
    descriptor: String,
) {
    when (descriptor) {
        "I", "F" -> when {
            argument is Operand.Slot && descriptor == "I" && readsInt(argument.slot) ->
                emit.move(register, registers.home(argument.slot, SlotKindFlow.INT))
            argument is Operand.Slot && descriptor == "F" && readsFloat(argument.slot) ->
                emit.move(register, registers.home(argument.slot, SlotKindFlow.FLOAT))
            argument is Operand.Immediate && register < 256 -> emit.const(register, argument.bits.toInt())
            else -> {
                if (descriptor == "I") loadInt(Registers.I0, Registers.W0, argument) else loadFloat(Registers.I0, Registers.W0, argument)
                emit.move(register, Registers.I0)
            }
        }
        "D" -> when {
            argument is Operand.Slot && readsDouble(argument.slot) -> emit.moveWide(register, registers.home(argument.slot, SlotKindFlow.DOUBLE))
            argument is Operand.Immediate && register < 256 -> emit.constWide(register, argument.bits)
            else -> {
                loadDouble(Registers.W0, argument)
                emit.moveWide(register, Registers.W0)
            }
        }
        OBJECT_TYPE -> loadReference(register, argument)
        else -> when {
            argument is Operand.Slot && readsLong(argument.slot) -> emit.moveWide(register, registers.home(argument.slot, SlotKindFlow.LONG))
            argument is Operand.Immediate && register < 256 -> emit.constWide(register, argument.bits)
            else -> {
                loadWide(Registers.W0, argument)
                emit.moveWide(register, Registers.W0)
            }
        }
    }
}

private fun LowerContext.bridgeCall(
    instruction: ControlInstruction,
    call: CallSite,
) {
    val indirect = instruction is ControlInstruction.CallIndirectI || instruction is ControlInstruction.CallIndirectS
    if (indirect || (call.type.params.types + call.type.results.types).any { type -> type is ValueType.Reference }) {
        return bridgeObjects(instruction, call)
    }
    loadCallBuffer()
    call.arguments().forEachIndexed { index, argument ->
        loadWide(Registers.W0, argument)
        emit.const(Registers.I2, index)
        emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
    }
    when (instruction) {
        is ControlInstruction.Call -> {
            loadObject(Registers.W1, Resource.Instance, MODULE_INSTANCE)
            loadObject(Registers.W1 + 1, Resource.Function(instruction.function.index), FUNCTION_INSTANCE)
            emit.invoke(CALL_IMPORT, Registers.CONTEXT, Registers.W1, Registers.W1 + 1, Registers.OBJECT, Registers.DEPTH)
        }
        is ControlInstruction.CallRefS -> {
            val window = registers.window + 1
            emit.moveObject(window, Registers.CONTEXT)
            loadObject(Registers.W1, Resource.Instance, MODULE_INSTANCE)
            emit.moveObject(window + 1, Registers.W1)
            emit.moveObject(window + 3, Registers.OBJECT)
            loadReference(window + 2, instruction.functionSlot)
            emit.move(window + 4, Registers.DEPTH)
            emit.invokeRange(CALL_REFERENCE, window, CALL_REFERENCE.words)
            restoreWindow()
            emit.moveObject(Registers.OBJECT, registers.callBuffer)
        }
        else -> throw Unsupported(instruction::class.simpleName ?: "call")
    }
    unwindCheck()
    for (index in call.type.results.types.indices) {
        emit.const(Registers.I2, index)
        emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
        store(call.resultBase + index, Registers.W0)
    }
}

private fun LowerContext.bridgeObjects(
    instruction: ControlInstruction,
    call: CallSite,
) {

    val parameters = call.type.params.types
    val results = call.type.results.types
    newLanes(maxOf(parameters.size, results.size, 1), words = true, references = (parameters + results).any { type -> type is ValueType.Reference })

    call.arguments().forEachIndexed { index, argument ->
        emit.const(Registers.I1, index)
        if (parameters[index] is ValueType.Reference) {
            loadReference(Registers.OBJECT, argument)
            emit.arrayPut(ArrayKind.Object, Registers.OBJECT, Registers.THIRD_OBJECT, Registers.I1)
        } else {
            loadWide(Registers.W0, argument)
            emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.SECOND_OBJECT, Registers.I1)
        }
    }

    val window = registers.window + 1
    emit.moveObject(window, Registers.CONTEXT)
    loadObject(Registers.OBJECT, Resource.Instance, MODULE_INSTANCE)
    emit.moveObject(window + 1, Registers.OBJECT)
    when (instruction) {
        is ControlInstruction.Call -> {
            loadObject(Registers.OBJECT, Resource.Function(instruction.function.index), FUNCTION_INSTANCE)
            emit.moveObject(window + 2, Registers.OBJECT)
            objectLane(window + 3)
            emit.invokeRange(CALL_OBJECTS, window, CALL_OBJECTS.words)
        }
        is ControlInstruction.CallIndirectI, is ControlInstruction.CallIndirectS -> {
            val table = if (instruction is ControlInstruction.CallIndirectI) instruction.table.index else (instruction as ControlInstruction.CallIndirectS).table.index
            val type = if (instruction is ControlInstruction.CallIndirectI) instruction.type.index else (instruction as ControlInstruction.CallIndirectS).type.index
            val element = if (instruction is ControlInstruction.CallIndirectI) Operand.Immediate(instruction.elementIndex.toLong()) else Operand.Slot((instruction as ControlInstruction.CallIndirectS).elementIndexSlot)
            loadObject(Registers.OBJECT, Resource.ReferenceTable(table), REFERENCE_TABLE)
            emit.moveObject(window + 2, Registers.OBJECT)
            loadValueInt(Registers.I0, Resource.RuntimeType(type))
            emit.move(window + 3, Registers.I0)
            loadInt(Registers.I0, Registers.W0, element)
            emit.move(window + 4, Registers.I0)
            objectLane(window + 5)
            emit.invokeRange(CALL_INDIRECT_OBJECTS, window, CALL_INDIRECT_OBJECTS.words)
        }
        is ControlInstruction.CallRefS -> {
            loadReference(window + 2, instruction.functionSlot)
            objectLane(window + 3)
            emit.invokeRange(CALL_REFERENCE_OBJECTS, window, CALL_REFERENCE_OBJECTS.words)
        }
        else -> throw Unsupported(instruction::class.simpleName ?: "call")
    }
    restoreWindow()
    unwindCheck()

    results.forEachIndexed { index, type ->
        emit.const(Registers.I1, index)
        if (type is ValueType.Reference) {
            emit.arrayGet(ArrayKind.Object, Registers.OBJECT, Registers.THIRD_OBJECT, Registers.I1)
            storeReference(call.resultBase + index, Registers.OBJECT)
        } else {
            emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.SECOND_OBJECT, Registers.I1)
            store(call.resultBase + index, Registers.W0)
        }
    }
}

private fun LowerContext.loadIndirectTable(
    register: Int,
    table: Int,
) {
    loadObject(register, Resource.ReferenceTable(table), REFERENCE_TABLE)
}

private fun LowerContext.objectLane(first: Int) {
    emit.moveObject(first, Registers.SECOND_OBJECT)
    emit.moveObject(first + 1, Registers.THIRD_OBJECT)
    emit.move(first + 2, Registers.DEPTH)
}

private const val OBJECT_ARRAY = "[Ljava/lang/Object;"
