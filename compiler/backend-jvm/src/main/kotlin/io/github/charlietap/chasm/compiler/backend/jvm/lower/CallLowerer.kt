package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_CALL
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_CALL_INDIRECT
import io.github.charlietap.chasm.compiler.backend.abi.BRIDGE_CALL_REF
import io.github.charlietap.chasm.compiler.backend.abi.COMPLETE_TAIL_CALL
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FUNCTION_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.INDIRECT_TARGET
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.PENDING_TAIL_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.REF_TARGET
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.takesArgumentArray
import io.github.charlietap.chasm.compiler.backend.abi.valueDescriptor
import io.github.charlietap.chasm.compiler.backend.analysis.form.CallSite
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.callSite
import io.github.charlietap.chasm.type.FunctionType
import org.objectweb.asm.Opcodes

internal fun LowerContext.lowerCall(
    instruction: ControlInstruction,
) {
    val call = checkNotNull(instruction.callSite(declarations))
    when (instruction) {
        is ControlInstruction.Call -> if (declarations.isImported(instruction.function)) {
            runtimeCall(call) {
                pushResource(Resource.Function(instruction.function.index))
                BRIDGE_CALL
            }
        } else {
            directCall(instruction.function, call)
        }
        is ControlInstruction.CallIndirectI -> indirectCall(
            call,
            target = {
                pushContext()
                pushResource(Resource.Table(instruction.table.index))
                pushResource(Resource.RuntimeType(instruction.type.index))
                emit.int(instruction.elementIndex)
                emit.invokeStatic(INDIRECT_TARGET)
            },
        ) {
            runtimeCall(call) {
                pushResource(Resource.Table(instruction.table.index))
                pushResource(Resource.RuntimeType(instruction.type.index))
                emit.int(instruction.elementIndex)
                BRIDGE_CALL_INDIRECT
            }
        }
        is ControlInstruction.CallIndirectS -> indirectCall(
            call,
            target = {
                pushContext()
                pushResource(Resource.Table(instruction.table.index))
                pushResource(Resource.RuntimeType(instruction.type.index))
                pushInt(Operand.Slot(instruction.elementIndexSlot))
                emit.invokeStatic(INDIRECT_TARGET)
            },
        ) {
            runtimeCall(call) {
                pushResource(Resource.Table(instruction.table.index))
                pushResource(Resource.RuntimeType(instruction.type.index))
                pushInt(Operand.Slot(instruction.elementIndexSlot))
                BRIDGE_CALL_INDIRECT
            }
        }
        is ControlInstruction.CallRefS -> indirectCall(
            call,
            target = {
                pushContext()
                pushLong(Operand.Slot(instruction.functionSlot))
                emit.invokeStatic(REF_TARGET)
            },
        ) {
            runtimeCall(call) {
                pushLong(Operand.Slot(instruction.functionSlot))
                BRIDGE_CALL_REF
            }
        }
        else -> unsupported(instruction)
    }
}

private inline fun LowerContext.indirectCall(
    call: CallSite,
    target: () -> Unit,
    slow: () -> Unit,
) {
    val signature = module.signature(call.type) ?: return slow()
    val runtime = emit.label()
    val done = emit.label()
    target()
    emit.insn(Opcodes.DUP)
    emit.type(Opcodes.INSTANCEOF, signature)
    emit.jump(Opcodes.IFEQ, runtime)
    emit.type(Opcodes.CHECKCAST, signature)
    pushContext()
    emit.load(Opcodes.ILOAD, Locals.DEPTH)
    if (takesArgumentArray(call.type)) pushArgumentArray(call.arguments) else pushArguments(call.type, call.arguments)
    emit.invokeVirtual(module.typedCall(signature, call.type))
    afterCall(call, grows = true)
    emit.jump(Opcodes.GOTO, done)
    emit.place(runtime, OBJECT_TYPE.removePrefix("L").removeSuffix(";"))
    emit.insn(Opcodes.POP)
    slow()
    emit.place(done)
}

private fun LowerContext.directCall(
    callee: FunctionIndex,
    call: CallSite,
) {
    val type = call.type
    emit.load(Opcodes.ALOAD, Locals.BINDING)
    pushContext()
    emit.load(Opcodes.ILOAD, Locals.DEPTH)
    if (takesArgumentArray(type)) pushArgumentArray(call.arguments) else pushArguments(type, call.arguments)
    emit.invokeStatic(module.functionMethod(callee))
    afterCall(call, grows = callee.index in module.analysis.memoryGrowers)
}

