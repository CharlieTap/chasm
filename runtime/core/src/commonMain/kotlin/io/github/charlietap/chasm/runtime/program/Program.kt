package io.github.charlietap.chasm.runtime.program

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import kotlin.jvm.JvmOverloads

class Program
    @JvmOverloads
    constructor(initialCapacity: Int = INITIAL_CAPACITY) {

        var instructions: Array<DispatchableInstruction> = dispatchableArray(initialCapacity)
            private set

        var size: Int = 0
            private set

        fun append(value: DispatchableInstruction): Int {
            ensureCapacity(size + 1)
            val index = size++
            instructions[index] = value
            return index
        }

        fun append(values: Array<DispatchableInstruction>): Int {
            val entryIp = size
            if (values.isEmpty()) return entryIp

            ensureCapacity(size + values.size)
            values.copyInto(instructions, destinationOffset = size)
            size += values.size
            return entryIp
        }

        fun replace(index: Int, value: DispatchableInstruction) {
            require(index in 0 until size) {
                "program instruction index is out of bounds"
            }
            instructions[index] = value
        }

        fun truncate(size: Int) {
            require(size in 0..this.size) {
                "program size is out of bounds"
            }
            for (index in size until this.size) {
                instructions[index] = unavailableInstruction
            }
            this.size = size
        }

        private fun ensureCapacity(requiredCapacity: Int) {
            if (requiredCapacity <= instructions.size) return

            var capacity = instructions.size
            while (capacity < requiredCapacity) {
                capacity *= 2
            }
            @Suppress("UNCHECKED_CAST")
            val grown = instructions.copyOf(capacity) as Array<DispatchableInstruction>
            instructions = grown
        }
    }

const val EXIT_IP = -1

private const val INITIAL_CAPACITY = 256

private val unavailableInstruction = DispatchableInstruction { _, _, _, _, _ ->
    error("unavailable program instruction cannot be dispatched")
}

@Suppress("UNCHECKED_CAST")
private fun dispatchableArray(capacity: Int): Array<DispatchableInstruction> {
    require(capacity > 0) {
        "program capacity must be positive"
    }
    return arrayOfNulls<DispatchableInstruction>(capacity) as Array<DispatchableInstruction>
}
