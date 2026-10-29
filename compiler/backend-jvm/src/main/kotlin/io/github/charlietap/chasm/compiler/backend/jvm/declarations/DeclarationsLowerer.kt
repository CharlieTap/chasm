package io.github.charlietap.chasm.compiler.backend.jvm.declarations

import io.github.charlietap.chasm.compiler.backend.jvm.JvmOutput
import io.github.charlietap.chasm.compiler.backend.jvm.ModuleLowering
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CLASS_FILE_VERSION
import io.github.charlietap.chasm.compiler.backend.jvm.abi.MAX_FANOUT
import io.github.charlietap.chasm.compiler.backend.jvm.pushInt
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

internal const val DECLARATIONS_BUILDER = "io/github/charlietap/chasm/declarations/construction/DeclarationsBuilder"

internal const val DECLARE = "declare"
internal const val DECLARE_DESCRIPTOR = "(L$DECLARATIONS_BUILDER;)V"

internal typealias DeclarationsLowerer = (List<BuilderCall>, String, JvmOutput) -> List<String>

internal fun DeclarationsLowerer(
    calls: List<BuilderCall>,
    packagePath: String,
    output: JvmOutput,
): List<String> {
    var classes = 0

    fun nextClass(): String = "$packagePath/Declare${classes++}"

    var level = batchCallsByBytecodeBudget(calls).map { chunk ->
        val owner = nextClass()
        output.classFile(owner, declareClass(owner) { method -> chunk.forEach { call -> method.call(call) } })
        owner
    }
    while (level.size > MAX_FANOUT) {
        level = level.chunked(MAX_FANOUT).map { members ->
            val owner = nextClass()
            output.classFile(owner, declareClass(owner) { method -> method.declareAll(members) })
            owner
        }
    }
    return level
}

internal fun MethodVisitor.declareAll(
    classes: List<String>,
) {
    classes.forEach { owner ->
        visitVarInsn(Opcodes.ALOAD, 0)
        visitMethodInsn(Opcodes.INVOKESTATIC, owner, DECLARE, DECLARE_DESCRIPTOR, false)
    }
}

private fun batchCallsByBytecodeBudget(
    calls: List<BuilderCall>,
): List<List<BuilderCall>> {
    val chunks = ArrayList<List<BuilderCall>>()
    var current = ArrayList<BuilderCall>()
    var bytes = RETURN_OPCODE_BYTES
    calls.forEach { call ->
        val size = maxCallBytecodeBytes(call)
        if (current.isNotEmpty() && bytes + size > MAX_DECLARE_BYTES) {
            chunks += current
            current = ArrayList()
            bytes = RETURN_OPCODE_BYTES
        }
        current += call
        bytes += size
    }
    if (current.isNotEmpty()) chunks += current
    return chunks
}

private fun declareClass(
    owner: String,
    body: (MethodVisitor) -> Unit,
): ByteArray {
    val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
    writer.visit(CLASS_FILE_VERSION, Opcodes.ACC_FINAL or Opcodes.ACC_SUPER, owner, null, ModuleLowering.OBJECT, null)
    writer.visitMethod(Opcodes.ACC_STATIC, DECLARE, DECLARE_DESCRIPTOR, null, null).apply {
        visitCode()
        body(this)
        visitInsn(Opcodes.RETURN)
        visitMaxs(0, 0)
        visitEnd()
    }
    writer.visitEnd()
    return writer.toByteArray()
}

private fun MethodVisitor.call(
    call: BuilderCall,
) {
    visitVarInsn(Opcodes.ALOAD, 0)
    val descriptor = StringBuilder("(")
    call.arguments.forEach { argument ->
        when (argument) {
            is Int -> {
                pushInt(argument)
                descriptor.append('I')
            }
            is Long -> {
                when (argument) {
                    0L -> visitInsn(Opcodes.LCONST_0)
                    1L -> visitInsn(Opcodes.LCONST_1)
                    else -> visitLdcInsn(argument)
                }
                descriptor.append('J')
            }
            is Boolean -> {
                visitInsn(if (argument) Opcodes.ICONST_1 else Opcodes.ICONST_0)
                descriptor.append('Z')
            }
            is String -> {
                visitLdcInsn(argument)
                descriptor.append("Ljava/lang/String;")
            }
            else -> throw IllegalArgumentException("a builder call cannot take $argument")
        }
    }
    descriptor.append(")V")
    visitMethodInsn(Opcodes.INVOKEVIRTUAL, DECLARATIONS_BUILDER, call.methodName, descriptor.toString(), false)
}

private fun maxCallBytecodeBytes(
    call: BuilderCall,
): Int = 1 + call.arguments.size * 3 + 3

private const val RETURN_OPCODE_BYTES = 1

internal const val MAX_DECLARE_BYTES = 6_000
