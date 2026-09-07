package io.github.charlietap.chasm.runtime.program

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.exception.FunctionExceptionTable
import kotlin.jvm.JvmOverloads

class Program
    @JvmOverloads
    constructor(initialCapacity: Int = INITIAL_CAPACITY) {

        var instructions: Array<DispatchableInstruction> = dispatchableArray(initialCapacity)
            private set

        var size: Int = 0
            private set

        private var exceptionTables: ArrayList<FunctionExceptionTable>? = null

        val hasExceptionHandlers: Boolean
            get() = exceptionTables != null

        fun registerExceptionTable(table: FunctionExceptionTable) {
            require(table.entryIp >= 0 && table.instructionCount > 0 && table.endIp <= size)
            val tables = exceptionTables ?: ArrayList<FunctionExceptionTable>().also { exceptionTables = it }
            require(tables.isEmpty() || tables.last().endIp <= table.entryIp)
            tables.add(table)
        }

        fun exceptionTable(ip: Int): FunctionExceptionTable? {
            val tables = exceptionTables ?: return null
            var low = 0
            var high = tables.lastIndex
            while (low <= high) {
                val middle = (low + high) ushr 1
                val table = tables[middle]
                when {
                    ip < table.entryIp -> high = middle - 1
                    ip >= table.endIp -> low = middle + 1
                    else -> return table
                }
            }
            return null
        }

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
            exceptionTables?.let { tables ->
                while (tables.isNotEmpty() && tables.last().endIp > size) tables.removeAt(tables.lastIndex)
                if (tables.isEmpty()) exceptionTables = null
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

const val EXIT_IP = Int.MAX_VALUE

private const val INITIAL_CAPACITY = 256

private val unavailableInstruction = DispatchableInstruction { _, _, _ ->
    error("unavailable program instruction cannot be dispatched")
}

@Suppress("UNCHECKED_CAST")
private fun dispatchableArray(capacity: Int): Array<DispatchableInstruction> {
    require(capacity > 0) {
        "program capacity must be positive"
    }
    return arrayOfNulls<DispatchableInstruction>(capacity) as Array<DispatchableInstruction>
}
