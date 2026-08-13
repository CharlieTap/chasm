package io.github.charlietap.chasm.compiler.program

import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.program.Program

internal class ProgramFragment(
    private val instructions: Array<DispatchableInstruction>,
    private val relocationInstructionIndices: IntArray,
    private val relocationTargetIps: IntArray,
    private val relocationFactories: Array<TargetInstructionFactory>,
    private val multiTargetRelocations: Array<ProgramBuilder.MultiTargetRelocation>,
) {

    private var linked = false

    fun appendTo(program: Program): Int {
        check(!linked) {
            "program fragment is already linked"
        }
        linked = true
        val baseIp = program.size
        if (baseIp != 0) relocate(baseIp)
        program.append(instructions)
        return baseIp
    }

    private fun relocate(baseIp: Int) {
        for (index in relocationFactories.indices) {
            val instructionIndex = relocationInstructionIndices[index]
            val targetIp = baseIp + relocationTargetIps[index]
            instructions[instructionIndex] = relocationFactories[index].create(targetIp)
        }
        for (index in multiTargetRelocations.indices) {
            val relocation = multiTargetRelocations[index]
            val targetIps = relocation.targetIps
            for (targetIndex in targetIps.indices) {
                targetIps[targetIndex] += baseIp
            }
            instructions[relocation.instructionIndex] = relocation.instruction(targetIps)
        }
    }
}
