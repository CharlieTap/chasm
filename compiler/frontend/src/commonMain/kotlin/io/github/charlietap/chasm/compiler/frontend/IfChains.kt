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
    private val elses: IntArray,
    private val rejected: BooleanArray,
    private val cases: Array<ProgramTarget?>,
    private val defaults: Array<ProgramTarget?>,
) {
    private var members = IntArray(INITIAL_MEMBERS)
    private var constants = IntArray(INITIAL_MEMBERS)

    init {
        elses.fill(-1, 0, instructions.size)
        rejected.fill(false, 0, instructions.size)
        cases.fill(null, 0, instructions.size)
        defaults.fill(null, 0, instructions.size)
        var open = IntArray(16)
        var depth = 0
        for (position in 0 until instructions.size) {
            when (val instruction = instructions.at(position)) {
                is ControlInstruction.If,
                is ControlInstruction.Block,
                is ControlInstruction.Loop,
                is ControlInstruction.TryTable,
                -> {
                    if (depth == open.size) open = open.copyOf(depth * 2)
                    open[depth++] = position
                }
                is ControlInstruction.Else -> if (depth > 0) elses[open[depth - 1]] = position
                is ControlInstruction.End -> depth = maxOf(0, depth - instruction.count)
                else -> Unit
            }
        }
    }

    fun caseAt(
        ifIndex: Int,
    ): ProgramTarget? = cases[ifIndex]

    fun defaultAt(
        ifIndex: Int,
    ): ProgramTarget? = defaults[ifIndex]

    fun open(
        ifIndex: Int,
        selector: Int,
        program: ProgramBuilder,
    ): JumpTable? {
        if (rejected[ifIndex]) return null
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
            val otherwise = elses[position]
            if (otherwise < 0) {
                settled = count
                break
            }
            position = otherwise + 4
        }
        if (dense == 0) {
            for (start in 1 until settled) {
                if (!hasDensePrefix(start, count)) rejected[members[start]] = true
            }
            return null
        }

        val base = if (denseLow >= 0 && isDense(0, denseHigh, dense)) 0 else denseLow
        val default = program.target()
        val targets = IntArray(denseHigh - base + 2) { default.index }
        for (member in 0 until dense) {
            val case = program.target()
            cases[members[member]] = case
            targets[constants[member] - base] = case.index
        }
        defaults[members[dense - 1]] = default
        return JumpTable(base, targets)
    }

    private fun chainConstant(
        position: Int,
        selector: Int,
    ): NumericInstruction.I32Const? {
        val get = instructions.at(position - 3) as? VariableInstruction.LocalGet ?: return null
        if (get.localIdx.toInt() != selector) return null
        if (instructions.at(position - 1) != NumericInstruction.I32Eq || instructions.at(position) !is ControlInstruction.If) return null
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
            if (size >= MIN_MEMBERS && isDense(low, high, size)) return true
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

internal fun FunctionCompilationContext.ifChainCase(
    ifIndex: Int,
): ProgramTarget? = ifChains?.caseAt(ifIndex)

internal fun FunctionCompilationContext.ifChainDefault(
    ifIndex: Int,
): ProgramTarget? = ifChains?.defaultAt(ifIndex)

internal fun FunctionCompilationContext.beginIfChain(
    ifIndex: Int,
    condition: NumericCondition,
): ProgramTarget? {
    if (condition !is NumericCondition.I32Eq || condition.right !is FusedOperand.I32Const) return null
    val get = instructions.at(ifIndex - 3) as? VariableInstruction.LocalGet ?: return null
    val selector = get.localIdx.toInt()
    val slot = layout.localSlot(selector)
    if ((condition.left as? FusedOperand.FrameSlot)?.offset != slot) return null
    val chains = ifChains ?: IfChains(
        instructions = instructions,
        elses = workspace.chainElses(instructions.size),
        rejected = workspace.rejectedChains(instructions.size),
        cases = workspace.chainCases(instructions.size),
        defaults = workspace.chainDefaults(instructions.size),
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
        if (this[index] == value) return index
    }
    return -1
}

private const val INITIAL_MEMBERS = 8

private const val MIN_MEMBERS = 4

private const val MAX_MEMBERS = 1_024

private const val DENSITY_FACTOR = 5

private const val DENSITY_DIVISOR = 2
