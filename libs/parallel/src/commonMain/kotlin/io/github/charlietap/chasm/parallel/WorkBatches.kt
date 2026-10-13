package io.github.charlietap.chasm.parallel

/** Consecutive items grouped into batches. A batch covers the items from [start] until [end]. */
class WorkBatches
    @PublishedApi
    internal constructor(
        private val starts: IntArray,
    ) {

        val count: Int
            get() = starts.size - 1

        fun start(batch: Int): Int = starts[batch]

        fun end(batch: Int): Int = starts[batch + 1]
    }

/** Cuts [itemCount] items into batches of at least [batchWork], given the [work] in each item. */
inline fun workBatches(
    itemCount: Int,
    batchWork: Int,
    work: (item: Int) -> Int,
): WorkBatches {
    var starts = IntArray(INITIAL_BATCH_CAPACITY)
    var count = 0
    var currentWork = batchWork.toLong()
    for (item in 0 until itemCount) {
        if (currentWork >= batchWork) {
            if (count + 1 >= starts.size) starts = starts.copyOf(starts.size * 2)
            starts[count++] = item
            currentWork = 0
        }
        currentWork += work(item)
    }
    starts[count++] = itemCount
    return WorkBatches(starts.copyOf(count))
}

fun singleBatch(itemCount: Int): WorkBatches = WorkBatches(intArrayOf(0, itemCount))

@PublishedApi
internal const val INITIAL_BATCH_CAPACITY = 16
