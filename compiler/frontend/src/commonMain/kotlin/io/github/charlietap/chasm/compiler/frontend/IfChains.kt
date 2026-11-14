package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.ast.instruction.ControlInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.instruction.VariableInstruction
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.cir.AdminInstruction
import io.github.charlietap.chasm.cir.FusedOperand
import io.github.charlietap.chasm.cir.NumericCondition
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.context.InstructionCursor
import io.github.charlietap.chasm.compiler.frontend.program.ProgramBuilder
import io.github.charlietap.chasm.compiler.frontend.program.ProgramTarget
import io.github.charlietap.chasm.cir.NumericInstruction as CirNumericInstruction

internal class IfChains(
    private val instructions: InstructionCursor,
    private val tables: IfChainTables,
) {
    private var members = IntArray(INITIAL_MEMBERS)
    private var constants = IntArray(INITIAL_MEMBERS)
    private var elsesRecorded = false

    fun caseAt(ifIndex: Int): ProgramTarget? {
        return tables.caseAt(ifIndex)
    }

    fun defaultAt(ifIndex: Int): ProgramTarget? {
        return tables.defaultAt(ifIndex)
    }

    fun open(
        ifIndex: Int,
        selector: Int,
        program: ProgramBuilder,
    ): JumpTable? {
        if (tables.isRejected(ifIndex)) {
            return null
        }
        var count = 0
        var dense = 0
        var low = Int.MAX_VALUE
        var high = Int.MIN_VALUE
        var denseLow = 0
        var denseHigh = 0
        var settled = 1
        var position = ifIndex
        while (count < MAX_MEMBERS) {
            val test = chainConstant(position, selector)
            if (test == null) {
                settled = count
                break
            }
            val constant = test.value
            val duplicate = constants.indexOf(constant, count)
            if (duplicate >= 0) {
                settled = duplicate + 1
                break
            }
            if (count == members.size) {
                members = members.copyOf(count * 2)
                constants = constants.copyOf(count * 2)
            }
            members[count] = position
            constants[count] = constant
            count++
            low = minOf(low, constant)
            high = maxOf(high, constant)
            if (count >= MIN_MEMBERS && isDense(low, high, count)) {
                dense = count
                denseLow = low
                denseHigh = high
            }
            val otherwise = elseOf(position)
            if (otherwise < 0) {
                settled = count
                break
            }
            position = otherwise + 4
        }
        if (dense == 0) {
            for (start in 1 until settled) {
                if (!hasDensePrefix(start, count)) {
                    tables.reject(members[start])
                }
            }
            return null
        }

        val base = if (denseLow >= 0 && isDense(0, denseHigh, dense)) 0 else denseLow
        val default = program.target()
        val targets = IntArray(denseHigh - base + 2) { default.index }
        for (member in 0 until dense) {
            val case = program.target()
            tables.setCase(members[member], case)
            targets[constants[member] - base] = case.index
        }
        tables.setDefault(members[dense - 1], default)
        return JumpTable(base, targets)
    }

    private fun elseOf(ifIndex: Int): Int {

        if (!elsesRecorded) {
            recordElses()
            elsesRecorded = true
        }

        return tables.elseAt(ifIndex)
    }

    private fun recordElses() {

        var open = IntArray(INITIAL_DEPTH)
        var depth = 0
        for (position in 0 until instructions.size) {
            when (val instruction = instructions.at(position)) {
                is ControlInstruction.If,
                is ControlInstruction.Block,
                is ControlInstruction.Loop,
                is ControlInstruction.TryTable,
                -> {
                    if (depth == open.size) {
                        open = open.copyOf(depth * 2)
                    }
                    open[depth++] = position
                }
                is ControlInstruction.Else -> {
                    if (depth > 0) {
                        tables.setElse(open[depth - 1], position)
                    }
                }
                is ControlInstruction.End -> {
                    depth = maxOf(0, depth - instruction.count)
                }
                else -> Unit
            }
        }
    }

    private fun chainConstant(
        position: Int,
        selector: Int,
    ): NumericInstruction.I32Const? {
        val get = instructions.at(position - 3) as? VariableInstruction.LocalGet ?: return null
        if (get.localIdx.toInt() != selector) {
            return null
        }
        if (instructions.at(position - 1) != NumericInstruction.I32Eq || instructions.at(position) !is ControlInstruction.If) {
            return null
        }
        return instructions.at(position - 2) as? NumericInstruction.I32Const
    }

    private fun hasDensePrefix(
        start: Int,
        count: Int,
    ): Boolean {
        var low = Int.MAX_VALUE
        var high = Int.MIN_VALUE
        for (member in start until count) {
            low = minOf(low, constants[member])
            high = maxOf(high, constants[member])
            val size = member - start + 1
            if (size >= MIN_MEMBERS && isDense(low, high, size)) {
                return true
            }
        }
        return false
    }

    private fun isDense(
        low: Int,
        high: Int,
        count: Int,
    ): Boolean = (high.toLong() - low + 1) * DENSITY_DIVISOR <= count.toLong() * DENSITY_FACTOR
}

