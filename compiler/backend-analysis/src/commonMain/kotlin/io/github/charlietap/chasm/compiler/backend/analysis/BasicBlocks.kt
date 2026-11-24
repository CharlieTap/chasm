package io.github.charlietap.chasm.compiler.backend.analysis

class BasicBlocks(
    val leaders: IntArray,
    val blockAt: IntArray,
) {
    fun end(block: Int): Int {
        return if (block + 1 < leaders.size) leaders[block + 1] else blockAt.size
    }
}

fun basicBlocks(
    isLeader: BooleanArray,
    size: Int,
): BasicBlocks {

    var blocks = 0
    for (position in 0 until size) {
        if (isLeader[position]) {
            blocks++
        }
    }

    val leaders = IntArray(blocks)
    val blockAt = IntArray(size)
    var block = -1
    for (position in 0 until size) {
        if (isLeader[position]) {
            leaders[++block] = position
        }
        blockAt[position] = block
    }

    return BasicBlocks(leaders, blockAt)
}
