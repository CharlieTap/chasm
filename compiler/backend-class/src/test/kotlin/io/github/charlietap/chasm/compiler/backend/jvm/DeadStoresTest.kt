package io.github.charlietap.chasm.compiler.backend.jvm

import io.github.charlietap.chasm.compiler.backend.abi.INT_BITS_TO_FLOAT
import io.github.charlietap.chasm.compiler.backend.abi.LONG_BITS_TO_DOUBLE
import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.jvm.abi.CLASS_FILE_VERSION
import io.github.charlietap.chasm.compiler.backend.jvm.lower.Emitter
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.FrameNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode
import kotlin.test.Test
import kotlin.test.assertEquals

class DeadStoresTest {

    @Test
    fun `stores to locals of every width that are never read are removed and typed top in the frame`() {

        val method = lowered("(I)I", 1, arrayOf(Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.LONG, Opcodes.FLOAT, Opcodes.DOUBLE, Opcodes.INTEGER)) {
            int(9)
            store(Opcodes.ISTORE, 0)
            int(1)
            store(Opcodes.ISTORE, 1)
            long(2L)
            store(Opcodes.LSTORE, 2)
            float(3f.toRawBits())
            store(Opcodes.FSTORE, 4)
            double(4.0.toRawBits())
            store(Opcodes.DSTORE, 5)
            int(5)
            store(Opcodes.ISTORE, 7)
            place(Label())
            load(Opcodes.ILOAD, 1)
            load(Opcodes.ILOAD, 7)
            insn(Opcodes.IADD)
            insn(Opcodes.IRETURN)
        }

        assertEquals(listOf(0, 1, 7), stores(method))
        assertEquals(listOf<Any>(Opcodes.INTEGER, Opcodes.INTEGER, Opcodes.TOP, Opcodes.TOP, Opcodes.TOP, Opcodes.TOP, Opcodes.TOP, Opcodes.INTEGER), frame(method))
        assertEquals(6, run(method, 0))
    }

    @Test
    fun `a duplicated value stored to an unread local loses the duplicate and keeps the original`() {

        val method = lowered("()J", 0, arrayOf(Opcodes.LONG, Opcodes.LONG, Opcodes.INTEGER, Opcodes.INTEGER)) {
            long(7L)
            insn(Opcodes.DUP2)
            store(Opcodes.LSTORE, 2)
            store(Opcodes.LSTORE, 0)
            int(3)
            insn(Opcodes.DUP)
            store(Opcodes.ISTORE, 5)
            store(Opcodes.ISTORE, 4)
            place(Label())
            load(Opcodes.LLOAD, 0)
            load(Opcodes.ILOAD, 4)
            insn(Opcodes.I2L)
            insn(Opcodes.LADD)
            insn(Opcodes.LRETURN)
        }

        assertEquals(listOf(0, 4), stores(method))
        assertEquals(emptyList(), method.instructions.filter { instruction -> instruction.opcode == Opcodes.DUP || instruction.opcode == Opcodes.DUP2 })
        assertEquals(listOf<Any>(Opcodes.LONG, Opcodes.TOP, Opcodes.TOP, Opcodes.INTEGER), frame(method))
        assertEquals(10L, run(method))
    }

    @Test
    fun `a bit cast stored to an unread local is removed with the load it converts`() {

        val method = lowered("()J", 0, arrayOf(Opcodes.INTEGER, Opcodes.FLOAT, Opcodes.LONG, Opcodes.DOUBLE)) {
            int(5)
            store(Opcodes.ISTORE, 0)
            load(Opcodes.ILOAD, 0)
            invokeStatic(INT_BITS_TO_FLOAT)
            store(Opcodes.FSTORE, 1)
            long(9L)
            store(Opcodes.LSTORE, 2)
            load(Opcodes.LLOAD, 2)
            invokeStatic(LONG_BITS_TO_DOUBLE)
            store(Opcodes.DSTORE, 4)
            load(Opcodes.LLOAD, 2)
            insn(Opcodes.L2I)
            invokeStatic(INT_BITS_TO_FLOAT)
            store(Opcodes.FSTORE, 1)
            place(Label())
            load(Opcodes.ILOAD, 0)
            insn(Opcodes.I2L)
            load(Opcodes.LLOAD, 2)
            insn(Opcodes.LADD)
            insn(Opcodes.LRETURN)
        }

        assertEquals(listOf(0, 2), stores(method))
        assertEquals(emptyList(), method.instructions.filter { instruction -> instruction.opcode == Opcodes.INVOKESTATIC || instruction.opcode == Opcodes.L2I })
        assertEquals(listOf<Any>(Opcodes.INTEGER, Opcodes.TOP, Opcodes.LONG), frame(method))
        assertEquals(14L, run(method))
    }

    @Test
    fun `a value from a call stored to an unread local keeps the call and the store`() {

        val method = lowered("()J", 0, arrayOf(Opcodes.LONG)) {
            invokeStatic(NANO_TIME)
            store(Opcodes.LSTORE, 0)
            place(Label())
            long(1L)
            insn(Opcodes.LRETURN)
        }

        assertEquals(listOf(0), stores(method))
        assertEquals(listOf<Any>(Opcodes.LONG), frame(method))
        assertEquals(1L, run(method))
    }

    private fun lowered(
        descriptor: String,
        firstLocalAfterParameters: Int,
        frameLocals: Array<Any>,
        body: Emitter.() -> Unit,
    ): MethodNode {

        val method = MethodNode(Opcodes.ASM9, Opcodes.ACC_PUBLIC or Opcodes.ACC_STATIC, METHOD, descriptor, null, null)
        method.visitCode()

        Emitter(method, frameLocals).apply {

            body()

            removeDeadStores(firstLocalAfterParameters)
        }

        method.visitMaxs(0, 0)
        method.visitEnd()

        return method
    }

    private fun stores(method: MethodNode): List<Int> {
        return method.instructions.filterIsInstance<VarInsnNode>()
            .filter { instruction -> instruction.opcode in Opcodes.ISTORE..Opcodes.ASTORE }
            .map { instruction -> instruction.`var` }
            .distinct()
    }

    private fun frame(method: MethodNode): List<Any> {
        return method.instructions.filterIsInstance<FrameNode>().first().local
    }

    private fun run(
        method: MethodNode,
        vararg arguments: Any,
    ): Any? {

        val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
        writer.visit(CLASS_FILE_VERSION, Opcodes.ACC_PUBLIC, OWNER, null, "java/lang/Object", null)
        method.accept(writer)
        writer.visitEnd()

        val type = SingleClassLoader(OWNER, writer.toByteArray()).loadClass(OWNER)
        return type.declaredMethods.single().invoke(null, *arguments)
    }

    private class SingleClassLoader(
        private val name: String,
        private val bytes: ByteArray,
    ) : ClassLoader(DeadStoresTest::class.java.classLoader) {
        override fun findClass(name: String): Class<*> {
            if (name != this.name) {
                throw ClassNotFoundException(name)
            }
            return defineClass(name, bytes, 0, bytes.size)
        }
    }

    private companion object {
        const val OWNER = "DeadStoresProbe"
        const val METHOD = "run"
        val NANO_TIME = MethodRef("Ljava/lang/System;", "nanoTime", emptyList(), "J")
    }
}
