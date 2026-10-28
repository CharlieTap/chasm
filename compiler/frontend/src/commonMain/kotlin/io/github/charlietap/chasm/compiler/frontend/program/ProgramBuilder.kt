package io.github.charlietap.chasm.compiler.frontend.program

import io.github.charlietap.chasm.cir.Instruction
import io.github.charlietap.chasm.compiler.frontend.emptyIntArray

internal class ProgramBuilder(
    capacity: Int = INITIAL_CAPACITY,
) {

    private var instructions = arrayOfNulls<Instruction>(maxOf(capacity, 1))
    private var count = 0

    private var targetPositions = IntArray(INITIAL_TARGET_CAPACITY)
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
    private var checkpoints = emptyIntArray
    private var checkpointCount = 0

    internal var provenance: SourceProvenanceRecorder? = null

    val size: Int
        get() = count

    fun reset() {
        count = 0
        targetCount = 0
        patchFactories.clear()
        multiTargetPatches = null
        multiTargetPatchDependencyCount = 0
        unboundTargetCount = 0
        unresolvedInstructionCount = 0
        finished = false
        checkpointCount = 0
        provenance = null
    }

    fun target(): ProgramTarget {
        checkNotFinished()
        if (targetCount == targetPositions.size) {
            targetPositions = targetPositions.copyOf(targetPositions.size * 2)
            firstPatchByTarget = firstPatchByTarget.copyOf(firstPatchByTarget.size * 2)
            if (firstMultiTargetPatchByTarget.isNotEmpty()) {
                firstMultiTargetPatchByTarget = firstMultiTargetPatchByTarget.copyOf(targetPositions.size)
            }
        }
        val target = ProgramTarget(targetCount++)
        targetPositions[target.index] = UNBOUND
        firstPatchByTarget[target.index] = 0
        if (firstMultiTargetPatchByTarget.isNotEmpty()) firstMultiTargetPatchByTarget[target.index] = 0
        unboundTargetCount++
        return target
    }

    fun bind(target: ProgramTarget) {
        checkNotFinished()
        checkTarget(target)
        check(targetPositions[target.index] == UNBOUND) {
            "program target is already bound"
        }
        targetPositions[target.index] = count
        resolvePatches(target)
        unboundTargetCount--
    }

    fun append(
        instruction: Instruction,
    ): Int {
        checkNotFinished()
        val index = count
        add(instruction)
        provenance?.record(index)
        return index
    }

    fun append(
        target: ProgramTarget,
        instruction: TargetInstructionFactory,
    ): Int {
        checkNotFinished()
        checkTarget(target)
        val index = count
        provenance?.record(index)
        val targetPosition = targetPositions[target.index]
        if (targetPosition != UNBOUND) {
            add(instruction.create(targetPosition))
            return index
        }
        add(null)
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
        instruction: (IntArray) -> Instruction,
    ): Int {
        checkNotFinished()
        val index = count
        provenance?.record(index)
        var remainingTargetCount = 0
        for (targetIndex in targetIndices) {
            checkTargetIndex(targetIndex)
            if (targetPositions[targetIndex] == UNBOUND) remainingTargetCount++
        }
        if (remainingTargetCount == 0) {
            add(instruction(resolveTargetPositionsInPlace(targetIndices)))
            return index
        }
        add(null)
        unresolvedInstructionCount++
        val patches = multiTargetPatches ?: mutableListOf<MultiTargetPatch>().also { multiTargetPatches = it }
        if (firstMultiTargetPatchByTarget.isEmpty()) {
            firstMultiTargetPatchByTarget = IntArray(targetPositions.size)
        }
        val patchIndex = patches.size
        patches.add(MultiTargetPatch(index, targetIndices, instruction, remainingTargetCount))
        for (targetIndex in targetIndices) {
            if (targetPositions[targetIndex] != UNBOUND) continue
            addMultiTargetPatchDependency(targetIndex, patchIndex)
        }
        return index
    }

    fun markCheckpoint() {
        if (checkpointCount == checkpoints.size) {
            checkpoints = checkpoints.copyOf(maxOf(INITIAL_PATCH_CAPACITY, checkpoints.size * 2))
        }
        checkpoints[checkpointCount++] = count - 1
    }

    fun checkpoints(): IntArray = if (checkpointCount == 0) emptyIntArray else checkpoints.copyOf(checkpointCount)

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

    @Suppress("UNCHECKED_CAST")
    fun instructions(): Array<Instruction> {
        check(finished)
        return instructions.copyOf(count) as Array<Instruction>
    }

    fun targetOffset(targetIndex: Int): Int {
        check(finished)
        checkTargetIndex(targetIndex)
        return targetPositions[targetIndex]
    }

    private fun add(
        instruction: Instruction?,
    ) {
        if (count == instructions.size) {
            instructions = instructions.copyOf(instructions.size * 2)
        }
        instructions[count++] = instruction
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
        val targetPosition = targetPositions[target.index]
        var patch = firstPatchByTarget[target.index]
        firstPatchByTarget[target.index] = 0
        while (patch != 0) {
            val patchIndex = patch - 1
            val factory = checkNotNull(patchFactories[patchIndex])
            instructions[patchInstructionIndices[patchIndex]] = factory.create(targetPosition)
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
                instructions[patch.instructionIndex] = patch.instruction(resolveTargetPositionsInPlace(patch.targetIndices))
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

    private fun resolveTargetPositionsInPlace(
        targetIndices: IntArray,
    ): IntArray {
        for (index in targetIndices.indices) {
            targetIndices[index] = targetPositions[targetIndices[index]]
        }
        return targetIndices
    }

    private class MultiTargetPatch(
        val instructionIndex: Int,
        val targetIndices: IntArray,
        val instruction: (IntArray) -> Instruction,
        var remainingTargetCount: Int,
    )

    private companion object {
        const val INITIAL_CAPACITY = 64
        const val INITIAL_PATCH_CAPACITY = 4
        const val INITIAL_TARGET_CAPACITY = 8
        const val UNBOUND = Int.MIN_VALUE
    }
}

internal fun interface TargetInstructionFactory {
    fun create(
        targetPosition: Int,
    ): Instruction
}
