package io.github.charlietap.chasm.compiler.backend.dex.lower

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_CALL
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_CALL_INDIRECT
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_CALL_REF
import io.github.charlietap.chasm.compiler.backend.abi.COMPLETE_TAIL_CALL
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.FUNCTION_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.INDIRECT_TARGET
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MODULE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.PENDING_TAIL_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.REF_TARGET
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.TABLE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.takesArgumentArray
import io.github.charlietap.chasm.compiler.backend.abi.valueDescriptor
import io.github.charlietap.chasm.compiler.backend.analysis.form.CallSite
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.callSite
import io.github.charlietap.chasm.compiler.backend.dex.abi.typedCall
import io.github.charlietap.chasm.compiler.backend.dex.analysis.SlotKindFlow
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.dexter.ArrayKind
import io.github.charlietap.dexter.Cond
import io.github.charlietap.dexter.Conversion
import io.github.charlietap.dexter.Label

internal fun LowerContext.lowerCall(
    instruction: ControlInstruction,
) {
    val call = checkNotNull(instruction.callSite(declarations))
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

    val words = loadWindow(call.type, call.arguments)
    val method = module.functionMethod(callee, call.type)
    emit.invokeRange(method, registers.window, 3 + words)

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
            loadObject(Registers.W1, Resource.Table(instruction.table.index), TABLE_INSTANCE)
            loadInt(Registers.I0, Registers.W0, Operand.Slot(instruction.elementIndexSlot))
            emit.invoke(INDIRECT_TARGET, Registers.CONTEXT, Registers.W1, Registers.I2, Registers.I0)
        }
        is ControlInstruction.CallIndirectI -> {
            loadValueInt(Registers.I2, Resource.RuntimeType(instruction.type.index))
            loadObject(Registers.W1, Resource.Table(instruction.table.index), TABLE_INSTANCE)
            emit.const(Registers.I0, instruction.elementIndex)
            emit.invoke(INDIRECT_TARGET, Registers.CONTEXT, Registers.W1, Registers.I2, Registers.I0)
        }
        is ControlInstruction.CallRefS -> {
            loadWide(Registers.W2, instruction.functionSlot)
            emit.invoke(REF_TARGET, Registers.CONTEXT, Registers.W2, Registers.W2 + 1)
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
    loadWindow(call.type, call.arguments)
    emit.move(registers.window + 2, Registers.DEPTH)
    emit.invokeInterfaceRange(typedCall(signature, call.type), registers.window)

    finishCall(
        returns = returnDescriptor(call.type),
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
    emit.checkCast(Registers.OBJECT, signature)
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
            else -> SlotKindFlow.LONG
        }
        val home = writeTarget(slot, kind)

        if (home >= 0) {
            if (returns == "J" || returns == "D") {
                emit.moveResultWide(home)
            } else {
                emit.moveResult(home)
            }
            roots.refreshAfterCall(position)
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
    if (checksPendingTailCall) {
        completePendingTailCall(returns, results)
    }
    roots.refreshAfterCall(position)

    when (results) {
        0 -> Unit
        1 -> when (returns) {
            "I" -> storeInt(call.resultBase, Registers.I1)
            "F" -> storeFloat(call.resultBase, Registers.I1)
            "D" -> storeDouble(call.resultBase, Registers.W0)
            else -> store(call.resultBase, Registers.W0)
        }
        else -> for (index in 0 until results) {
            emit.const(Registers.I2, index)
            emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
            store(call.resultBase + index, Registers.W0)
        }
    }
}

private fun LowerContext.completePendingTailCall(
    returns: String,
    results: Int,
) {
    val done = emit.freshLabel()
    emit.getField(Registers.I0, Registers.CONTEXT, CONTEXT_TYPE, PENDING_TAIL_FIELD, FUNCTION_INSTANCE)
    emit.branchZero(Cond.Eq, Registers.I0, done)
    emit.const(Registers.I0, maxOf(results, 1))
    emit.newArray(Registers.OBJECT, Registers.I0, "[J")
    emit.invoke(COMPLETE_TAIL_CALL, Registers.CONTEXT, Registers.OBJECT, Registers.DEPTH)
    unwindCheck()
    if (results == 1) {
        emit.const(Registers.I2, 0)
        emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
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
    emit.place(done)
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
        emit.const(Registers.I0, arguments.size)
        emit.newArray(Registers.OBJECT, Registers.I0, "[J")
        arguments.forEachIndexed { index, argument ->
            loadWide(Registers.W0, argument)
            emit.const(Registers.I2, index)
            emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
        }
        emit.moveObject(window + 3, Registers.OBJECT)
        return 1
    }
    var words = 0
    arguments.forEachIndexed { index, argument ->
        val descriptor = valueDescriptor(type.params.types[index])
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
    loadCallBuffer()
    call.arguments.forEachIndexed { index, argument ->
        loadWide(Registers.W0, argument)
        emit.const(Registers.I2, index)
        emit.arrayPut(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
    }
    when (instruction) {
        is ControlInstruction.Call -> {
            loadObject(Registers.W1, Resource.Instance, MODULE_INSTANCE)
            loadObject(Registers.W1 + 1, Resource.Function(instruction.function.index), FUNCTION_INSTANCE)
            emit.invoke(BRIDGE_CALL, Registers.CONTEXT, Registers.W1, Registers.W1 + 1, Registers.OBJECT, Registers.DEPTH)
        }
        is ControlInstruction.CallIndirectI ->
            bridgeIndirect(instruction.table.index, instruction.type.index, Operand.Immediate(instruction.elementIndex.toLong()))
        is ControlInstruction.CallIndirectS ->
            bridgeIndirect(instruction.table.index, instruction.type.index, Operand.Slot(instruction.elementIndexSlot))
        is ControlInstruction.CallRefS -> {
            val window = registers.window + 1
            emit.moveObject(window + 4, Registers.OBJECT)
            emit.moveObject(window, Registers.CONTEXT)
            loadObject(Registers.W1, Resource.Instance, MODULE_INSTANCE)
            emit.moveObject(window + 1, Registers.W1)
            loadWide(Registers.W2, instruction.functionSlot)
            emit.moveWide(window + 2, Registers.W2)
            emit.move(window + 5, Registers.DEPTH)
            emit.invokeRange(BRIDGE_CALL_REF, window, BRIDGE_CALL_REF.words)
            restoreWindow()
            emit.moveObject(Registers.OBJECT, registers.callBuffer)
        }
        else -> throw Unsupported(instruction::class.simpleName ?: "call")
    }
    unwindCheck()
    roots.refreshAfterCall(position)
    for (index in call.type.results.types.indices) {
        emit.const(Registers.I2, index)
        emit.arrayGet(ArrayKind.Wide, Registers.W0, Registers.OBJECT, Registers.I2)
        store(call.resultBase + index, Registers.W0)
    }
}

private fun LowerContext.bridgeIndirect(
    table: Int,
    type: Int,
    elementIndex: Operand,
) {
    val window = registers.window + 1
    emit.moveObject(window + 5, Registers.OBJECT)
    emit.moveObject(window, Registers.CONTEXT)
    loadValueInt(Registers.I0, Resource.RuntimeType(type))
    emit.move(window + 3, Registers.I0)
    loadObject(Registers.W1, Resource.Instance, MODULE_INSTANCE)
    emit.moveObject(window + 1, Registers.W1)
    loadObject(Registers.W1, Resource.Table(table), TABLE_INSTANCE)
    emit.moveObject(window + 2, Registers.W1)
    loadInt(Registers.I0, Registers.W0, elementIndex)
    emit.move(window + 4, Registers.I0)
    emit.move(window + 6, Registers.DEPTH)
    emit.invokeRange(BRIDGE_CALL_INDIRECT, window, BRIDGE_CALL_INDIRECT.words)
    restoreWindow()
    emit.moveObject(Registers.OBJECT, registers.callBuffer)
}
