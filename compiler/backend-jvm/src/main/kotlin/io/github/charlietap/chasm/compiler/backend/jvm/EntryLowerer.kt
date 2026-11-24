package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.compiler.backend.abi.CALL_STACK_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.ENTRY_CALL_OBJECTS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.GUEST_HEAP_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_ENTRY
import io.github.charlietap.chasm.compiler.backend.abi.OBJECT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.RESULT_REFERENCES
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.takesArgumentArray
import io.github.charlietap.chasm.compiler.backend.abi.takesReferenceArray
import io.github.charlietap.chasm.compiler.backend.abi.valueDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CLASS_FILE_VERSION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.STACK_OVERFLOW
import io.github.charlietap.chasm.type.FunctionType
import io.github.charlietap.chasm.type.ValueType
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

internal typealias SignatureLowerer = (String, MethodRef) -> ByteArray

internal fun SignatureLowerer(
    signature: String,
    typed: MethodRef,
): ByteArray {

    val writer = supportClassWriter()
    writer.visit(CLASS_FILE_VERSION, Opcodes.ACC_ABSTRACT or Opcodes.ACC_SUPER, internalName(signature), null, internalName(OBJECT_ENTRY), null)

    writer.visitMethod(0, "<init>", "()V", null, null).apply {
        visitCode()
        visitVarInsn(Opcodes.ALOAD, 0)
        visitMethodInsn(Opcodes.INVOKESPECIAL, internalName(OBJECT_ENTRY), "<init>", "()V", false)
        visitInsn(Opcodes.RETURN)
        visitMaxs(0, 0)
        visitEnd()
    }
    writer.visitMethod(Opcodes.ACC_PUBLIC or Opcodes.ACC_ABSTRACT, typed.name, typed.descriptor, null, null).visitEnd()
    writer.visitEnd()

    return writer.toByteArray()
}

internal typealias EntryLowerer = (ModuleLowering, String, List<MethodRef>, FunctionType, String) -> ByteArray

internal fun EntryLowerer(
    module: ModuleLowering,
    entry: String,
    methods: List<MethodRef>,
    type: FunctionType,
    signature: String,
): ByteArray {

    val writer = supportClassWriter()
    val owner = internalName(entry)
    val grouped = methods.size > 1
    val call = EntryCall(owner, module.bindingType, methods, type)
    writer.visit(CLASS_FILE_VERSION, Opcodes.ACC_FINAL or Opcodes.ACC_SUPER, owner, null, internalName(signature), null)
    writer.visitField(Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL, BINDING_FIELD, module.bindingType, null, null).visitEnd()
    if (grouped) {
        writer.visitField(Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL, ID_FIELD, "I", null, null).visitEnd()
    }

    writer.visitMethod(0, "<init>", entryConstructor(module.bindingType, grouped), null, null).apply {
        visitCode()
        visitVarInsn(Opcodes.ALOAD, 0)
        visitMethodInsn(Opcodes.INVOKESPECIAL, internalName(signature), "<init>", "()V", false)
        visitVarInsn(Opcodes.ALOAD, 0)
        visitVarInsn(Opcodes.ALOAD, 1)
        visitFieldInsn(Opcodes.PUTFIELD, owner, BINDING_FIELD, module.bindingType)
        if (grouped) {
            visitVarInsn(Opcodes.ALOAD, 0)
            visitVarInsn(Opcodes.ILOAD, 2)
            visitFieldInsn(Opcodes.PUTFIELD, owner, ID_FIELD, "I")
        }
        visitInsn(Opcodes.RETURN)
        visitMaxs(0, 0)
        visitEnd()
    }

    val typed = module.typedCall(signature, type)
    writer.method(typed) {
        visitVarInsn(Opcodes.ALOAD, 0)
        visitFieldInsn(Opcodes.GETFIELD, owner, BINDING_FIELD, module.bindingType)
        var local = 1
        typed.parameters.forEach { descriptor ->
            visitVarInsn(loadOpcode(descriptor), local)
            local += descriptorWords(descriptor)
        }
        call.invokeFunction(this)
        visitInsn(returnOpcode(typed.returns))
    }

    writer.method(ENTRY_CALL_OBJECTS) {
        guarded {
            call.emitCallObjects(this)
        }
    }
    writer.visitEnd()

    return writer.toByteArray()
}

private const val BINDING_FIELD = "binding"

private const val ID_FIELD = "id"

internal fun entryConstructor(
    binding: String,
    grouped: Boolean,
): String = if (grouped) "(${binding}I)V" else "($binding)V"

private const val CONTEXT_LOCAL = 1

private const val VALUES_LOCAL = 2

