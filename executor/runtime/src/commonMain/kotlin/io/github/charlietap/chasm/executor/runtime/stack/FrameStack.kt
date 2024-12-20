package io.github.charlietap.chasm.executor.runtime.stack

import kotlin.jvm.JvmOverloads

class FrameStack
    @JvmOverloads
    constructor() {

        private var elements: Array<ActivationFrame?> = arrayOfNulls(CAPACITY)
        private var top = 0

        fun push(value: ActivationFrame) {
            elements[top] = value
            top++
        }

        fun popOrNull(): ActivationFrame? = try {
            top--
            val value = elements[top]
            elements[top] = null
            value
        } catch (_: Exception) {
            null
        }

        fun peekOrNull(): ActivationFrame? = try {
            elements[top - 1]
        } catch (_: Exception) {
            null
        }

        fun peekNthOrNull(n: Int): ActivationFrame? = try {
            elements[top - 1 - n]
        } catch (_: Exception) {
            null
        }

        fun shrink(preserveTopN: Int, depth: Int) {
            elements.copyInto(
                destination = elements,
                destinationOffset = depth,
                startIndex = top - preserveTopN,
                endIndex = top,
            )

            var i = depth + preserveTopN
            while (i < top) {
                elements[i] = null
                i++
            }

            top = depth + preserveTopN
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
                add(elements[i]!!)
            }
        }
    }

private const val CAPACITY = 1028
