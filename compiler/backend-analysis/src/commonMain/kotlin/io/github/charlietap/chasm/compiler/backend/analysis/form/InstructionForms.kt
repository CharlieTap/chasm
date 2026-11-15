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
    private val storage: InstructionFormStorage = InstructionFormStorage(),
) {
    private val instructions = function.instructions
    private val decoded = storage.decoded(instructions.size)
    private var numericForms: Array<NumericForm?>? = null
    private var memoryAccesses: Array<MemoryAccess?>? = null
    private var helperCalls: Array<HelperCall?>? = null
    private var callSites: Array<CallSite?>? = null

    val size: Int get() = instructions.size

    fun instruction(position: Int): Instruction {
        return instructions[position]
    }

    fun numeric(position: Int): NumericForm {
        val forms = numericForms ?: storage.numericForms(size).also { created -> numericForms = created }
        if (isFirstRequest(position, NUMERIC)) {
            forms[position] = (instructions[position] as NumericInstruction).form()
        }
        return checkNotNull(forms[position])
    }

    fun access(position: Int): MemoryAccess? {
        val accesses = memoryAccesses ?: storage.memoryAccesses(size).also { created -> memoryAccesses = created }
        if (isFirstRequest(position, ACCESS)) {
            accesses[position] = (instructions[position] as? MemoryInstruction)?.access()
        }
        return accesses[position]
    }

    fun helperCall(position: Int): HelperCall? {
        val calls = helperCalls ?: storage.helperCalls(size).also { created -> helperCalls = created }
        if (isFirstRequest(position, HELPER)) {
            calls[position] = instructions[position].helperCall()
        }
        return calls[position]
    }

    fun callSite(position: Int): CallSite? {
        val instruction = instructions[position] as? ControlInstruction ?: return null
        val sites = callSites ?: storage.callSites(size).also { created -> callSites = created }
        if (isFirstRequest(position, CALL)) {
            sites[position] = instruction.callSite(declarations)
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

class InstructionFormStorage {
    private var decoded = ByteArray(0)
    private var numericForms = arrayOfNulls<NumericForm>(0)
    private var memoryAccesses = arrayOfNulls<MemoryAccess>(0)
    private var helperCalls = arrayOfNulls<HelperCall>(0)
    private var callSites = arrayOfNulls<CallSite>(0)
    private var numericUsed = 0
    private var accessUsed = 0
    private var helperUsed = 0
    private var callUsed = 0

    internal fun decoded(size: Int): ByteArray {
        if (!fitsCapacity(decoded.size, size)) decoded = ByteArray(size)
        decoded.fill(0, 0, size)
        numericForms.fill(null, 0, numericUsed)
        memoryAccesses.fill(null, 0, accessUsed)
        helperCalls.fill(null, 0, helperUsed)
        callSites.fill(null, 0, callUsed)
        numericUsed = 0
        accessUsed = 0
        helperUsed = 0
        callUsed = 0
        return decoded
    }

    internal fun numericForms(size: Int): Array<NumericForm?> {
        if (!fitsCapacity(numericForms.size, size)) numericForms = arrayOfNulls(size)
        numericUsed = size
        return numericForms
    }

    internal fun memoryAccesses(size: Int): Array<MemoryAccess?> {
        if (!fitsCapacity(memoryAccesses.size, size)) memoryAccesses = arrayOfNulls(size)
        accessUsed = size
        return memoryAccesses
    }

    internal fun helperCalls(size: Int): Array<HelperCall?> {
        if (!fitsCapacity(helperCalls.size, size)) helperCalls = arrayOfNulls(size)
        helperUsed = size
        return helperCalls
    }

    internal fun callSites(size: Int): Array<CallSite?> {
        if (!fitsCapacity(callSites.size, size)) callSites = arrayOfNulls(size)
        callUsed = size
        return callSites
    }

    private fun fitsCapacity(
        capacity: Int,
        size: Int,
    ): Boolean {
        return capacity >= size && capacity <= maxOf(size * RETAINED_GROWTH, RETAINED_CAPACITY)
    }

    private companion object {
        const val RETAINED_GROWTH = 4
        const val RETAINED_CAPACITY = 16_384
    }
}

private const val NUMERIC = 1

private const val ACCESS = 2

private const val HELPER = 4

private const val CALL = 8
