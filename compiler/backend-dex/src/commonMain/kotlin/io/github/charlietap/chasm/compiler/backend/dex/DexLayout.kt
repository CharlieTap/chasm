package io.github.charlietap.chasm.compiler.backend.dex

import io.github.charlietap.dexter.DexFile
import io.github.charlietap.dexter.Dexter

internal class DexLayout(
    val files: List<IntRange>,
    val entriesWithCode: Boolean,
) {
    fun holdsEntries(file: Int): Boolean = entriesWithCode && file == files.lastIndex

    fun beginFile(
        file: Int,
        reusable: Dexter,
    ): DexFile = (if (holdsEntries(file)) Dexter() else reusable).beginFile()
}

internal fun partitionedLayout(
    plan: ModulePlan,
): DexLayout {
    val files = contiguousFiles(LongArray(plan.batches.size) { batch -> plan.batchWork(batch).toLong() })
    return DexLayout(files, entriesWithCode = files.size == 1)
}

internal fun filePerBatchLayout(
    plan: ModulePlan,
): DexLayout = DexLayout(plan.batches.indices.map { batch -> batch..batch }, entriesWithCode = false)

internal fun contiguousFiles(
    work: LongArray,
): List<IntRange> {
    val batches = work.size
    if (batches == 0) return emptyList()
    val total = work.sum()
    val count = if (total <= ANDROID_MINIMUM_PARALLEL_WORK) 1 else ((total + FILE_WORK - 1) / FILE_WORK).toInt().coerceIn(1, minOf(MAX_FILES, batches))

    val files = ArrayList<IntRange>(count)
    var start = 0
    var cumulative = 0L
    for (batch in 0 until batches) {
        cumulative += work[batch]
        val remainingFiles = count - files.size - 1
        val remainingBatches = batches - batch - 1
        val boundary = total * (files.size + 1) / count
        if (remainingFiles > 0 && remainingBatches >= remainingFiles && (cumulative >= boundary || remainingBatches == remainingFiles)) {
            files += start..batch
            start = batch + 1
        }
    }
    files += start until batches
    return files
}

private const val FILE_WORK = 4_000L

private const val MAX_FILES = 64
