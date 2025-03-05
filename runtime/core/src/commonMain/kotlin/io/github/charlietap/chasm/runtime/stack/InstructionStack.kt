package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import kotlin.jvm.JvmOverloads

class InstructionStack
    @JvmOverloads
    constructor(minCapacity: Int = MIN_CAPACITY) {

        private var elements: Array<DispatchableInstruction?>
        private var top = 0

        init {
            val arrayCapacity: Int =
                if (minCapacity.countOneBits() != 1) {
                    (minCapacity - 1).takeHighestOneBit() shl 1
                } else {
                    minCapacity
                }
            elements = arrayOfNulls<DispatchableInstruction?>(arrayCapacity)
        }

        fun push(value: DispatchableInstruction) {
            elements[top] = value
            top++
            if (top == elements.size) {
                doubleCapacity()
            }
        }

        fun pushAll(values: Array<DispatchableInstruction>) {
            val requiredSize = top + values.size
            while (requiredSize > elements.size) {
                doubleCapacity()
            }
            values.copyInto(elements, startIndex = 0, endIndex = values.size, destinationOffset = top)
            top += values.size
        }

        // we intentionally leave the instruction in the stack
        // and just move the stack pointer, this memory will not be reclaimed
        // irrespective as the thread object has a reference to it,
        // and we benefit from the performance not setting it on each loop
        fun pop(): DispatchableInstruction {
            top--
            val value = elements[top]
            // elements[top] = null
            return value!!
        }

        fun peekOrNull(): DispatchableInstruction? = try {
            elements[top - 1]
        } catch (_: Exception) {
            null
        }

        fun peekNthOrNull(n: Int): DispatchableInstruction? = try {
            elements[top - 1 - n]
        } catch (_: Exception) {
            null
        }

        fun shrink(depth: Int) {
            top = depth
        }

        fun depth(): Int = top

        fun clear() {
            for (i in 0 until top) {
                elements[i] = null
            }
            top = 0
        }

        fun entries() = buildList {
            for (i in 0 until top) {
                @Suppress("UNCHECKED_CAST")
                add(elements[i] as DispatchableInstruction)
            }
        }

        private fun doubleCapacity() {
            val newCapacity = elements.size * 2
            elements = elements.copyOf(newCapacity)
        }
    }

private const val MIN_CAPACITY = 256
