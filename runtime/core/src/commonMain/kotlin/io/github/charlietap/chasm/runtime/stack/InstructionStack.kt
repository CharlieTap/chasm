package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.store.Store
import kotlin.jvm.JvmOverloads

class InstructionStack
    @JvmOverloads
    constructor(minCapacity: Int = MIN_CAPACITY) {

        private var elements: Array<DispatchableInstruction>
        private var top = 0

        init {
            val arrayCapacity: Int =
                if (minCapacity.countOneBits() != 1) {
                    (minCapacity - 1).takeHighestOneBit() shl 1
                } else {
                    minCapacity
                }
            elements = dispatchableArray(arrayCapacity)
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
            // elements[top] = null
            return elements[top]
        }

        fun execute(
            vstack: ValueStack,
            cstack: ControlStack,
            store: Store,
            context: ExecutionContext,
        ) {
            while (top != 0) {
                top--
                elements[top](vstack, cstack, store, context)
            }
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
            @Suppress("UNCHECKED_CAST")
            val nullableElements = elements as Array<DispatchableInstruction?>
            for (i in 0 until top) {
                nullableElements[i] = null
            }
            top = 0
        }

        fun entries() = buildList {
            for (i in 0 until top) {
                add(elements[i])
            }
        }

        private fun doubleCapacity() {
            val newCapacity = elements.size * 2
            @Suppress("UNCHECKED_CAST")
            elements = elements.copyOf(newCapacity) as Array<DispatchableInstruction>
        }
    }

private const val MIN_CAPACITY = 256

@Suppress("UNCHECKED_CAST")
private fun dispatchableArray(capacity: Int): Array<DispatchableInstruction> =
    arrayOfNulls<DispatchableInstruction>(capacity) as Array<DispatchableInstruction>