internal class JumpTable(
    val base: Int,
    val targets: IntArray,
)

internal class IfChainTables {

    private var generation = 0
    private var stamps = IntArray(0)
    private var elses = IntArray(0)
    private var rejected = BooleanArray(0)
    private var cases = arrayOfNulls<ProgramTarget>(0)
    private var defaults = arrayOfNulls<ProgramTarget>(0)

    fun begin(size: Int) {

        generation++
        if (size > stamps.size) {
            val capacity = maxOf(size, stamps.size * 2)
            stamps = IntArray(capacity)
            elses = IntArray(capacity)
            rejected = BooleanArray(capacity)
            cases = arrayOfNulls(capacity)
            defaults = arrayOfNulls(capacity)
        }
    }

    fun elseAt(position: Int): Int {
        prepare(position)
        return elses[position]
    }

    fun setElse(
        position: Int,
        elsePosition: Int,
    ) {
        prepare(position)
        elses[position] = elsePosition
    }

    fun isRejected(position: Int): Boolean {
        prepare(position)
        return rejected[position]
    }

    fun reject(position: Int) {
        prepare(position)
        rejected[position] = true
    }

    fun caseAt(position: Int): ProgramTarget? {
        prepare(position)
        return cases[position]
    }

    fun setCase(
        position: Int,
        target: ProgramTarget,
    ) {
        prepare(position)
        cases[position] = target
    }

    fun defaultAt(position: Int): ProgramTarget? {
        prepare(position)
        return defaults[position]
    }

    fun setDefault(
        position: Int,
        target: ProgramTarget,
    ) {
        prepare(position)
        defaults[position] = target
    }

    private fun prepare(position: Int) {
        if (stamps[position] != generation) {
            stamps[position] = generation
            elses[position] = NO_ELSE
            rejected[position] = false
            cases[position] = null
            defaults[position] = null
        }
    }
}

internal fun FunctionCompilationContext.ifChainCase(ifIndex: Int): ProgramTarget? {
    return ifChains?.caseAt(ifIndex)
}

internal fun FunctionCompilationContext.ifChainDefault(ifIndex: Int): ProgramTarget? {
    return ifChains?.defaultAt(ifIndex)
}

internal fun FunctionCompilationContext.beginIfChain(
    ifIndex: Int,
    condition: NumericCondition,
): ProgramTarget? {
    if (condition !is NumericCondition.I32Eq || condition.right !is FusedOperand.I32Const) {
        return null
    }
    val get = instructions.at(ifIndex - 3) as? VariableInstruction.LocalGet ?: return null
    val selector = get.localIdx.toInt()
    val slot = layout.localSlot(selector)
    if ((condition.left as? FusedOperand.FrameSlot)?.offset != slot) {
        return null
    }
    val chains = ifChains ?: IfChains(
        instructions = instructions,
        tables = workspace.ifChainTables.also { tables -> tables.begin(instructions.size) },
    ).also { ifChains = it }
    val table = chains.open(ifIndex, selector, program) ?: return null
    val operand = if (table.base == 0) {
        slot
    } else {
        val biased = frame.allocate()
        emit(CirNumericInstruction.I32AddSi(slot, -table.base, biased))
        frame.release(biased)
        biased
    }
    append(table.targets) { resolved -> AdminInstruction.JumpTableS(operand, resolved) }
    return chains.caseAt(ifIndex)
}

private fun IntArray.indexOf(
    value: Int,
    count: Int,
): Int {
    for (index in 0 until count) {
        if (this[index] == value) {
            return index
        }
    }
    return -1
}

private const val INITIAL_MEMBERS = 8

private const val INITIAL_DEPTH = 16

private const val NO_ELSE = -1

private const val MIN_MEMBERS = 4

private const val MAX_MEMBERS = 1_024

private const val DENSITY_FACTOR = 5

private const val DENSITY_DIVISOR = 2
