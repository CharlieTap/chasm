package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.cir.HeapType
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.Resource
import io.github.charlietap.chasm.compiler.backend.abi.UNPACK_LONGS
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.abi.packLongs
import io.github.charlietap.chasm.compiler.backend.jvm.abi.BYTE_ARRAY_BASE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CLASS_FILE_VERSION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CODE_INSTALLATION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.UNSAFE_INSTANCE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.UNSAFE_TYPE
import io.github.charlietap.chasm.compiler.backend.jvm.binding.bindingPartField
import io.github.charlietap.chasm.compiler.backend.jvm.binding.bindingPartType
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

internal typealias BindingLowerer = (ModuleLowering, JvmOutput) -> Unit

internal fun BindingLowerer(
    module: ModuleLowering,
    output: JvmOutput,
) {
    for (part in 0 until module.layout.classes) {
        val type = bindingPartType(module.bindingType, part)
        output.classFile(internalName(type), bindingClass(module, type, part))
    }
}

private fun bindingClass(
    module: ModuleLowering,
    type: String,
    part: Int,
): ByteArray {
    val writer = supportClassWriter()
    val owner = internalName(type)
    writer.visit(CLASS_FILE_VERSION, Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER, owner, null, ModuleLowering.OBJECT, null)
    val resources = module.layout.resourcesOf(part)
    resources.forEach { resource ->
        val field = module.layout.fieldOf(resource)
        writer.visitField(0, field.name, field.descriptor, null, null).visitEnd()
    }
    val parts = if (part == 0) 1 until module.layout.classes else IntRange.EMPTY
    parts.forEach { held -> writer.visitField(0, bindingPartField(held), bindingPartType(module.bindingType, held), null, null).visitEnd() }
    val chunks = resources.chunked(FIELDS_PER_METHOD)
    val chunkDescriptor = "($type$CODE_INSTALLATION)V"
    writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "($CODE_INSTALLATION)V", null, null).apply {
        visitCode()
        visitVarInsn(Opcodes.ALOAD, 0)
        visitMethodInsn(Opcodes.INVOKESPECIAL, ModuleLowering.OBJECT, "<init>", "()V", false)
        parts.forEach { held ->
            val heldType = bindingPartType(module.bindingType, held)
            visitVarInsn(Opcodes.ALOAD, 0)
            visitTypeInsn(Opcodes.NEW, internalName(heldType))
            visitInsn(Opcodes.DUP)
            visitVarInsn(Opcodes.ALOAD, 1)
            visitMethodInsn(Opcodes.INVOKESPECIAL, internalName(heldType), "<init>", "($CODE_INSTALLATION)V", false)
            visitFieldInsn(Opcodes.PUTFIELD, owner, bindingPartField(held), heldType)
        }
        chunks.indices.forEach { chunk ->
            visitVarInsn(Opcodes.ALOAD, 0)
            visitVarInsn(Opcodes.ALOAD, 1)
            visitMethodInsn(Opcodes.INVOKESTATIC, owner, "bind$chunk", chunkDescriptor, false)
        }
        visitInsn(Opcodes.RETURN)
        visitMaxs(0, 0)
        visitEnd()
    }
    chunks.forEachIndexed { chunk, chunkResources ->
        writer.visitMethod(Opcodes.ACC_PRIVATE or Opcodes.ACC_STATIC, "bind$chunk", chunkDescriptor, null, null).apply {
            visitCode()
            chunkResources.forEach { resource ->
                val field = module.layout.fieldOf(resource)
                visitVarInsn(Opcodes.ALOAD, 0)
                loadResource(resource)
                visitFieldInsn(Opcodes.PUTFIELD, owner, field.name, field.descriptor)
            }
            visitInsn(Opcodes.RETURN)
            visitMaxs(0, 0)
            visitEnd()
        }
    }
    writer.visitEnd()
    return writer.toByteArray()
}

private const val FIELDS_PER_METHOD = 200

