package io.github.charlietap.chasm.compiler.backend.analysis.form

import io.github.charlietap.chasm.cir.CirFunction
import io.github.charlietap.chasm.cir.ControlInstruction
import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.cir.MemoryInstruction
import io.github.charlietap.chasm.cir.NumericInstruction
import io.github.charlietap.chasm.compiler.backend.analysis.ModuleIndexSpaces

class InstructionForms(
    val function: CirFunction,
    val declarations: ModuleIndexSpaces,
) {
    private val instructions = function.instructions
    private val decoded = ByteArray(instructions.size)
    private var numericForms: Array<NumericForm?>? = null
    private var memoryAccesses: Array<MemoryAccess?>? = null
    private var helperCalls: Array<HelperCall?>? = null
    private var callSites: Array<CallSite?>? = null

    val size: Int get() = instructions.size

    fun instruction(position: Int): Instruction {
        return instructions[position]
    }

    fun numeric(position: Int): NumericForm {
        val forms = numericForms ?: arrayOfNulls<NumericForm>(size).also { created -> numericForms = created }
        if (isFirstRequest(position, NUMERIC)) {
            forms[position] = (instructions[position] as NumericInstruction).form()
        }
        return checkNotNull(forms[position])
    }

    fun access(position: Int): MemoryAccess? {
        val accesses = memoryAccesses ?: arrayOfNulls<MemoryAccess>(size).also { created -> memoryAccesses = created }
        if (isFirstRequest(position, ACCESS)) {
            accesses[position] = (instructions[position] as? MemoryInstruction)?.access()
        }
        return accesses[position]
    }

    fun helperCall(position: Int): HelperCall? {
        val calls = helperCalls ?: arrayOfNulls<HelperCall>(size).also { created -> helperCalls = created }
        if (isFirstRequest(position, HELPER)) {
            calls[position] = instructions[position].helperCall()
        }
        return calls[position]
    }

    fun callSite(position: Int): CallSite? {
        val sites = callSites ?: arrayOfNulls<CallSite>(size).also { created -> callSites = created }
        if (isFirstRequest(position, CALL)) {
            sites[position] = (instructions[position] as? ControlInstruction)?.callSite(declarations)
        }
        return sites[position]
    }

    private fun isFirstRequest(
        position: Int,
        form: Int,
    ): Boolean {
        val flags = decoded[position].toInt()
        if (flags and form != 0) {
            return false
        }
        decoded[position] = (flags or form).toByte()
        return true
    }
}

private const val NUMERIC = 1

private const val ACCESS = 2

private const val HELPER = 4

private const val CALL = 8
