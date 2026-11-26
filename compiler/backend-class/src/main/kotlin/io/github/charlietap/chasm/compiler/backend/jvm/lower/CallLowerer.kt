package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.FunctionIndex
import io.github.charlietap.chasm.compiler.backend.abi.CALL_INDIRECT_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.CALL_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.CALL_REFERENCE_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.COMPLETE_TAIL_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FUNCTION_INSTANCE
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_INDIRECT_ENTRY
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.PENDING_TAIL_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.REFERENCE_TARGET
import io.github.charlietap.chasm.compiler.backend.abi.RESULT_REFERENCES
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.takesArgumentArray
import io.github.charlietap.chasm.compiler.backend.abi.takesReferenceArray
import io.github.charlietap.chasm.compiler.backend.abi.valueDescriptor
import io.github.charlietap.chasm.compiler.backend.analysis.form.CallSite
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.callSite
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import org.objectweb.asm.Opcodes

internal fun LowerContext.lowerCall(
    instruction: ControlInstruction,
) {
    val call = checkNotNull(instruction.callSite(declarations))
    when (instruction) {
        is ControlInstruction.Call -> if (declarations.isImported(instruction.function)) {
            runtimeCall(call) {
                pushResource(Resource.Function(instruction.function.index))
                CALL_OBJECTS
            }
        } else {
            directCall(instruction.function, call)
        }
        is ControlInstruction.CallIndirectI -> indirectCall(
            call,
            target = {
                pushContext()
                pushResource(Resource.ReferenceTable(instruction.table.index))
                pushResource(Resource.RuntimeType(instruction.type.index))
                emit.int(instruction.elementIndex)
                emit.invokeStatic(MANAGED_INDIRECT_ENTRY)
            },
        ) {
            runtimeCall(call) {
                pushResource(Resource.ReferenceTable(instruction.table.index))
                pushResource(Resource.RuntimeType(instruction.type.index))
                emit.int(instruction.elementIndex)
                CALL_INDIRECT_OBJECTS
            }
        }
        is ControlInstruction.CallIndirectS -> indirectCall(
            call,
            target = {
                pushContext()
                pushResource(Resource.ReferenceTable(instruction.table.index))
                pushResource(Resource.RuntimeType(instruction.type.index))
                pushInt(Operand.Slot(instruction.elementIndexSlot))
                emit.invokeStatic(MANAGED_INDIRECT_ENTRY)
            },
        ) {
            runtimeCall(call) {
                pushResource(Resource.ReferenceTable(instruction.table.index))
                pushResource(Resource.RuntimeType(instruction.type.index))
                pushInt(Operand.Slot(instruction.elementIndexSlot))
                CALL_INDIRECT_OBJECTS
            }
        }
        is ControlInstruction.CallRefS -> indirectCall(
            call,
            target = {
                pushObject(Operand.Slot(instruction.functionSlot))
                emit.invokeStatic(REFERENCE_TARGET)
            },
        ) {
            runtimeCall(call) {
                pushObject(Operand.Slot(instruction.functionSlot))
                CALL_REFERENCE_OBJECTS
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
    pushCallArguments(call.type, call.arguments())
    emit.invokeVirtual(module.typedCall(signature, call.type))
    afterCall(
        call,
        grows = true,
        checksUnwinding = module.analysis.usesExceptions,
        checksPendingTailCall = module.analysis.hasTailCalls,
    )
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
    emit.load(Opcodes.ALOAD, Locals.BINDING)
    pushContext()
    emit.load(Opcodes.ILOAD, Locals.DEPTH)
    pushCallArguments(call.type, call.arguments())
    emit.invokeStatic(module.functionMethod(callee))
    val analysis = module.analysis
    afterCall(
        call,
        grows = analysis.mayGrowMemory[callee.index],
        checksUnwinding = analysis.usesExceptions && analysis.mayUnwind[callee.index],
        checksPendingTailCall = analysis.hasTailCalls && analysis.requestsTailCall[callee.index],
    )
}

private fun LowerContext.pushCallArguments(
    type: FunctionType,
    arguments: List<Operand>,
) {
    if (takesArgumentArray(type)) {
        pushArgumentArrays(type, arguments)
    } else {
        pushArguments(type, arguments)
    }
}

private fun LowerContext.afterCall(
    call: CallSite,
    grows: Boolean,
    checksUnwinding: Boolean,
    checksPendingTailCall: Boolean,
) {

    val type = call.type
    if (!checksPendingTailCall && !checksUnwinding) {
        storeResults(type, call.resultBase)
        if (grows) refreshMemory()
        return
    }

    val results = type.results.types.size
    val returns = returnDescriptor(type, OBJECT_TYPE)
    when (returns) {
        "V" -> Unit
        "[J" -> {
            emit.store(Opcodes.ASTORE, locals.scratchValues)
            loadResultReferences(type)
        }
        OBJECT_TYPE -> emit.store(Opcodes.ASTORE, locals.scratchObject)
        else -> {
            encode(returns)
            emit.store(Opcodes.LSTORE, locals.scratchLong[0])
        }
    }

    if (checksPendingTailCall) {
        val done = emit.label()
        pushContext()
        emit.getField(CONTEXT_TYPE, PENDING_TAIL_FIELD, FUNCTION_INSTANCE)
        emit.jump(Opcodes.IFNULL, done)
        newLanes(maxOf(results, 1), references = type.results.types.any { result -> result is ValueType.Reference })
        pushContext()
        emit.load(Opcodes.ALOAD, locals.scratchValues)
        emit.load(Opcodes.ALOAD, locals.scratchReferences)
        emit.load(Opcodes.ILOAD, Locals.DEPTH)
        emit.invokeStatic(COMPLETE_TAIL_OBJECTS)
        if (returns == OBJECT_TYPE) {
            emit.load(Opcodes.ALOAD, locals.scratchReferences)
            emit.int(0)
            emit.insn(Opcodes.AALOAD)
            emit.store(Opcodes.ASTORE, locals.scratchObject)
        } else if (results == 1) {
            emit.load(Opcodes.ALOAD, locals.scratchValues)
            emit.int(0)
            emit.insn(Opcodes.LALOAD)
            emit.store(Opcodes.LSTORE, locals.scratchLong[0])
        }
        if (!checksUnwinding) {
            unwindCheck()
        }
        emit.place(done)
    }

    if (checksUnwinding) {
        unwindCheck()
    }

    when {
        results == 0 -> Unit
        returns == OBJECT_TYPE -> {
            emit.load(Opcodes.ALOAD, locals.scratchObject)
            storeObject(call.resultBase)
        }
        results == 1 -> {
            emit.load(Opcodes.LLOAD, locals.scratchLong[0])
            storeLong(call.resultBase)
        }
        else -> storeLaneResults(type, call.resultBase)
    }

    if (grows) refreshMemory()
}

private inline fun LowerContext.runtimeCall(
    call: CallSite,
    target: () -> MethodRef,
) {
    packArguments(call.type, call.arguments())
    pushContext()
    pushResource(Resource.Instance)
    val method = target()
    emit.load(Opcodes.ALOAD, locals.scratchValues)
    emit.load(Opcodes.ALOAD, locals.scratchReferences)
    emit.load(Opcodes.ILOAD, Locals.DEPTH)
    emit.invokeStatic(method)
    unwindCheck()
    storeLaneResults(call.type, call.resultBase)
    refreshMemory()
}

internal fun LowerContext.pushArguments(
    type: FunctionType,
    arguments: List<Operand>,
) {
    type.params.types.forEachIndexed { index, parameter ->
        val argument = arguments[index]
        when (valueDescriptor(parameter, OBJECT_TYPE)) {
            "I" -> pushInt(argument)
            "F" -> pushFloat(argument)
            "D" -> pushDouble(argument)
            OBJECT_TYPE -> pushObject(argument)
            else -> pushLong(argument)
        }
    }
}

private fun LowerContext.pushArgumentArrays(
    type: FunctionType,
    arguments: List<Operand>,
) {

    val parameters = type.params.types
    val referenceArray = takesReferenceArray(type, OBJECT_TYPE)

    emit.int(arguments.size)
    emit.newLongArray()
    arguments.forEachIndexed { index, argument ->
        if (referenceArray && parameters[index] is ValueType.Reference) {
            return@forEachIndexed
        }
        emit.insn(Opcodes.DUP)
        emit.int(index)
        pushLong(argument)
        emit.insn(Opcodes.LASTORE)
    }

    if (!referenceArray) {
        return
    }

    emit.int(arguments.size)
    emit.newObjectArray()
    arguments.forEachIndexed { index, argument ->
        if (parameters[index] !is ValueType.Reference) {
            return@forEachIndexed
        }
        emit.insn(Opcodes.DUP)
        emit.int(index)
        pushObject(argument)
        emit.insn(Opcodes.AASTORE)
    }
}

internal fun LowerContext.storeResults(
    type: FunctionType,
    base: Int,
) {
    when (returnDescriptor(type, OBJECT_TYPE)) {
        "V" -> Unit
        "I" -> storeInt(base)
        "F" -> storeFloat(base)
        "D" -> storeDouble(base)
        "J" -> storeLong(base)
        OBJECT_TYPE -> storeObject(base)
        else -> {
            emit.store(Opcodes.ASTORE, locals.scratchValues)
            loadResultReferences(type)
            storeLaneResults(type, base)
        }
    }
}

private fun LowerContext.loadResultReferences(
    type: FunctionType,
) {
    val results = type.results.types
    if (results.none { result -> result is ValueType.Reference }) {
        return
    }
    pushContext()
    emit.int(results.size)
    emit.invokeStatic(RESULT_REFERENCES)
    emit.store(Opcodes.ASTORE, locals.scratchReferences)
}

internal fun LowerContext.storeLaneResults(
    type: FunctionType,
    base: Int,
) {
    type.results.types.forEachIndexed { index, result ->
        if (result is ValueType.Reference) {
            emit.load(Opcodes.ALOAD, locals.scratchReferences)
            emit.int(index)
            emit.insn(Opcodes.AALOAD)
            storeObject(base + index)
        } else {
            emit.load(Opcodes.ALOAD, locals.scratchValues)
            emit.int(index)
            emit.insn(Opcodes.LALOAD)
            storeLong(base + index)
        }
    }
}

internal fun LowerContext.packArguments(
    type: FunctionType,
    arguments: List<Operand>,
) {

    val parameters = type.params.types
    newLanes(maxOf(parameters.size, type.results.types.size, 1), references = (parameters + type.results.types).any { value -> value is ValueType.Reference })

    arguments.forEachIndexed { index, argument ->
        if (parameters[index] is ValueType.Reference) {
            emit.load(Opcodes.ALOAD, locals.scratchReferences)
            emit.int(index)
            pushObject(argument)
            emit.insn(Opcodes.AASTORE)
        } else {
            emit.load(Opcodes.ALOAD, locals.scratchValues)
            emit.int(index)
            pushLong(argument)
            emit.insn(Opcodes.LASTORE)
        }
    }
}

internal fun LowerContext.newLanes(
    size: Int,
    references: Boolean,
) {

    emit.int(size)
    emit.newLongArray()
    emit.store(Opcodes.ASTORE, locals.scratchValues)

    if (references) {
        emit.int(size)
        emit.newObjectArray()
    } else {
        emit.insn(Opcodes.ACONST_NULL)
    }
    emit.store(Opcodes.ASTORE, locals.scratchReferences)
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
