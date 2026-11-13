package io.github.charlietap.chasm.compiler.backend.jvm

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.FrameNode
import org.objectweb.asm.tree.IincInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.VarInsnNode

internal fun removeDeadStores(
    node: MethodNode,
    frameLocals: Array<Any>,
    firstLocalAfterParameters: Int,
) {

    val localCount = frameLocals.sumOf { type -> localSlotCount(type) }
    val readLocals = findReadLocals(node, localCount)
    val deadLocals = removeStoresToUnreadLocals(node, firstLocalAfterParameters, readLocals)
    if (deadLocals.none { dead -> dead }) {
        return
    }

    val frame = node.instructions.firstNotNullOfOrNull { instruction -> instruction as? FrameNode } ?: return
    frame.local = frameLocalsWithDeadAsTop(frameLocals, deadLocals)
}

private fun findReadLocals(
    node: MethodNode,
    localCount: Int,
): BooleanArray {

    val readLocals = BooleanArray(localCount)
    for (instruction in node.instructions) {
        when (instruction) {
            is VarInsnNode -> {
                if (instruction.opcode in Opcodes.ILOAD..Opcodes.ALOAD) {
                    readLocals[instruction.`var`] = true
                }
            }
            is IincInsnNode -> readLocals[instruction.`var`] = true
        }
    }

    return readLocals
}

private fun removeStoresToUnreadLocals(
    node: MethodNode,
    firstLocalAfterParameters: Int,
    readLocals: BooleanArray,
): BooleanArray {

    val deadLocals = BooleanArray(readLocals.size)
    var instruction = node.instructions.first
    while (instruction != null) {

        val next = instruction.next
        if (instruction is VarInsnNode && instruction.opcode in Opcodes.ISTORE..Opcodes.ASTORE &&
            instruction.`var` >= firstLocalAfterParameters && !readLocals[instruction.`var`]
        ) {
            val valueStart = storedValueStart(instruction)
            if (valueStart != null) {

                deadLocals[instruction.`var`] = true

                var current: AbstractInsnNode = valueStart
                while (current !== instruction) {
                    val following = current.next
                    node.instructions.remove(current)
                    current = following
                }
                node.instructions.remove(instruction)
            }
        }

        instruction = next
    }

    return deadLocals
}

private fun storedValueStart(store: VarInsnNode): AbstractInsnNode? {

    val producer = store.previous ?: return null

    return when {
        producer.opcode in Opcodes.ACONST_NULL..Opcodes.LDC -> producer
        producer.opcode in Opcodes.ILOAD..Opcodes.ALOAD -> producer
        producer.opcode == Opcodes.DUP || producer.opcode == Opcodes.DUP2 -> producer
        producer is MethodInsnNode && producer.isBitCast() -> {

            val operand = producer.previous ?: return null

            when {
                operand.opcode in Opcodes.ILOAD..Opcodes.DLOAD -> operand
                operand.opcode == Opcodes.L2I || operand.opcode == Opcodes.I2L ->
                    operand.previous?.takeIf { load -> load.opcode in Opcodes.ILOAD..Opcodes.DLOAD }
                else -> null
            }
        }
        else -> null
    }
}

private fun MethodInsnNode.isBitCast(): Boolean {
    return opcode == Opcodes.INVOKESTATIC && when (owner) {
        "java/lang/Float" -> name == "intBitsToFloat" || name == "floatToRawIntBits"
        "java/lang/Double" -> name == "longBitsToDouble" || name == "doubleToRawLongBits"
        else -> false
    }
}

private fun frameLocalsWithDeadAsTop(
    frameLocals: Array<Any>,
    deadLocals: BooleanArray,
): List<Any> {

    val types = ArrayList<Any>(frameLocals.size + deadLocals.count { dead -> dead })
    var local = 0
    for (type in frameLocals) {
        if (deadLocals[local]) {
            repeat(localSlotCount(type)) { _ -> types += Opcodes.TOP }
        } else {
            types += type
        }
        local += localSlotCount(type)
    }

    while (types.lastOrNull() == Opcodes.TOP) types.removeAt(types.lastIndex)

    return types
}

private fun localSlotCount(type: Any): Int {
    return if (type == Opcodes.LONG || type == Opcodes.DOUBLE) 2 else 1
}
