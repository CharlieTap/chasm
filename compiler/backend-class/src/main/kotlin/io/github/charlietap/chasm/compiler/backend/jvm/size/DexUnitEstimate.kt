package io.github.charlietap.chasm.compiler.backend.jvm.size

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.compiler.backend.jvm.abi.ART_ESTIMATED_METHOD_UNITS
import io.github.charlietap.chasm.compiler.backend.jvm.abi.MAX_SHORT_BRANCH_METHOD_BYTES
import org.objectweb.asm.Label
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.IincInsnNode
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.LookupSwitchInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.TableSwitchInsnNode
import java.util.IdentityHashMap

@InternalChasmApi
data class DexUnitEstimate(
    override val limit: Int = ART_ESTIMATED_METHOD_UNITS,
) : MethodSize {

    override val dispatcherLimit: Int = limit

    override val chunkOverhead: Int = 220

    override val switchAllowance: Int = 1

    override fun of(
        method: MethodNode,
        bytecodeLength: Int,
    ): Int {

        var units = 0
        method.instructions.forEach { instruction -> units += unitsOf(instruction) }

        return maxOf(units, scaled(bytecodeLength))
    }

    override fun before(
        method: MethodNode,
        positions: Int,
        placement: (Int) -> Label,
    ): IntArray {

        val unitsAt = IdentityHashMap<LabelNode, Int>()
        var units = 0
        method.instructions.forEach { instruction ->
            if (instruction is LabelNode) {
                unitsAt[instruction] = units
            }
            units += unitsOf(instruction)
        }

        return IntArray(positions + 1) { position ->
            val label = placement(position).info as LabelNode
            maxOf(unitsAt.getValue(label), scaled(label.label.offset))
        }
    }

    override fun describe(size: Int): String {
        return "$size estimated DEX code units"
    }

    private fun scaled(bytes: Int): Int {
        return ((bytes.toLong() * limit + MAX_SHORT_BRANCH_METHOD_BYTES - 1) / MAX_SHORT_BRANCH_METHOD_BYTES).toInt()
    }

    private fun unitsOf(instruction: AbstractInsnNode): Int {
        val opcode = instruction.opcode
        return when {
            opcode < 0 -> 0
            opcode in Opcodes.ICONST_M1..Opcodes.ICONST_5 -> 1
            opcode == Opcodes.ACONST_NULL -> 1
            opcode == Opcodes.LCONST_0 || opcode == Opcodes.LCONST_1 -> 2
            opcode in Opcodes.FCONST_0..Opcodes.FCONST_2 -> 3
            opcode == Opcodes.DCONST_0 || opcode == Opcodes.DCONST_1 -> 5
            instruction is IntInsnNode && (opcode == Opcodes.BIPUSH || opcode == Opcodes.SIPUSH) -> intConstant(instruction.operand.toLong())
            instruction is LdcInsnNode -> when (val constant = instruction.cst) {
                is Int -> intConstant(constant.toLong())
                is Long -> longConstant(constant)
                is Float -> 3
                is Double -> 5
                else -> 2
            }
            instruction is MethodInsnNode -> if (instruction.desc.endsWith(")V")) 3 else 4
            instruction is TableSwitchInsnNode -> 7 + 2 * instruction.labels.size
            instruction is LookupSwitchInsnNode -> 5 + 4 * instruction.labels.size
            instruction is IincInsnNode -> 2
            opcode in Opcodes.IFEQ..Opcodes.IF_ACMPNE || opcode == Opcodes.IFNULL || opcode == Opcodes.IFNONNULL -> 2
            opcode == Opcodes.GOTO -> 1
            opcode in Opcodes.IRETURN..Opcodes.RETURN -> 1
            opcode in Opcodes.GETSTATIC..Opcodes.PUTFIELD -> 2
            opcode in Opcodes.IALOAD..Opcodes.SALOAD || opcode in Opcodes.IASTORE..Opcodes.SASTORE -> 2
            opcode in Opcodes.INEG..Opcodes.DNEG -> 1
            opcode in Opcodes.IADD..Opcodes.LXOR -> 2
            opcode in Opcodes.I2L..Opcodes.I2S -> 1
            opcode in Opcodes.LCMP..Opcodes.DCMPG -> 2
            opcode == Opcodes.NEW || opcode == Opcodes.NEWARRAY || opcode == Opcodes.ANEWARRAY || opcode == Opcodes.MULTIANEWARRAY -> 2
            opcode == Opcodes.CHECKCAST || opcode == Opcodes.INSTANCEOF -> 2
            opcode == Opcodes.ATHROW -> 1
            opcode == Opcodes.ARRAYLENGTH -> 1
            else -> 0
        }
    }

    private fun intConstant(value: Long): Int {
        return when (value) {
            in -8L..7L -> 1
            in Short.MIN_VALUE..Short.MAX_VALUE -> 2
            else -> 3
        }
    }

    private fun longConstant(value: Long): Int {
        return when (value) {
            in Short.MIN_VALUE..Short.MAX_VALUE -> 2
            in Int.MIN_VALUE..Int.MAX_VALUE -> 3
            else -> 5
        }
    }
}
