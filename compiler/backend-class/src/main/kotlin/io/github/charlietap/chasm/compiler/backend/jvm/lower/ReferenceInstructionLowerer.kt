package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.cir.ReferenceInstruction
import io.github.charlietap.chasm.cir.ReferenceType
import io.github.charlietap.chasm.compiler.backend.abi.CAST_FAILED
import io.github.charlietap.chasm.compiler.backend.abi.COMPILED_RTT_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.FUNCTION_MATCHES
import io.github.charlietap.chasm.compiler.backend.abi.MANAGED_FUNCTION
import io.github.charlietap.chasm.compiler.backend.abi.NULL_REFERENCE
import io.github.charlietap.chasm.compiler.backend.abi.RTT_ARRAY_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.RTT_DEPTH_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.RTT_SUPERTYPES_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.SAME_I31
import io.github.charlietap.chasm.compiler.backend.analysis.form.Operand
import io.github.charlietap.chasm.compiler.backend.analysis.form.slot
import io.github.charlietap.chasm.compiler.backend.analysis.gc.ReferenceTest
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes

internal fun lowerReferenceInstruction(
    instruction: ReferenceInstruction,
    context: LowerContext,
) {
    when (instruction) {
        is ReferenceInstruction.RefIsNullS -> context.booleanResult(instruction.destinationSlot) { holds ->
            context.branchOnNull(slot(instruction.valueSlot), isNull = true, holds)
        }
        is ReferenceInstruction.RefAsNonNullS -> context.refAsNonNull(instruction.valueSlot, instruction.destinationSlot)
        is ReferenceInstruction.RefNullS -> {
            context.emit.insn(Opcodes.ACONST_NULL)
            context.storeObject(instruction.destinationSlot)
        }
        is ReferenceInstruction.RefFuncS -> {
            context.pushResource(Resource.FunctionObject(instruction.function.index))
            context.storeObject(instruction.destinationSlot)
        }
        is ReferenceInstruction.RefEqSs -> context.booleanResult(instruction.destinationSlot) { holds ->
            context.pushObject(slot(instruction.reference1Slot))
            context.pushObject(slot(instruction.reference2Slot))
            context.emit.jump(Opcodes.IF_ACMPEQ, holds)
            context.pushObject(slot(instruction.reference1Slot))
            context.pushObject(slot(instruction.reference2Slot))
            context.emit.invokeStatic(SAME_I31)
            context.emit.jump(Opcodes.IFNE, holds)
        }
        is ReferenceInstruction.RefCastS -> context.refCast(instruction.referenceSlot, instruction.typeTest, instruction.destinationSlot)
        is ReferenceInstruction.RefTestS -> context.booleanResult(instruction.destinationSlot) { holds ->
            context.branchOnType(slot(instruction.referenceSlot), instruction.typeTest, holds)
        }
    }
}

private fun LowerContext.refAsNonNull(
    valueSlot: Int,
    destination: Int,
) {

    branchOnNull(slot(valueSlot), isNull = true, trapLabel(NULL_REFERENCE))

    if (valueSlot != destination) {
        pushObject(slot(valueSlot))
        storeObject(destination)
    }
}

private fun LowerContext.refCast(
    referenceSlot: Int,
    type: ReferenceType,
    destination: Int,
) {

    val matches = emit.label()
    branchOnType(slot(referenceSlot), type, matches)
    trap(CAST_FAILED)
    emit.place(matches)

    if (referenceSlot != destination) {
        pushObject(slot(referenceSlot))
        storeObject(destination)
    }
}

internal fun LowerContext.branchOnType(
    reference: Operand,
    type: ReferenceType,
    matches: Label,
) {

    val fails = emit.label()
    pushObject(reference)
    emit.jump(Opcodes.IFNULL, if (type.nullable) matches else fails)

    when (val test = module.referenceTests.test(type.heapType)) {
        ReferenceTest.Always -> emit.jump(Opcodes.GOTO, matches)
        ReferenceTest.Never -> Unit
        ReferenceTest.NotFunction -> {
            branchOnInstance(reference, MANAGED_FUNCTION, fails)
            emit.jump(Opcodes.GOTO, matches)
        }
        is ReferenceTest.InstanceOf -> branchOnInstance(reference, test.descriptor, matches)
        is ReferenceTest.Defined -> branchOnRtt(reference, test.typeIndex, matches, fails)
        is ReferenceTest.FunctionType -> {
            pushContext()
            pushObject(reference)
            pushResource(Resource.RuntimeType(test.typeIndex))
            emit.invokeStatic(FUNCTION_MATCHES)
            emit.jump(Opcodes.IFNE, matches)
        }
    }

    emit.place(fails)
}

private fun LowerContext.branchOnInstance(
    reference: Operand,
    descriptor: String,
    target: Label,
) {
    pushObject(reference)
    emit.type(Opcodes.INSTANCEOF, descriptor)
    emit.jump(Opcodes.IFNE, target)
}

private fun LowerContext.branchOnRtt(
    reference: Operand,
    typeIndex: Int,
    matches: Label,
    fails: Label,
) {

    val test = module.rttTests.test(typeIndex)
    pushObject(reference)
    emit.type(Opcodes.INSTANCEOF, test.baseClass)
    emit.jump(Opcodes.IFEQ, fails)

    pushObject(reference)
    emit.type(Opcodes.CHECKCAST, test.baseClass)
    emit.getField(test.baseClass, COMPILED_RTT_FIELD, RTT_TYPE)

    if (test.final) {
        pushResource(Resource.Rtt(typeIndex))
        emit.jump(Opcodes.IF_ACMPEQ, matches)
        return
    }

    if (test.depth > 0) {
        emit.store(Opcodes.ASTORE, locals.scratchRtt)
        emit.load(Opcodes.ALOAD, locals.scratchRtt)
        emit.getField(RTT_TYPE, RTT_DEPTH_FIELD, "I")
        emit.int(test.depth)
        emit.jump(Opcodes.IF_ICMPLT, fails)
        emit.load(Opcodes.ALOAD, locals.scratchRtt)
    }

    emit.getField(RTT_TYPE, RTT_SUPERTYPES_FIELD, RTT_ARRAY_TYPE)
    emit.int(test.depth)
    emit.insn(Opcodes.AALOAD)
    pushResource(Resource.Rtt(typeIndex))
    emit.jump(Opcodes.IF_ACMPEQ, matches)
}
