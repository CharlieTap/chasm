package io.github.charlietap.chasm.runtime.component.resource

class HostResourcePayloadTable {

    private var payloads = arrayOfNulls<Any>(INITIAL_CAPACITY)
    private var occupied = BooleanArray(INITIAL_CAPACITY)
    private var nextFree = IntArray(INITIAL_CAPACITY)
    private var freeHead = ABSENT_INDEX
    private var nextUnused = 0

    var size: Int = 0
        private set

    fun insert(payload: Any?): Int {
        val index = if (freeHead != ABSENT_INDEX) {
            freeHead.also { freeHead = nextFree[it] }
        } else {
            if (nextUnused == payloads.size) grow()
            nextUnused++
            nextUnused - 1
        }
        payloads[index] = payload
        occupied[index] = true
        size += 1
        return index
    }

    operator fun get(index: Int): Any? {
        requirePayload(index)
        return payloads[index]
    }

    fun remove(index: Int): Any? {
        requirePayload(index)
        val payload = payloads[index]
        payloads[index] = null
        occupied[index] = false
        nextFree[index] = freeHead
        freeHead = index
        size -= 1
        return payload
    }

    private fun requirePayload(index: Int) {
        if (index !in 0 until nextUnused || !occupied[index]) {
            throw ResourceTableException(ResourceTableError.InvalidPayload)
        }
    }

    private fun grow() {
        val capacity = payloads.size shl 1
        payloads = payloads.copyOf(capacity)
        occupied = occupied.copyOf(capacity)
        nextFree = nextFree.copyOf(capacity)
    }
}

private const val INITIAL_CAPACITY = 8
private const val ABSENT_INDEX = -1