private fun MethodVisitor.loadResource(
    resource: Resource,
) {
    if (resource is Resource.Longs) return pushLongs(resource.values)
    when (resource) {
        Resource.Unsafe -> {
            visitMethodInsn(Opcodes.INVOKESTATIC, internalName(UNSAFE_INSTANCE.owner), UNSAFE_INSTANCE.name, UNSAFE_INSTANCE.descriptor, false)
            return visitTypeInsn(Opcodes.CHECKCAST, internalName(UNSAFE_TYPE))
        }
        Resource.ByteArrayBase -> return visitMethodInsn(
            Opcodes.INVOKESTATIC,
            internalName(BYTE_ARRAY_BASE.owner),
            BYTE_ARRAY_BASE.name,
            BYTE_ARRAY_BASE.descriptor,
            false,
        )
        else -> Unit
    }
    visitVarInsn(Opcodes.ALOAD, 1)
    val accessor = when (resource) {
        Resource.Instance -> return installation(INSTANCE)
        is Resource.Memory -> indexed(MEMORY, resource.index)
        is Resource.Table -> indexed(TABLE, resource.index)
        is Resource.Global -> indexed(GLOBAL, resource.index)
        is Resource.Function -> indexed(FUNCTION, resource.index)
        is Resource.Element -> indexed(ELEMENT, resource.index)
        is Resource.Data -> indexed(DATA, resource.index)
        is Resource.RuntimeType -> indexed(RUNTIME_TYPE, resource.typeIndex)
        is Resource.TagAddress -> indexed(TAG_ADDRESS, resource.index)
        is Resource.FunctionReference -> indexed(FUNCTION_REFERENCE, resource.index)
        is Resource.TypeTest -> {
            visitInsn(if (resource.type.nullable) Opcodes.ICONST_1 else Opcodes.ICONST_0)
            pushInt(heapTypeNumber(resource.type.heapType))
            TYPE_TEST
        }
        else -> error("$resource has no binding field")
    }
    installation(accessor)
}

private fun MethodVisitor.indexed(
    accessor: MethodRef,
    index: Int,
): MethodRef {
    pushInt(index)
    return accessor
}

private fun MethodVisitor.installation(
    accessor: MethodRef,
) = visitMethodInsn(Opcodes.INVOKEVIRTUAL, internalName(accessor.owner), accessor.name, accessor.descriptor, false)

private fun MethodVisitor.pushLongs(
    values: LongArray,
) {
    if (values.size > INLINE_LONGS) {
        val parts = packLongs(values)
        pushInt(parts.size)
        visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/String")
        parts.forEachIndexed { index, part ->
            visitInsn(Opcodes.DUP)
            pushInt(index)
            visitLdcInsn(part)
            visitInsn(Opcodes.AASTORE)
        }
        return visitMethodInsn(Opcodes.INVOKESTATIC, internalName(UNPACK_LONGS.owner), UNPACK_LONGS.name, UNPACK_LONGS.descriptor, false)
    }
    pushInt(values.size)
    visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_LONG)
    values.forEachIndexed { index, value ->
        if (value == 0L) return@forEachIndexed
        visitInsn(Opcodes.DUP)
        pushInt(index)
        visitLdcInsn(value)
        visitInsn(Opcodes.LASTORE)
    }
}

private const val INLINE_LONGS = 16

private fun heapTypeNumber(
    heapType: HeapType,
): Int = when (heapType) {
    is HeapType.Concrete -> heapType.type.index
    is HeapType.Abstract -> -1 - ABSTRACT_HEAP_TYPES.indexOf(heapType.type.text).also { check(it >= 0) { "unknown heap type $heapType" } }
}

private val ABSTRACT_HEAP_TYPES = listOf("func", "nofunc", "extern", "noextern", "any", "eq", "i31", "struct", "array", "none", "exn", "noexn")

private fun accessor(
    name: String,
    returns: String,
) = MethodRef(CODE_INSTALLATION, name, listOf("I"), returns)

private val INSTANCE = MethodRef(CODE_INSTALLATION, "getInstance", emptyList(), "Lio/github/charlietap/chasm/runtime/instance/ModuleInstance;")
private val MEMORY = accessor("memory", "Lio/github/charlietap/chasm/runtime/instance/MemoryInstance;")
private val TABLE = accessor("table", "Lio/github/charlietap/chasm/runtime/instance/TableInstance;")
private val GLOBAL = accessor("global", "Lio/github/charlietap/chasm/runtime/instance/GlobalInstance;")
private val FUNCTION = accessor("function", "Lio/github/charlietap/chasm/runtime/instance/FunctionInstance;")
private val ELEMENT = accessor("element", "Lio/github/charlietap/chasm/runtime/instance/ElementInstance;")
private val DATA = accessor("data", "Lio/github/charlietap/chasm/runtime/instance/DataInstance;")
private val RUNTIME_TYPE = accessor("runtimeType", "I")
private val TAG_ADDRESS = accessor("tagAddress", "I")
private val FUNCTION_REFERENCE = accessor("functionReference", "J")
private val TYPE_TEST = MethodRef(CODE_INSTALLATION, "typeTest", listOf("Z", "I"), OBJECT_TYPE)
