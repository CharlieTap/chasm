package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.chasm.compiler.backend.analysis.BoundsCheckStorage
import io.github.charlietap.chasm.compiler.backend.analysis.InstructionTableStorage
import io.github.charlietap.chasm.compiler.backend.analysis.form.InstructionFormStorage

internal class LoweringWorkspace {

    val forms = InstructionFormStorage()
    val table = InstructionTableStorage()
    val boundsChecks = BoundsCheckStorage()
    private var typedSignatures = arrayOfNulls<String>(0)
    private var noRedundantChecks = BooleanArray(0)
    private var estimatePrefix = LongArray(0)

    fun estimatePrefix(size: Int): LongArray {
        if (estimatePrefix.size < size || estimatePrefix.size > maxOf(size * RETAINED_GROWTH, RETAINED_CAPACITY)) {
            estimatePrefix = LongArray(size)
        }
        return estimatePrefix
    }

    fun noRedundantChecks(size: Int): BooleanArray {
        if (noRedundantChecks.size < size) noRedundantChecks = BooleanArray(size)
        return noRedundantChecks
    }

    fun typedSignatures(size: Int): Array<String?> {
        if (typedSignatures.size < size || typedSignatures.size > maxOf(size * RETAINED_GROWTH, RETAINED_CAPACITY)) {
            typedSignatures = arrayOfNulls(size)
        } else {
            typedSignatures.fill(null, 0, size)
        }
        return typedSignatures
    }

    private companion object {
        const val RETAINED_GROWTH = 4
        const val RETAINED_CAPACITY = 16_384
    }
}
