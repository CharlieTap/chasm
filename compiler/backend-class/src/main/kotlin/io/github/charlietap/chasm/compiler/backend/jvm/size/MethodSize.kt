package io.github.charlietap.chasm.compiler.backend.jvm.size

import io.github.charlietap.chasm.InternalChasmApi
import org.objectweb.asm.Label
import org.objectweb.asm.tree.MethodNode

@InternalChasmApi
interface MethodSize {
    val limit: Int
    val dispatcherLimit: Int
    val chunkOverhead: Int
    val switchAllowance: Int

    fun of(
        method: MethodNode,
        bytecodeLength: Int,
    ): Int

    fun before(
        method: MethodNode,
        positions: Int,
        placement: (Int) -> Label,
    ): IntArray

    fun describe(size: Int): String
}
