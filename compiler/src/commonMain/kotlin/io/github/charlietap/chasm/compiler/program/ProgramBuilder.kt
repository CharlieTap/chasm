package io.github.charlietap.chasm.compiler.program

import io.github.charlietap.chasm.compiler.emptyIntArray
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.program.Program

internal class ProgramBuilder(
    private val program: Program,
) {

    private var targetIps = IntArray(INITIAL_TARGET_CAPACITY)
    private var firstPatchByTarget = IntArray(INITIAL_TARGET_CAPACITY)
    private var firstMultiTargetPatchByTarget = emptyIntArray
    private var targetCount = 0
    private var patchInstructionIndices = emptyIntArray
    private var nextPatchIndices = emptyIntArray
    private val patchFactories = ArrayList<TargetInstructionFactory?>()
    private var multiTargetPatches: MutableList<MultiTargetPatch>? = null
    private var multiTargetPatchDependencies = emptyIntArray
    private var nextMultiTargetPatchDependencies = emptyIntArray
    private var multiTargetPatchDependencyCount = 0
    private var unboundTargetCount = 0
    private var unresolvedInstructionCount = 0
    private var finished = false

    val baseIp: Int = program.size

    val size: Int
        get() = program.size - baseIp

    val nextIp: Int
        get() = program.size

    fun target(): ProgramTarget {
        checkNotFinished()
        if (targetCount == targetIps.size) {
            targetIps = targetIps.copyOf(targetIps.size * 2)
            firstPatchByTarget = firstPatchByTarget.copyOf(firstPatchByTarget.size * 2)
            if (firstMultiTargetPatchByTarget.isNotEmpty()) {
                firstMultiTargetPatchByTarget = firstMultiTargetPatchByTarget.copyOf(targetIps.size)
            }
        }
        val target = ProgramTarget(targetCount++)
        targetIps[target.index] = UNBOUND_IP
        unboundTargetCount++
        return target
    }

    fun bind(target: ProgramTarget) {
        checkNotFinished()
        checkTarget(target)
        check(targetIps[target.index] == UNBOUND_IP) {
            "program target is already bound"
        }
        targetIps[target.index] = nextIp
        resolvePatches(target)
        unboundTargetCount--
    }

    fun append(instruction: DispatchableInstruction): Int {
        checkNotFinished()
        return program.append(instruction)
    }

    fun append(
        target: ProgramTarget,
        instruction: TargetInstructionFactory,
    ): Int {
        checkNotFinished()
        val index = program.size
        checkTarget(target)
        val targetIp = targetIps[target.index]
        if (targetIp != UNBOUND_IP) {
            program.append(instruction.create(targetIp))
            return index
        }
        program.append(unresolvedInstruction)
        unresolvedInstructionCount++
        val patchIndex = patchFactories.size
        if (patchIndex == patchInstructionIndices.size) {
            val capacity = maxOf(INITIAL_PATCH_CAPACITY, patchInstructionIndices.size * 2)
            patchInstructionIndices = patchInstructionIndices.copyOf(capacity)
            nextPatchIndices = nextPatchIndices.copyOf(capacity)
        }
        patchInstructionIndices[patchIndex] = index
        nextPatchIndices[patchIndex] = firstPatchByTarget[target.index]
        firstPatchByTarget[target.index] = patchIndex + 1
        patchFactories.add(instruction)
        return index
    }

    fun append(
        targetIndices: IntArray,
        instruction: (IntArray) -> DispatchableInstruction,
    ): Int {
        checkNotFinished()
        val index = program.size
        var remainingTargetCount = 0
        for (targetIndex in targetIndices) {
            checkTargetIndex(targetIndex)
            if (targetIps[targetIndex] == UNBOUND_IP) remainingTargetCount++
        }
        if (remainingTargetCount == 0) {
            program.append(instruction(resolveTargetIpsInPlace(targetIndices)))
            return index
        }
        program.append(unresolvedInstruction)
        unresolvedInstructionCount++
        val patches = multiTargetPatches ?: mutableListOf<MultiTargetPatch>().also { multiTargetPatches = it }
        if (firstMultiTargetPatchByTarget.isEmpty()) {
            firstMultiTargetPatchByTarget = IntArray(targetIps.size)
        }
        val patchIndex = patches.size
        patches.add(MultiTargetPatch(index, targetIndices, instruction, remainingTargetCount))
        for (targetIndex in targetIndices) {
            if (targetIps[targetIndex] != UNBOUND_IP) continue
            addMultiTargetPatchDependency(targetIndex, patchIndex)
        }

        return index
    }

    fun finish() {
        checkNotFinished()
        check(unboundTargetCount == 0) {
            "program contains an unbound target"
        }
        check(unresolvedInstructionCount == 0) {
            "program contains an unresolved instruction"
        }
        multiTargetPatches = null
        finished = true
    }

    private fun checkNotFinished() {
        check(!finished) {
            "program is already finished"
        }
    }

    private fun checkTarget(target: ProgramTarget) {
        checkTargetIndex(target.index)
    }

    private fun checkTargetIndex(targetIndex: Int) {
        check(targetIndex in 0 until targetCount) {
            "program target index is out of bounds"
        }
    }

    private fun resolvePatches(target: ProgramTarget) {
        val targetIp = targetIps[target.index]
        var patch = firstPatchByTarget[target.index]
        firstPatchByTarget[target.index] = 0
        while (patch != 0) {
            val patchIndex = patch - 1
            val factory = checkNotNull(patchFactories[patchIndex])
            program.replace(patchInstructionIndices[patchIndex], factory.create(targetIp))
            unresolvedInstructionCount--
            patchFactories[patchIndex] = null
            patch = nextPatchIndices[patchIndex]
        }

        var dependency = if (firstMultiTargetPatchByTarget.isEmpty()) {
            0
        } else {
            firstMultiTargetPatchByTarget[target.index].also { firstMultiTargetPatchByTarget[target.index] = 0 }
        }
        while (dependency != 0) {
            val dependencyIndex = dependency - 1
            val patchIndex = multiTargetPatchDependencies[dependencyIndex]
            val patch = checkNotNull(multiTargetPatches)[patchIndex]
            patch.remainingTargetCount--
            if (patch.remainingTargetCount == 0) {
                program.replace(patch.instructionIndex, patch.instruction(resolveTargetIpsInPlace(patch.targetIndices)))
                unresolvedInstructionCount--
            }
            dependency = nextMultiTargetPatchDependencies[dependencyIndex]
        }
    }

    private fun addMultiTargetPatchDependency(targetIndex: Int, patchIndex: Int) {
        if (multiTargetPatchDependencyCount == multiTargetPatchDependencies.size) {
            val capacity = maxOf(INITIAL_PATCH_CAPACITY, multiTargetPatchDependencies.size * 2)
            multiTargetPatchDependencies = multiTargetPatchDependencies.copyOf(capacity)
            nextMultiTargetPatchDependencies = nextMultiTargetPatchDependencies.copyOf(capacity)
        }
        multiTargetPatchDependencies[multiTargetPatchDependencyCount] = patchIndex
        nextMultiTargetPatchDependencies[multiTargetPatchDependencyCount] = firstMultiTargetPatchByTarget[targetIndex]
        firstMultiTargetPatchByTarget[targetIndex] = multiTargetPatchDependencyCount + 1
        multiTargetPatchDependencyCount++
    }

    private fun resolveTargetIpsInPlace(targetIndices: IntArray): IntArray {
        for (index in targetIndices.indices) {
            targetIndices[index] = targetIps[targetIndices[index]]
        }
        return targetIndices
    }

    private class MultiTargetPatch(
        val instructionIndex: Int,
        val targetIndices: IntArray,
        val instruction: (IntArray) -> DispatchableInstruction,
        var remainingTargetCount: Int,
    )

    private companion object {
        const val INITIAL_PATCH_CAPACITY = 4
        const val INITIAL_TARGET_CAPACITY = 8
        const val UNBOUND_IP = Int.MIN_VALUE
    }
}

internal fun interface TargetInstructionFactory {
    fun create(targetIp: Int): DispatchableInstruction
}

private val unresolvedInstruction = DispatchableInstruction { _, _, _, _, _ ->
    error("unresolved instruction cannot be dispatched")
}
