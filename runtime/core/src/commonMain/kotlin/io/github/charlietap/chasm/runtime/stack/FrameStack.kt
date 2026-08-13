package io.github.charlietap.chasm.runtime.stack

import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

class FrameStack {

    private var arities = IntArray(INITIAL_CAPACITY)
    private var handlerDepths = IntArray(INITIAL_CAPACITY)
    private var valueDepths = IntArray(INITIAL_CAPACITY)
    private var instances: Array<ModuleInstance?> = arrayOfNulls(INITIAL_CAPACITY)
    private var previousFramePointers = IntArray(INITIAL_CAPACITY)
    private var resultSlotBases = IntArray(INITIAL_CAPACITY)
    private var returnIps = IntArray(INITIAL_CAPACITY)
    private var top = 0

    fun push(value: ActivationFrame) = push(
        arity = value.arity,
        handlerDepth = value.handlerDepth,
        valueDepth = value.valueDepth,
        instance = value.instance,
        previousFramePointer = value.previousFramePointer,
        resultSlotBase = value.resultSlotBase,
        returnIp = value.returnIp,
    )

    fun push(
        arity: Int,
        handlerDepth: Int,
        valueDepth: Int,
        instance: ModuleInstance,
        previousFramePointer: Int,
        resultSlotBase: Int,
        returnIp: Int,
    ) {
        ensureCapacity()

        arities[top] = arity
        handlerDepths[top] = handlerDepth
        valueDepths[top] = valueDepth
        instances[top] = instance
        previousFramePointers[top] = previousFramePointer
        resultSlotBases[top] = resultSlotBase
        returnIps[top] = returnIp
        top++
    }

    fun pop(): ActivationFrame {
        val index = topIndex()
        val frame = frame(index)
        remove(index)
        return frame
    }

    fun discard() {
        val index = topIndex()
        remove(index)
    }

    fun peek(): ActivationFrame = frame(topIndex())

    fun peekNth(n: Int): ActivationFrame = frame(indexOfNth(n))

    fun peekArity(): Int = arities[topIndex()]

    fun peekHandlerDepth(): Int = handlerDepths[topIndex()]

    fun peekValueDepth(): Int = valueDepths[topIndex()]

    fun peekInstance(): ModuleInstance = instances[topIndex()]!!

    fun peekPreviousFramePointer(): Int = previousFramePointers[topIndex()]

    fun peekResultSlotBase(): Int = resultSlotBases[topIndex()]

    fun peekReturnIp(): Int = returnIps[topIndex()]

    fun replaceInstance(instance: ModuleInstance) {
        instances[topIndex()] = instance
    }

    fun shrink(depth: Int) {
        if (depth < top) {
            instances.fill(null, depth, top)
        }
        top = depth
    }

    fun depth(): Int = top

    fun clear() {
        instances.fill(null, 0, top)
        top = 0
    }

    fun entries() = buildList {
        for (index in 0 until top) {
            add(frame(index))
        }
    }

    private fun ensureCapacity() {
        val capacity = arities.size
        if (top < capacity) return
        if (capacity == MAX_CAPACITY) {
            throw InvocationException(InvocationError.CallStackExhausted)
        }

        val newCapacity = minOf(capacity * 2, MAX_CAPACITY)
        arities = arities.copyOf(newCapacity)
        handlerDepths = handlerDepths.copyOf(newCapacity)
        valueDepths = valueDepths.copyOf(newCapacity)
        instances = instances.copyOf(newCapacity)
        previousFramePointers = previousFramePointers.copyOf(newCapacity)
        resultSlotBases = resultSlotBases.copyOf(newCapacity)
        returnIps = returnIps.copyOf(newCapacity)
    }

    private fun frame(index: Int) = ActivationFrame(
        arity = arities[index],
        handlerDepth = handlerDepths[index],
        valueDepth = valueDepths[index],
        instance = instances[index]!!,
        previousFramePointer = previousFramePointers[index],
        resultSlotBase = resultSlotBases[index],
        returnIp = returnIps[index],
    )

    private fun topIndex(): Int {
        if (top == 0) {
            throw InvocationException(InvocationError.MissingStackFrame)
        }
        return top - 1
    }

    private fun indexOfNth(n: Int): Int {
        val index = topIndex() - n
        if (index !in 0 until top) {
            throw InvocationException(InvocationError.MissingStackFrame)
        }
        return index
    }

    private fun remove(index: Int) {
        instances[index] = null
        top = index
    }
}

private const val INITIAL_CAPACITY = 32
private const val MAX_CAPACITY = 1028
