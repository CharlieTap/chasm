package io.github.charlietap.chasm.compiler.backend.jvm.size

import io.github.charlietap.chasm.compiler.backend.jvm.abi.HOTSPOT_METHOD_BYTES
import io.github.charlietap.chasm.compiler.backend.jvm.abi.MAX_SHORT_BRANCH_METHOD_BYTES
import org.objectweb.asm.Label
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.MethodNode

data class BytecodeSize(
    override val limit: Int = HOTSPOT_METHOD_BYTES,
) : MethodSize {

    override val dispatcherLimit: Int = MAX_SHORT_BRANCH_METHOD_BYTES

    override val chunkOverhead: Int = 512

    override val switchAllowance: Int = 3

    override fun of(
        method: MethodNode,
        bytecodeLength: Int,
    ): Int {
        return bytecodeLength
    }

    override fun before(
        method: MethodNode,
        positions: Int,
        placement: (Int) -> Label,
    ): IntArray {
        return IntArray(positions + 1) { position -> (placement(position).info as LabelNode).label.offset }
    }

    override fun describe(size: Int): String {
        return "$size bytes"
    }
}
