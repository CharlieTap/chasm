package io.github.charlietap.chasm.compiler.backend.jvm.size

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodNode

internal fun probeMethod(emit: MethodVisitor.() -> Unit): MethodNode {
    return MethodNode(Opcodes.ASM9, Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, "probe", "()I", null, null).apply {
        visitCode()
        emit()
        visitMaxs(0, 0)
        visitEnd()
    }
}

internal fun resolveOffsets(node: MethodNode) {

    val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
    writer.visit(Opcodes.V11, Opcodes.ACC_PUBLIC, "Probe", null, "java/lang/Object", null)
    node.accept(writer)
}
