package io.github.charlietap.chasm.compiler.backend.jvm.size

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DexUnitEstimateTest {

    private val estimate = DexUnitEstimate()

    @Test
    fun `each instruction counts the DEX units it becomes`() {

        val rows = listOf(
            Row("iconst_m1", 1) { visitInsn(Opcodes.ICONST_M1) },
            Row("iconst_5", 1) { visitInsn(Opcodes.ICONST_5) },
            Row("aconst_null", 1) { visitInsn(Opcodes.ACONST_NULL) },
            Row("lconst_1", 2) { visitInsn(Opcodes.LCONST_1) },
            Row("fconst_2", 3) { visitInsn(Opcodes.FCONST_2) },
            Row("dconst_1", 5) { visitInsn(Opcodes.DCONST_1) },
            Row("bipush -8", 1) { visitIntInsn(Opcodes.BIPUSH, -8) },
            Row("bipush 7", 1) { visitIntInsn(Opcodes.BIPUSH, 7) },
            Row("bipush 8", 2) { visitIntInsn(Opcodes.BIPUSH, 8) },
            Row("sipush 32767", 2) { visitIntInsn(Opcodes.SIPUSH, 32_767) },
            Row("ldc 32768", 3) { visitLdcInsn(32_768) },
            Row("ldc -9", 2) { visitLdcInsn(-9) },
            Row("ldc 32767L", 2) { visitLdcInsn(32_767L) },
            Row("ldc 32768L", 3) { visitLdcInsn(32_768L) },
            Row("ldc 2^31", 5) { visitLdcInsn(1L shl 31) },
            Row("ldc float", 3) { visitLdcInsn(1.5f) },
            Row("ldc double", 5) { visitLdcInsn(1.5) },
            Row("ldc string", 2) { visitLdcInsn("text") },
            Row("ldc class", 2) { visitLdcInsn(Type.getObjectType("Probe")) },
            Row("invoke returning void", 3) { visitMethodInsn(Opcodes.INVOKESTATIC, "Probe", "call", "(IJ)V", false) },
            Row("invoke returning int", 4) { visitMethodInsn(Opcodes.INVOKEVIRTUAL, "Probe", "call", "()I", false) },
            Row("tableswitch with 3 cases", 13) { visitTableSwitchInsn(0, 2, Label(), Label(), Label(), Label()) },
            Row("lookupswitch with 3 cases", 17) { visitLookupSwitchInsn(Label(), intArrayOf(1, 10, 100), arrayOf(Label(), Label(), Label())) },
            Row("iinc", 2) { visitIincInsn(1, 1) },
            Row("ifeq", 2) { visitJumpInsn(Opcodes.IFEQ, Label()) },
            Row("if_acmpne", 2) { visitJumpInsn(Opcodes.IF_ACMPNE, Label()) },
            Row("ifnull", 2) { visitJumpInsn(Opcodes.IFNULL, Label()) },
            Row("ifnonnull", 2) { visitJumpInsn(Opcodes.IFNONNULL, Label()) },
            Row("goto", 1) { visitJumpInsn(Opcodes.GOTO, Label()) },
            Row("ireturn", 1) { visitInsn(Opcodes.IRETURN) },
            Row("return", 1) { visitInsn(Opcodes.RETURN) },
            Row("getstatic", 2) { visitFieldInsn(Opcodes.GETSTATIC, "Probe", "field", "I") },
            Row("putfield", 2) { visitFieldInsn(Opcodes.PUTFIELD, "Probe", "field", "I") },
            Row("iaload", 2) { visitInsn(Opcodes.IALOAD) },
            Row("sastore", 2) { visitInsn(Opcodes.SASTORE) },
            Row("ineg", 1) { visitInsn(Opcodes.INEG) },
            Row("dneg", 1) { visitInsn(Opcodes.DNEG) },
            Row("iadd", 2) { visitInsn(Opcodes.IADD) },
            Row("lxor", 2) { visitInsn(Opcodes.LXOR) },
            Row("i2l", 1) { visitInsn(Opcodes.I2L) },
            Row("i2s", 1) { visitInsn(Opcodes.I2S) },
            Row("lcmp", 2) { visitInsn(Opcodes.LCMP) },
            Row("dcmpg", 2) { visitInsn(Opcodes.DCMPG) },
            Row("new", 2) { visitTypeInsn(Opcodes.NEW, "Probe") },
            Row("newarray", 2) { visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_INT) },
            Row("anewarray", 2) { visitTypeInsn(Opcodes.ANEWARRAY, "Probe") },
            Row("multianewarray", 2) { visitMultiANewArrayInsn("[[I", 2) },
            Row("checkcast", 2) { visitTypeInsn(Opcodes.CHECKCAST, "Probe") },
            Row("instanceof", 2) { visitTypeInsn(Opcodes.INSTANCEOF, "Probe") },
            Row("athrow", 1) { visitInsn(Opcodes.ATHROW) },
            Row("arraylength", 1) { visitInsn(Opcodes.ARRAYLENGTH) },
        )

        rows.forEach { row -> assertEquals(row.units, estimate.of(probeMethod(row.emit), 0), row.name) }
    }

    @Test
    fun `loads, stores and stack shuffles count nothing`() {

        val node = probeMethod {
            visitVarInsn(Opcodes.ILOAD, 0)
            visitVarInsn(Opcodes.ALOAD, 1)
            visitVarInsn(Opcodes.ISTORE, 2)
            visitVarInsn(Opcodes.ASTORE, 3)
            visitInsn(Opcodes.DUP)
            visitInsn(Opcodes.DUP2_X2)
            visitInsn(Opcodes.SWAP)
            visitInsn(Opcodes.POP)
            visitInsn(Opcodes.POP2)
            visitInsn(Opcodes.NOP)
        }

        assertEquals(0, estimate.of(node, 0))
    }

    @Test
    fun `a method over the short-branch byte limit is over the unit limit`() {
        assertEquals(estimate.limit, estimate.of(probeMethod {}, 32_767))
        assertTrue(estimate.of(probeMethod {}, 32_768) > estimate.limit)
    }

    @Test
    fun `the size before a position is the size of the instructions before its label`() {

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

        val before = estimate.before(node, 2) { position -> labels[position] }

        assertContentEquals(intArrayOf(0, 1, 5), before)
    }

    @Test
    fun `the size before a position counts bytes where they outweigh the instructions`() {

        val labels = Array(2) { Label() }
        val node = probeMethod {
            visitLabel(labels[0])
            repeat(1_000) {
                visitVarInsn(Opcodes.ILOAD, 1)
                visitInsn(Opcodes.POP)
            }
            visitLabel(labels[1])
            visitInsn(Opcodes.ICONST_0)
            visitInsn(Opcodes.IRETURN)
        }
        resolveOffsets(node)

        val before = estimate.before(node, 1) { position -> labels[position] }

        assertContentEquals(intArrayOf(0, 315), before)
    }

    private data class Row(
        val name: String,
        val units: Int,
        val emit: MethodVisitor.() -> Unit,
    )
}
