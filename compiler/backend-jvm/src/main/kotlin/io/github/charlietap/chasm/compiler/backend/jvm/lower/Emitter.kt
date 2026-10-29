package io.github.charlietap.chasm.compiler.backend.jvm.lower

import io.github.charlietap.chasm.compiler.backend.abi.MethodRef
import io.github.charlietap.chasm.compiler.backend.abi.internalName
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

internal class Emitter(
    private val visitor: MethodVisitor,
    private val frameLocals: Array<Any>,
) {

    private var framed = false

    private var codeSinceFrame = true

    fun label(): Label = Label()

    fun place(
        label: Label,
        stack: Any? = null,
    ) {
        visitor.visitLabel(label)
        if (!codeSinceFrame) return
        codeSinceFrame = false
        val stackItems: Array<Any> = if (stack == null) emptyArray() else arrayOf(stack)
        if (!framed) {
            framed = true
            visitor.visitFrame(Opcodes.F_FULL, frameLocals.size, frameLocals, stackItems.size, stackItems)
        } else if (stack == null) {
            visitor.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
        } else {
            visitor.visitFrame(Opcodes.F_SAME1, 0, null, 1, stackItems)
        }
    }

    fun insn(
        opcode: Int,
    ) = code { visitor.visitInsn(opcode) }

    fun load(
        opcode: Int,
        local: Int,
    ) = code { visitor.visitVarInsn(opcode, local) }

    fun store(
        opcode: Int,
        local: Int,
    ) = code { visitor.visitVarInsn(opcode, local) }

    fun fixedWidthInt(
        value: Int,
    ) = code {
        require(value in 0..Short.MAX_VALUE) { "$value does not fit a fixed-width push" }
        visitor.visitIntInsn(Opcodes.SIPUSH, value)
    }

    fun int(
        value: Int,
    ) = code {
        when (value) {
            in -1..5 -> visitor.visitInsn(Opcodes.ICONST_0 + value)
            in Byte.MIN_VALUE..Byte.MAX_VALUE -> visitor.visitIntInsn(Opcodes.BIPUSH, value)
            in Short.MIN_VALUE..Short.MAX_VALUE -> visitor.visitIntInsn(Opcodes.SIPUSH, value)
            else -> visitor.visitLdcInsn(value)
        }
    }

    fun long(
        value: Long,
    ) = code {
        when (value) {
            0L -> visitor.visitInsn(Opcodes.LCONST_0)
            1L -> visitor.visitInsn(Opcodes.LCONST_1)
            else -> visitor.visitLdcInsn(value)
        }
    }

    fun float(
        bits: Int,
    ) = code {
        when {
            bits == 0 -> visitor.visitInsn(Opcodes.FCONST_0)
            bits == 1f.toRawBits() -> visitor.visitInsn(Opcodes.FCONST_1)
            bits == 2f.toRawBits() -> visitor.visitInsn(Opcodes.FCONST_2)
            Float.fromBits(bits).isNaN() -> {
                visitor.visitLdcInsn(bits)
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Float", "intBitsToFloat", "(I)F", false)
            }
            else -> visitor.visitLdcInsn(Float.fromBits(bits))
        }
    }

    fun double(
        bits: Long,
    ) = code {
        when {
            bits == 0L -> visitor.visitInsn(Opcodes.DCONST_0)
            bits == 1.0.toRawBits() -> visitor.visitInsn(Opcodes.DCONST_1)
            Double.fromBits(bits).isNaN() -> {
                visitor.visitLdcInsn(bits)
                visitor.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Double", "longBitsToDouble", "(J)D", false)
            }
            else -> visitor.visitLdcInsn(Double.fromBits(bits))
        }
    }

    fun string(
        value: String,
    ) = code { visitor.visitLdcInsn(value) }

    fun invokeStatic(
        method: MethodRef,
    ) = code {
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC, internalName(method.owner), method.name, method.descriptor, false)
    }

    fun invokeVirtual(
        method: MethodRef,
    ) = code {
        visitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL, internalName(method.owner), method.name, method.descriptor, false)
    }

    fun invokeInterface(
        method: MethodRef,
    ) = code {
        visitor.visitMethodInsn(Opcodes.INVOKEINTERFACE, internalName(method.owner), method.name, method.descriptor, true)
    }

    fun invokeSpecial(
        method: MethodRef,
    ) = code {
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL, internalName(method.owner), method.name, method.descriptor, false)
    }

    fun getField(
        owner: String,
        name: String,
        type: String,
    ) = code {
        visitor.visitFieldInsn(Opcodes.GETFIELD, internalName(owner), name, type)
    }

    fun putField(
        owner: String,
        name: String,
        type: String,
    ) = code {
        visitor.visitFieldInsn(Opcodes.PUTFIELD, internalName(owner), name, type)
    }

    fun getStatic(
        owner: String,
        name: String,
        type: String,
    ) = code {
        visitor.visitFieldInsn(Opcodes.GETSTATIC, internalName(owner), name, type)
    }

    fun putStatic(
        owner: String,
        name: String,
        type: String,
    ) = code {
        visitor.visitFieldInsn(Opcodes.PUTSTATIC, internalName(owner), name, type)
    }

    fun type(
        opcode: Int,
        type: String,
    ) = code { visitor.visitTypeInsn(opcode, internalName(type)) }

    fun newLongArray() = code { visitor.visitIntInsn(Opcodes.NEWARRAY, Opcodes.T_LONG) }

    fun jump(
        opcode: Int,
        label: Label,
    ) = code { visitor.visitJumpInsn(opcode, label) }

    fun tableSwitch(
        minimum: Int,
        default: Label,
        targets: Array<Label>,
    ) = code {
        visitor.visitTableSwitchInsn(minimum, minimum + targets.size - 1, default, *targets)
    }

    fun lookupSwitch(
        default: Label,
        keys: IntArray,
        targets: Array<Label>,
    ) = code {
        visitor.visitLookupSwitchInsn(default, keys, targets)
    }

    fun tryCatch(
        start: Label,
        end: Label,
        handler: Label,
        type: String?,
    ) = visitor.visitTryCatchBlock(start, end, handler, type?.let(::internalName))

    fun line(
        line: Int,
        start: Label,
    ) = visitor.visitLineNumber(line, start)

    private inline fun code(
        write: () -> Unit,
    ) {
        write()
        codeSinceFrame = true
    }
}
