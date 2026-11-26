package io.github.charlietap.chasm.compiler.backend.jvm.size

import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import kotlin.test.Test
import kotlin.test.assertContentEquals

class BytecodeSizeTest {

    @Test
    fun `the size before a position is its label's bytecode offset`() {

        val labels = Array(3) { Label() }
        val node = probeMethod {
            visitLabel(labels[0])
            visitInsn(Opcodes.ICONST_1)
            visitLabel(labels[1])
            visitMethodInsn(Opcodes.INVOKESTATIC, "Probe", "call", "(I)I", false)
            visitLabel(labels[2])
            visitInsn(Opcodes.IRETURN)
        }
        resolveOffsets(node)

        val before = BytecodeSize().before(node, 2) { position -> labels[position] }

        assertContentEquals(intArrayOf(0, 1, 4), before)
    }
}