private const val REFERENCES_LOCAL = 3

private const val DEPTH_LOCAL = 4

private const val RESULT_LOCAL = 5

private const val RESULT_REFERENCES_LOCAL = 6

private const val OUT_OF_MEMORY = "Ljava/lang/OutOfMemoryError;"

private class EntryCall(
    private val owner: String,
    private val binding: String,
    private val methods: List<MethodRef>,
    private val type: FunctionType,
) {

    fun invokeFunction(visitor: MethodVisitor) {

        val first = methods.first()
        if (methods.size == 1) {
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, internalName(first.owner), first.name, first.descriptor, false)
            return
        }

        val done = Label()
        val cases = Array(methods.size) { Label() }
        visitor.visitVarInsn(Opcodes.ALOAD, 0)
        visitor.visitFieldInsn(Opcodes.GETFIELD, owner, ID_FIELD, "I")
        visitor.visitTableSwitchInsn(0, methods.size - 1, cases.last(), *cases)
        methods.forEachIndexed { index, method ->
            visitor.visitLabel(cases[index])
            visitor.visitMethodInsn(Opcodes.INVOKESTATIC, internalName(method.owner), method.name, method.descriptor, false)
            if (index < methods.lastIndex) {
                visitor.visitJumpInsn(Opcodes.GOTO, done)
            }
        }
        visitor.visitLabel(done)
    }

    fun emitCallObjects(visitor: MethodVisitor) {

        val result = returnDescriptor(type, OBJECT_TYPE)
        when (result) {
            OBJECT_TYPE -> resultSlot(visitor, REFERENCES_LOCAL)
            "I", "F", "D", "J" -> resultSlot(visitor, VALUES_LOCAL)
        }

        visitor.visitVarInsn(Opcodes.ALOAD, 0)
        visitor.visitFieldInsn(Opcodes.GETFIELD, owner, BINDING_FIELD, binding)
        visitor.visitVarInsn(Opcodes.ALOAD, CONTEXT_LOCAL)
        visitor.visitVarInsn(Opcodes.ILOAD, DEPTH_LOCAL)
        pushArguments(visitor)
        invokeFunction(visitor)

        when (result) {
            "V" -> Unit
            OBJECT_TYPE -> visitor.visitInsn(Opcodes.AASTORE)
            "[J" -> storeResults(visitor)
            else -> {
                visitor.encode(result)
                visitor.visitInsn(Opcodes.LASTORE)
            }
        }
        visitor.visitInsn(Opcodes.RETURN)
    }

    private fun resultSlot(
        visitor: MethodVisitor,
        array: Int,
    ) {
        visitor.visitVarInsn(Opcodes.ALOAD, array)
        visitor.visitInsn(Opcodes.ICONST_0)
    }

    private fun pushArguments(visitor: MethodVisitor) {

        if (takesArgumentArray(type)) {
            visitor.visitVarInsn(Opcodes.ALOAD, VALUES_LOCAL)
            if (takesReferenceArray(type, OBJECT_TYPE)) {
                visitor.visitVarInsn(Opcodes.ALOAD, REFERENCES_LOCAL)
            }
            return
        }

        type.params.types.forEachIndexed { index, parameter ->
            val descriptor = valueDescriptor(parameter, OBJECT_TYPE)
            if (descriptor == OBJECT_TYPE) {
                visitor.visitVarInsn(Opcodes.ALOAD, REFERENCES_LOCAL)
                visitor.pushInt(index)
                visitor.visitInsn(Opcodes.AALOAD)
            } else {
                visitor.visitVarInsn(Opcodes.ALOAD, VALUES_LOCAL)
                visitor.pushInt(index)
                visitor.visitInsn(Opcodes.LALOAD)
                visitor.decode(descriptor)
            }
        }
    }

    private fun storeResults(visitor: MethodVisitor) {

        val results = type.results.types
        val noResults = Label()
        visitor.visitVarInsn(Opcodes.ASTORE, RESULT_LOCAL)
        visitor.visitVarInsn(Opcodes.ALOAD, RESULT_LOCAL)
        visitor.visitJumpInsn(Opcodes.IFNULL, noResults)

        if (results.any { result -> result is ValueType.Reference }) {
            visitor.visitVarInsn(Opcodes.ALOAD, CONTEXT_LOCAL)
            visitor.pushInt(results.size)
            visitor.invoke(RESULT_REFERENCES)
            visitor.visitVarInsn(Opcodes.ASTORE, RESULT_REFERENCES_LOCAL)
        }

        results.forEachIndexed { index, result ->
            val copy = if (result is ValueType.Reference) REFERENCE_RESULT else NUMBER_RESULT
            visitor.visitVarInsn(Opcodes.ALOAD, copy.targetLocal)
            visitor.pushInt(index)
            visitor.visitVarInsn(Opcodes.ALOAD, copy.sourceLocal)
            visitor.pushInt(index)
            visitor.visitInsn(copy.loadOpcode)
            visitor.visitInsn(copy.storeOpcode)
        }
        visitor.visitLabel(noResults)
    }
}

