package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.compiler.backend.abi.CALL_DEPTH_FIELD
import io.github.charlietap.chasm.compiler.backend.abi.CALL_STACK_EXHAUSTED
import io.github.charlietap.chasm.compiler.backend.abi.CONTEXT_TYPE
import io.github.charlietap.chasm.compiler.backend.abi.DOUBLE_TO_RAW_LONG_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FLOAT_TO_RAW_INT_BITS
import io.github.charlietap.chasm.compiler.backend.abi.FRAME
import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.descriptorWords
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import io.github.charlietap.chasm.compiler.backend.abi.returnDescriptor
import io.github.charlietap.chasm.compiler.backend.abi.takesArgumentArray
import io.github.charlietap.chasm.compiler.backend.abi.valueDescriptor
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CLASS_FILE_VERSION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.COMPILED_FUNCTION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.ENTRY_CALL
import io.github.charlietap.chasm.compiler.backend.jvm.abi.ENTRY_INVOKE
import io.github.charlietap.chasm.compiler.backend.jvm.abi.STACK_OVERFLOW
import io.github.charlietap.chasm.type.FunctionType
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
    writer.visit(
        CLASS_FILE_VERSION,
        Opcodes.ACC_ABSTRACT or Opcodes.ACC_SUPER,
        internalName(signature),
        null,
        ModuleLowering.OBJECT,
        arrayOf(internalName(COMPILED_FUNCTION)),
    )
    writer.visitMethod(0, "<init>", "()V", null, null).apply {
        visitCode()
        visitVarInsn(Opcodes.ALOAD, 0)
        visitMethodInsn(Opcodes.INVOKESPECIAL, ModuleLowering.OBJECT, "<init>", "()V", false)
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
    if (grouped) writer.visitField(Opcodes.ACC_PRIVATE or Opcodes.ACC_FINAL, ID_FIELD, "I", null, null).visitEnd()

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

    writer.method(ENTRY_INVOKE) {
        guarded {
            call.emit(
                this,
                argument = { index -> element(array = 2, offset = { visitVarInsn(Opcodes.ILOAD, 3) }, index = index) },
                arguments = {
                    visitVarInsn(Opcodes.ALOAD, 2)
                    visitVarInsn(Opcodes.ILOAD, 3)
                },
                depth = {
                    visitVarInsn(Opcodes.ALOAD, 1)
                    visitFieldInsn(Opcodes.GETFIELD, internalName(CONTEXT_TYPE), CALL_DEPTH_FIELD, "I")
                },
                resultArray = {
                    visitVarInsn(Opcodes.ALOAD, 1)
                    invoke(FRAME)
                },
                resultOffset = { visitVarInsn(Opcodes.ILOAD, 3) },
            )
        }
    }

    writer.method(ENTRY_CALL) {
        guarded {
            call.emit(
                this,
                argument = { index -> element(array = 2, offset = null, index = index) },
                arguments = {
                    visitVarInsn(Opcodes.ALOAD, 2)
                    visitInsn(Opcodes.ICONST_0)
                },
                depth = { visitVarInsn(Opcodes.ILOAD, 3) },
                resultArray = { visitVarInsn(Opcodes.ALOAD, 2) },
                resultOffset = { visitInsn(Opcodes.ICONST_0) },
            )
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

private const val FIRST_FREE_LOCAL = 4

private class EntryCall(
    private val owner: String,
    private val binding: String,
    private val methods: List<MethodRef>,
    private val type: FunctionType,
) {
    private val parameters = type.params.types.map(::valueDescriptor)

    fun invokeFunction(
        visitor: MethodVisitor,
    ) = with(visitor) {
        val first = methods.first()
        if (methods.size == 1) return@with visitMethodInsn(Opcodes.INVOKESTATIC, internalName(first.owner), first.name, first.descriptor, false)
        val done = Label()
        val cases = Array(methods.size) { Label() }
        visitVarInsn(Opcodes.ALOAD, 0)
        visitFieldInsn(Opcodes.GETFIELD, owner, ID_FIELD, "I")
        visitTableSwitchInsn(0, methods.size - 1, cases.last(), *cases)
        methods.forEachIndexed { index, method ->
            visitLabel(cases[index])
            visitMethodInsn(Opcodes.INVOKESTATIC, internalName(method.owner), method.name, method.descriptor, false)
            if (index < methods.lastIndex) visitJumpInsn(Opcodes.GOTO, done)
        }
        visitLabel(done)
    }

    private val argumentLocals = IntArray(parameters.size).also { locals ->
        var next = FIRST_FREE_LOCAL
        parameters.forEachIndexed { index, descriptor ->
            locals[index] = next
            next += descriptorWords(descriptor)
        }
    }

    private val resultLocal = FIRST_FREE_LOCAL + if (takesArgumentArray(type)) 1 else parameters.sumOf(::descriptorWords)

    fun emit(
        visitor: MethodVisitor,
        argument: MethodVisitor.(Int) -> Unit,
        arguments: MethodVisitor.() -> Unit,
        depth: MethodVisitor.() -> Unit,
        resultArray: MethodVisitor.() -> Unit,
        resultOffset: MethodVisitor.() -> Unit,
    ) = with(visitor) {
        if (takesArgumentArray(type)) {
            pushInt(parameters.size)
            visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_LONG)
            visitVarInsn(Opcodes.ASTORE, FIRST_FREE_LOCAL)
            arguments()
            visitVarInsn(Opcodes.ALOAD, FIRST_FREE_LOCAL)
            visitInsn(Opcodes.ICONST_0)
            pushInt(parameters.size)
            visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V", false)
        } else {
            parameters.forEachIndexed { index, descriptor ->
                argument(index)
                decode(descriptor)
                visitVarInsn(storeOpcode(descriptor), argumentLocals[index])
            }
        }
        visitVarInsn(Opcodes.ALOAD, 0)
        visitFieldInsn(Opcodes.GETFIELD, owner, BINDING_FIELD, binding)
        visitVarInsn(Opcodes.ALOAD, 1)
        depth()
        if (takesArgumentArray(type)) {
            visitVarInsn(Opcodes.ALOAD, FIRST_FREE_LOCAL)
        } else {
            parameters.forEachIndexed { index, descriptor -> visitVarInsn(loadOpcode(descriptor), argumentLocals[index]) }
        }
        invokeFunction(this)
        when (val result = returnDescriptor(type)) {
            "V" -> Unit
            "[J" -> {
                val none = Label()
                visitVarInsn(Opcodes.ASTORE, resultLocal)
                visitVarInsn(Opcodes.ALOAD, resultLocal)
                visitJumpInsn(Opcodes.IFNULL, none)
                visitVarInsn(Opcodes.ALOAD, resultLocal)
                visitInsn(Opcodes.ICONST_0)
                resultArray()
                resultOffset()
                pushInt(type.results.types.size)
                visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "arraycopy", "(Ljava/lang/Object;ILjava/lang/Object;II)V", false)
                visitLabel(none)
            }
            else -> {
                encode(result)
                visitVarInsn(Opcodes.LSTORE, resultLocal)
                resultArray()
                resultOffset()
                visitVarInsn(Opcodes.LLOAD, resultLocal)
                visitInsn(Opcodes.LASTORE)
            }
        }
        visitInsn(Opcodes.RETURN)
    }
}

private fun MethodVisitor.element(
    array: Int,
    offset: (MethodVisitor.() -> Unit)?,
    index: Int,
) {
    visitVarInsn(Opcodes.ALOAD, array)
    pushInt(index)
    if (offset != null) {
        offset()
        visitInsn(Opcodes.IADD)
    }
    visitInsn(Opcodes.LALOAD)
}

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
    val handler = Label()
    visitTryCatchBlock(start, end, handler, internalName(STACK_OVERFLOW))
    visitLabel(start)
    body()
    visitLabel(end)
    visitLabel(handler)
    visitInsn(Opcodes.POP)
    invoke(CALL_STACK_EXHAUSTED)
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

private fun storeOpcode(
    descriptor: String,
) = when (descriptor) {
    "I" -> Opcodes.ISTORE
    "F" -> Opcodes.FSTORE
    "D" -> Opcodes.DSTORE
    else -> Opcodes.LSTORE
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
