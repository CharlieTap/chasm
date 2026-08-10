package io.github.charlietap.chasm.compiler.context

import io.github.charlietap.chasm.compiler.emptyIntArray
import io.github.charlietap.chasm.compiler.program.ProgramTarget
import io.github.charlietap.chasm.type.ValueType

internal class BlockContext(
    var kind: BlockKind,
    var baseHeight: Int,
    var branchSlots: IntArray,
    var resultSlots: IntArray,
    var parameterSlots: IntArray,
    var branchTypes: List<ValueType>,
    var resultTypes: List<ValueType>,
    var parameterTypes: List<ValueType>,
    var branchTarget: ProgramTarget,
    var continuationTarget: ProgramTarget,
    var handlerDepth: Int,
) {
    var inert = false
    var reachedByBranch = false
    var elseTarget = continuationTarget
    var entryFrameHeight = 0
    var inElse = false
    var thenReachable = false

    fun reset(
        kind: BlockKind,
        baseHeight: Int,
        branchSlots: IntArray,
        resultSlots: IntArray,
        parameterSlots: IntArray,
        branchTypes: List<ValueType>,
        resultTypes: List<ValueType>,
        parameterTypes: List<ValueType>,
        branchTarget: ProgramTarget,
        continuationTarget: ProgramTarget,
        handlerDepth: Int,
    ): BlockContext {
        this.kind = kind
        this.baseHeight = baseHeight
        this.branchSlots = branchSlots
        this.resultSlots = resultSlots
        this.parameterSlots = parameterSlots
        this.branchTypes = branchTypes
        this.resultTypes = resultTypes
        this.parameterTypes = parameterTypes
        this.branchTarget = branchTarget
        this.continuationTarget = continuationTarget
        this.handlerDepth = handlerDepth
        inert = false
        reachedByBranch = false
        elseTarget = continuationTarget
        entryFrameHeight = 0
        inElse = false
        thenReachable = false
        return this
    }

    fun resetInert(kind: BlockKind): BlockContext {
        reset(
            kind = kind,
            baseHeight = 0,
            branchSlots = emptyIntArray,
            resultSlots = emptyIntArray,
            parameterSlots = emptyIntArray,
            branchTypes = emptyList(),
            resultTypes = emptyList(),
            parameterTypes = emptyList(),
            branchTarget = inertProgramTarget,
            continuationTarget = inertProgramTarget,
            handlerDepth = 0,
        )
        inert = true
        return this
    }
}

internal class ControlStack(
    private val pool: ArrayList<BlockContext> = ArrayList(),
) : AbstractList<BlockContext>() {

    override var size: Int = 0
        private set

    override fun get(index: Int): BlockContext {
        checkElementIndex(index, size)
        return pool[index]
    }

    fun push(
        kind: BlockKind,
        baseHeight: Int,
        branchSlots: IntArray,
        resultSlots: IntArray,
        parameterSlots: IntArray,
        branchTypes: List<ValueType>,
        resultTypes: List<ValueType>,
        parameterTypes: List<ValueType>,
        branchTarget: ProgramTarget,
        continuationTarget: ProgramTarget,
        handlerDepth: Int,
    ): BlockContext {
        val block = if (size < pool.size) {
            pool[size].reset(
                kind = kind,
                baseHeight = baseHeight,
                branchSlots = branchSlots,
                resultSlots = resultSlots,
                parameterSlots = parameterSlots,
                branchTypes = branchTypes,
                resultTypes = resultTypes,
                parameterTypes = parameterTypes,
                branchTarget = branchTarget,
                continuationTarget = continuationTarget,
                handlerDepth = handlerDepth,
            )
        } else {
            BlockContext(
                kind = kind,
                baseHeight = baseHeight,
                branchSlots = branchSlots,
                resultSlots = resultSlots,
                parameterSlots = parameterSlots,
                branchTypes = branchTypes,
                resultTypes = resultTypes,
                parameterTypes = parameterTypes,
                branchTarget = branchTarget,
                continuationTarget = continuationTarget,
                handlerDepth = handlerDepth,
            ).also(pool::add)
        }
        size++
        return block
    }

    fun pushInert(kind: BlockKind): BlockContext {
        val block = if (size < pool.size) {
            pool[size].resetInert(kind)
        } else {
            BlockContext(
                kind = kind,
                baseHeight = 0,
                branchSlots = emptyIntArray,
                resultSlots = emptyIntArray,
                parameterSlots = emptyIntArray,
                branchTypes = emptyList(),
                resultTypes = emptyList(),
                parameterTypes = emptyList(),
                branchTarget = inertProgramTarget,
                continuationTarget = inertProgramTarget,
                handlerDepth = 0,
            ).also(pool::add).resetInert(kind)
        }
        size++
        return block
    }

    fun pop(): BlockContext {
        val index = size - 1
        val block = pool[index]
        size = index
        return block
    }
}

internal enum class BlockKind {
    Function,
    Block,
    Loop,
    If,
    TryTable,
}

private val inertProgramTarget = ProgramTarget(0)

private fun checkElementIndex(index: Int, size: Int) {
    if (index !in 0 until size) throw IndexOutOfBoundsException("index: $index, size: $size")
}
