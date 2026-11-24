package io.github.charlietap.chasm.compiler.backend.jvm.gc

import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.SPILLED_INTS_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.SPILLED_REFERENCES_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.analysis.gc.FieldStorage
import io.github.charlietap.chasm.compiler.backend.analysis.gc.Lane
import io.github.charlietap.chasm.compiler.backend.analysis.gc.LaneKind
import io.github.charlietap.chasm.compiler.backend.analysis.gc.STORAGE_FAMILY
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructStorage
import io.github.charlietap.chasm.compiler.backend.jvm.pushInt
import io.github.charlietap.chasm.type.FieldType
import io.github.charlietap.chasm.type.PackedType
import io.github.charlietap.chasm.type.StorageType
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

internal inline fun MethodVisitor.allocateStruct(
    storage: StructStorage,
    pushRtt: MethodVisitor.() -> Unit,
) {

    val owner = internalName(storage.className)
    val lanes = storage.fields.flatMap { field -> field.lanes }
    val spilledInts = lanes.count { lane -> lane.spilled && lane.kind == LaneKind.INT }
    val spilledReferences = lanes.count { lane -> lane.spilled && lane.kind == LaneKind.REFERENCE }

    visitTypeInsn(Opcodes.NEW, owner)
    visitInsn(Opcodes.DUP)
    pushRtt()
    visitMethodInsn(Opcodes.INVOKESPECIAL, owner, "<init>", "($RTT_TYPE)V", false)

    if (spilledInts > 0) {
        visitInsn(Opcodes.DUP)
        pushInt(spilledInts)
        visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT)
        visitFieldInsn(Opcodes.PUTFIELD, owner, SPILLED_INTS_FIELD, SPILLED_INTS_TYPE)
    }
    if (spilledReferences > 0) {
        visitInsn(Opcodes.DUP)
        pushInt(spilledReferences)
        visitTypeInsn(Opcodes.ANEWARRAY, OBJECT_CLASS)
        visitFieldInsn(Opcodes.PUTFIELD, owner, SPILLED_REFERENCES_FIELD, SPILLED_REFERENCES_TYPE)
    }
}

internal inline fun MethodVisitor.putField(
    storage: StructStorage,
    field: FieldStorage,
    wideLocal: Int,
    pushValue: MethodVisitor.() -> Unit,
) {

    val lanes = field.lanes
    if (lanes.size == 2) {
        pushValue()
        visitVarInsn(Opcodes.LSTORE, wideLocal)
        visitInsn(Opcodes.DUP)
        putLane(storage, lanes[0]) {
            visitVarInsn(Opcodes.LLOAD, wideLocal)
            visitInsn(Opcodes.L2I)
        }
        putLane(storage, lanes[1]) {
            visitVarInsn(Opcodes.LLOAD, wideLocal)
            pushInt(HALF_BITS)
            visitInsn(Opcodes.LUSHR)
            visitInsn(Opcodes.L2I)
        }
        return
    }

    val lane = lanes.single()
    putLane(storage, lane) {
        pushValue()
        if (lane.kind == LaneKind.INT) {
            maskPacked(field.type)
        }
    }
}

internal inline fun MethodVisitor.putLane(
    storage: StructStorage,
    lane: Lane,
    pushValue: MethodVisitor.() -> Unit,
) {

    val owner = laneOwner(storage, lane)
    when {
        lane.spilled && lane.kind == LaneKind.INT -> {
            visitFieldInsn(Opcodes.GETFIELD, owner, SPILLED_INTS_FIELD, SPILLED_INTS_TYPE)
            pushInt(lane.index)
            pushValue()
            visitInsn(Opcodes.IASTORE)
        }
        lane.spilled -> {
            visitFieldInsn(Opcodes.GETFIELD, owner, SPILLED_REFERENCES_FIELD, SPILLED_REFERENCES_TYPE)
            pushInt(lane.index)
            pushValue()
            visitInsn(Opcodes.AASTORE)
        }
        lane.kind == LaneKind.INT -> {
            pushValue()
            visitFieldInsn(Opcodes.PUTFIELD, owner, intLane(lane), "I")
        }
        else -> {
            pushValue()
            visitFieldInsn(Opcodes.PUTFIELD, owner, referenceLane(lane), OBJECT_TYPE)
        }
    }
}

internal fun MethodVisitor.getLane(
    storage: StructStorage,
    lane: Lane,
) {

    val owner = laneOwner(storage, lane)
    when {
        lane.spilled && lane.kind == LaneKind.INT -> {
            visitFieldInsn(Opcodes.GETFIELD, owner, SPILLED_INTS_FIELD, SPILLED_INTS_TYPE)
            pushInt(lane.index)
            visitInsn(Opcodes.IALOAD)
        }
        lane.spilled -> {
            visitFieldInsn(Opcodes.GETFIELD, owner, SPILLED_REFERENCES_FIELD, SPILLED_REFERENCES_TYPE)
            pushInt(lane.index)
            visitInsn(Opcodes.AALOAD)
        }
        lane.kind == LaneKind.INT -> visitFieldInsn(Opcodes.GETFIELD, owner, intLane(lane), "I")
        else -> visitFieldInsn(Opcodes.GETFIELD, owner, referenceLane(lane), OBJECT_TYPE)
    }
}

internal fun MethodVisitor.maskPacked(type: FieldType) {
    when ((type.storageType as? StorageType.Packed)?.type) {
        PackedType.I8 -> {
            pushInt(BYTE_MASK)
            visitInsn(Opcodes.IAND)
        }
        PackedType.I16 -> {
            pushInt(CHAR_MASK)
            visitInsn(Opcodes.IAND)
        }
        null -> Unit
    }
}

internal fun laneOwner(
    storage: StructStorage,
    lane: Lane,
): String {
    return internalName(STORAGE_FAMILY.storageClass(storage.className).declaringClass(lane).name)
}

internal fun intLane(lane: Lane): String {
    return "i${lane.index}"
}

internal fun referenceLane(lane: Lane): String {
    return "r${lane.index}"
}

internal const val SPILLED_INTS_TYPE = "[I"

internal const val SPILLED_REFERENCES_TYPE = "[Ljava/lang/Object;"

internal const val OBJECT_CLASS = "java/lang/Object"

internal const val HALF_BITS = 32

internal const val BYTE_MASK = 0xFF

internal const val CHAR_MASK = 0xFFFF