private fun LowerContext.afterCall(
    call: CallSite,
    grows: Boolean,
) {
    val type = call.type
    val analysis = module.analysis
    if (!analysis.hasTailCalls && !analysis.usesExceptions) {
        storeResults(type, call.resultBase)
        if (grows) refreshMemory()
        return
    }
    val results = type.results.types.size
    when (val returns = returnDescriptor(type)) {
        "V" -> Unit
        "[J" -> emit.store(Opcodes.ASTORE, locals.scratchValues)
        else -> {
            encode(returns)
            emit.store(Opcodes.LSTORE, locals.scratchLong[0])
        }
    }
    if (analysis.hasTailCalls) {
        val done = emit.label()
        pushContext()
        emit.getField(CONTEXT_TYPE, PENDING_TAIL_FIELD, FUNCTION_INSTANCE)
        emit.jump(Opcodes.IFNULL, done)
        emit.int(maxOf(results, 1))
        emit.newLongArray()
        emit.store(Opcodes.ASTORE, locals.scratchValues)
        pushContext()
        emit.load(Opcodes.ALOAD, locals.scratchValues)
        emit.load(Opcodes.ILOAD, Locals.DEPTH)
        emit.invokeStatic(COMPLETE_TAIL_CALL)
        if (results == 1) {
            emit.load(Opcodes.ALOAD, locals.scratchValues)
            emit.int(0)
            emit.insn(Opcodes.LALOAD)
            emit.store(Opcodes.LSTORE, locals.scratchLong[0])
        }
        emit.place(done)
    }
    unwindCheck()
    when (results) {
        0 -> Unit
        1 -> {
            emit.load(Opcodes.LLOAD, locals.scratchLong[0])
            storeLong(call.resultBase)
        }
        else -> storeEncodedResults(type, call.resultBase)
    }
    if (grows) refreshMemory()
}

private inline fun LowerContext.runtimeCall(
    call: CallSite,
    target: () -> MethodRef,
) {
    packArguments(call.type, call.arguments)
    pushContext()
    pushResource(Resource.Instance)
    val method = target()
    emit.load(Opcodes.ALOAD, locals.scratchValues)
    emit.load(Opcodes.ILOAD, Locals.DEPTH)
    emit.invokeStatic(method)
    unwindCheck()
    storeEncodedResults(call.type, call.resultBase)
    refreshMemory()
}

internal fun LowerContext.pushArguments(
    type: FunctionType,
    arguments: List<Operand>,
) {
    type.params.types.forEachIndexed { index, parameter ->
        val argument = arguments[index]
        when (valueDescriptor(parameter)) {
            "I" -> pushInt(argument)
            "F" -> pushFloat(argument)
            "D" -> pushDouble(argument)
            else -> pushLong(argument)
        }
    }
}

private fun LowerContext.pushArgumentArray(
    arguments: List<Operand>,
) {
    emit.int(arguments.size)
    emit.newLongArray()
    arguments.forEachIndexed { index, argument ->
        emit.insn(Opcodes.DUP)
        emit.int(index)
        pushLong(argument)
        emit.insn(Opcodes.LASTORE)
    }
}

internal fun LowerContext.storeResults(
    type: FunctionType,
    base: Int,
) {
    when (returnDescriptor(type)) {
        "V" -> Unit
        "I" -> storeInt(base)
        "F" -> storeFloat(base)
        "D" -> storeDouble(base)
        "J" -> storeLong(base)
        else -> {
            emit.store(Opcodes.ASTORE, locals.scratchValues)
            storeEncodedResults(type, base)
        }
    }
}

internal fun LowerContext.storeEncodedResults(
    type: FunctionType,
    base: Int,
) {
    for (index in type.results.types.indices) {
        emit.load(Opcodes.ALOAD, locals.scratchValues)
        emit.int(index)
        emit.insn(Opcodes.LALOAD)
        storeLong(base + index)
    }
}

internal fun LowerContext.packArguments(
    type: FunctionType,
    arguments: List<Operand>,
) {
    emit.int(maxOf(type.params.types.size, type.results.types.size, 1))
    emit.newLongArray()
    arguments.forEachIndexed { index, argument ->
        emit.insn(Opcodes.DUP)
        emit.int(index)
        pushLong(argument)
        emit.insn(Opcodes.LASTORE)
    }
    emit.store(Opcodes.ASTORE, locals.scratchValues)
}

internal fun LowerContext.encode(
    descriptor: String,
) {
    when (descriptor) {
        "I" -> emit.insn(Opcodes.I2L)
        "F" -> {
            emit.invokeStatic(FLOAT_TO_RAW_INT_BITS)
            emit.insn(Opcodes.I2L)
        }
        "D" -> emit.invokeStatic(DOUBLE_TO_RAW_LONG_BITS)
    }
}