private data class ResultCopy(
    val targetLocal: Int,
    val sourceLocal: Int,
    val loadOpcode: Int,
    val storeOpcode: Int,
)

private val REFERENCE_RESULT = ResultCopy(REFERENCES_LOCAL, RESULT_REFERENCES_LOCAL, Opcodes.AALOAD, Opcodes.AASTORE)

private val NUMBER_RESULT = ResultCopy(VALUES_LOCAL, RESULT_LOCAL, Opcodes.LALOAD, Opcodes.LASTORE)

private fun MethodVisitor.decode(
    descriptor: String,
) {
    when (descriptor) {
        "I" -> visitInsn(Opcodes.L2I)
        "F" -> {
            visitInsn(Opcodes.L2I)
            invoke(INT_BITS_TO_FLOAT)
        }
        "D" -> invoke(LONG_BITS_TO_DOUBLE)
    }
}

private fun MethodVisitor.encode(
    descriptor: String,
) {
    when (descriptor) {
        "I" -> visitInsn(Opcodes.I2L)
        "F" -> {
            invoke(FLOAT_TO_RAW_INT_BITS)
            visitInsn(Opcodes.I2L)
        }
        "D" -> invoke(DOUBLE_TO_RAW_LONG_BITS)
    }
}

private inline fun MethodVisitor.guarded(
    body: MethodVisitor.() -> Unit,
) {

    val start = Label()
    val end = Label()
    val overflow = Label()
    val exhausted = Label()
    visitTryCatchBlock(start, end, overflow, internalName(STACK_OVERFLOW))
    visitTryCatchBlock(start, end, exhausted, internalName(OUT_OF_MEMORY))

    visitLabel(start)
    body()
    visitLabel(end)

    visitLabel(overflow)
    rethrowAsTrap(CALL_STACK_EXHAUSTED)
    visitLabel(exhausted)
    rethrowAsTrap(GUEST_HEAP_EXHAUSTED)
}

private fun MethodVisitor.rethrowAsTrap(trap: MethodRef) {
    visitInsn(Opcodes.POP)
    invoke(trap)
    visitInsn(Opcodes.POP)
    visitInsn(Opcodes.ACONST_NULL)
    visitInsn(Opcodes.ATHROW)
}

private fun MethodVisitor.invoke(
    method: MethodRef,
) = visitMethodInsn(Opcodes.INVOKESTATIC, internalName(method.owner), method.name, method.descriptor, false)

internal fun MethodVisitor.pushInt(
    value: Int,
) = when (value) {
    in -1..5 -> visitInsn(Opcodes.ICONST_0 + value)
    in Byte.MIN_VALUE..Byte.MAX_VALUE -> visitIntInsn(Opcodes.BIPUSH, value)
    in Short.MIN_VALUE..Short.MAX_VALUE -> visitIntInsn(Opcodes.SIPUSH, value)
    else -> visitLdcInsn(value)
}

private fun loadOpcode(
    descriptor: String,
) = when (descriptor) {
    "I" -> Opcodes.ILOAD
    "F" -> Opcodes.FLOAD
    "D" -> Opcodes.DLOAD
    "J" -> Opcodes.LLOAD
    else -> Opcodes.ALOAD
}

private fun returnOpcode(
    descriptor: String,
) = when (descriptor) {
    "V" -> Opcodes.RETURN
    "I" -> Opcodes.IRETURN
    "F" -> Opcodes.FRETURN
    "D" -> Opcodes.DRETURN
    "J" -> Opcodes.LRETURN
    else -> Opcodes.ARETURN
}

internal inline fun ClassWriter.method(
    method: MethodRef,
    access: Int = Opcodes.ACC_PUBLIC,
    body: MethodVisitor.() -> Unit,
) {
    visitMethod(access, method.name, method.descriptor, null, null).apply {
        visitCode()
        body()
        visitMaxs(0, 0)
        visitEnd()
    }
}

internal fun supportClassWriter(): ClassWriter = object : ClassWriter(COMPUTE_FRAMES) {
    override fun getCommonSuperClass(
        type1: String,
        type2: String,
    ): String = ModuleLowering.OBJECT
}
