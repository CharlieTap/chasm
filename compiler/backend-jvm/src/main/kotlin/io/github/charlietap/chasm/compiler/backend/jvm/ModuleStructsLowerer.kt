package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.compiler.backend.abi.COMPILED_STRUCT
import io.github.charlietap.chasm.compiler.backend.abi.RTT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.UNREACHABLE
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.analysis.gc.StructStorage
import io.github.charlietap.chasm.compiler.backend.analysis.gc.UnsupportedShape
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CLASS_FILE_VERSION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.MODULE_NEW_STRUCT
import io.github.charlietap.chasm.compiler.backend.jvm.abi.MODULE_STRUCT_LANES
import io.github.charlietap.chasm.compiler.backend.jvm.abi.structsClassDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.gc.allocateStruct
import io.github.charlietap.chasm.type.ext.structType
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

internal typealias ModuleStructsLowerer = (ModuleLowering, ClassWriter, JvmOutput) -> Unit

internal fun ModuleStructsLowerer(
    module: ModuleLowering,
    writer: ClassWriter,
    output: JvmOutput,
) {

    val structs = module.definedTypes.indices
        .filter { index -> module.definedTypes[index].structType() != null }
        .mapNotNull { index -> supportedStruct(module, index)?.let { storage -> index to storage } }
        .toMap()
    val groups = structs.keys.groupBy { index -> index / TYPES_PER_GROUP }

    writer.method(MODULE_STRUCT_LANES) {
        pushLongs(structLanes(structs))
        visitInsn(Opcodes.ARETURN)
    }
    writer.method(MODULE_NEW_STRUCT) {
        newStruct(module, groups.keys.sorted())
    }

    groups.forEach { (group, types) ->
        val owner = internalName(structsClassDescriptor(module.entryPath, group))
        output.classFile(owner, groupClass(owner, types, structs))
    }
}

private fun MethodVisitor.newStruct(
    module: ModuleLowering,
    groups: List<Int>,
) {

    if (groups.isEmpty()) {
        unreachable()
        return
    }

    val labels = groups.map { Label() }
    val missing = Label()
    visitVarInsn(Opcodes.ILOAD, 1)
    pushInt(TYPES_PER_GROUP)
    visitInsn(Opcodes.IDIV)
    visitLookupSwitchInsn(missing, groups.toIntArray(), labels.toTypedArray())

    groups.forEachIndexed { position, group ->
        val owner = internalName(structsClassDescriptor(module.entryPath, group))
        visitLabel(labels[position])
        visitVarInsn(Opcodes.ILOAD, 1)
        visitVarInsn(Opcodes.ALOAD, 2)
        visitTypeInsn(Opcodes.CHECKCAST, internalName(RTT_TYPE))
        visitMethodInsn(Opcodes.INVOKESTATIC, owner, MODULE_NEW_STRUCT.name, STATIC_NEW_STRUCT_DESCRIPTOR, false)
        visitInsn(Opcodes.ARETURN)
    }

    visitLabel(missing)
    unreachable()
}

private fun groupClass(
    owner: String,
    types: List<Int>,
    structs: Map<Int, StructStorage>,
): ByteArray {

    val writer = supportClassWriter()
    writer.visit(CLASS_FILE_VERSION, Opcodes.ACC_FINAL or Opcodes.ACC_SUPER, owner, null, ModuleLowering.OBJECT, null)

    writer.visitMethod(Opcodes.ACC_STATIC, MODULE_NEW_STRUCT.name, STATIC_NEW_STRUCT_DESCRIPTOR, null, null).apply {
        visitCode()

        val first = types.first()
        val last = types.last()
        val missing = Label()
        val labels = Array(last - first + 1) { missing }
        types.forEach { type -> labels[type - first] = Label() }
        visitVarInsn(Opcodes.ILOAD, 0)
        visitTableSwitchInsn(first, last, missing, *labels)

        types.forEach { type ->
            visitLabel(labels[type - first])
            allocateStruct(structs.getValue(type)) {
                visitVarInsn(Opcodes.ALOAD, 1)
            }
            visitInsn(Opcodes.ARETURN)
        }

        visitLabel(missing)
        unreachable()
        visitMaxs(0, 0)
        visitEnd()
    }
    writer.visitEnd()

    return writer.toByteArray()
}

private fun structLanes(structs: Map<Int, StructStorage>): LongArray {

    val values = ArrayList<Long>()
    structs.forEach { (type, storage) ->
        val lanes = storage.fields.flatMap { field -> field.lanes }
        values += type.toLong()
        values += storage.bytes.toLong()
        values += lanes.size.toLong()
        lanes.forEach { lane ->
            values += (lane.index.toLong() shl 1) or if (lane.spilled) 1L else 0L
        }
    }

    return values.toLongArray()
}

private fun supportedStruct(
    module: ModuleLowering,
    index: Int,
): StructStorage? {
    return try {
        module.storagePlans.struct(index)
    } catch (_: UnsupportedShape) {
        null
    }
}

private fun MethodVisitor.unreachable() {
    visitMethodInsn(Opcodes.INVOKESTATIC, internalName(UNREACHABLE.owner), UNREACHABLE.name, UNREACHABLE.descriptor, false)
    visitInsn(Opcodes.POP)
    visitInsn(Opcodes.ACONST_NULL)
    visitInsn(Opcodes.ATHROW)
}

private const val TYPES_PER_GROUP = 128

private const val STATIC_NEW_STRUCT_DESCRIPTOR = "(I$RTT_TYPE)$COMPILED_STRUCT"
