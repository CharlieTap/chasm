package io.github.charlietap.chasm.compiler.frontend

import io.github.charlietap.chasm.cir.ExceptionCatch
import io.github.charlietap.chasm.cir.ExceptionRegion
import io.github.charlietap.chasm.cir.ExceptionTable
import io.github.charlietap.chasm.cir.InstructionIndex
import io.github.charlietap.chasm.cir.TagIndex
import io.github.charlietap.chasm.compiler.frontend.context.FunctionCompilationContext
import io.github.charlietap.chasm.compiler.frontend.program.ProgramBuilder
import io.github.charlietap.chasm.compiler.frontend.program.ProgramTarget

internal class ExceptionTableBuilder {
    private val regions = ArrayList<OpenExceptionRegion>()
    private var currentRegion = -1
    private var tailCalls: ArrayList<Int>? = null

    fun enterRegion(startOffset: Int, controlDepth: Int, catches: Array<CatchTarget>) {
        val region = OpenExceptionRegion(startOffset, currentRegion, controlDepth, catches)
        currentRegion = regions.size
        regions.add(region)
    }

    fun exitRegion(endOffset: Int, controlDepth: Int) {
        if (currentRegion < 0) return
        val region = regions[currentRegion]
        // A try_table with no catches does not have an entry of its own.
        if (region.controlDepth != controlDepth) return
        check(endOffset >= region.startOffset)
        region.endOffset = endOffset
        currentRegion = region.parentRegion
    }

    fun excludeTailCall(offset: Int) {
        if (currentRegion < 0) return
        val offsets = tailCalls ?: ArrayList<Int>().also { tailCalls = it }
        offsets.add(offset)
    }

    fun finish(
        program: ProgramBuilder,
    ): ExceptionTable {
        check(currentRegion == -1) { "function ended with an open exception region" }
        return ExceptionTable(
            regions = List(regions.size) { index ->
                val region = regions[index]
                check(region.endOffset >= region.startOffset)
                ExceptionRegion(
                    start = InstructionIndex(region.startOffset),
                    end = InstructionIndex(region.endOffset),
                    parent = region.parentRegion.takeIf { it >= 0 },
                    catches = List(region.catches.size) { catchIndex ->
                        val handler = region.catches[catchIndex]
                        ExceptionCatch(
                            tag = handler.tag,
                            target = InstructionIndex(program.targetOffset(handler.target.index)),
                            payloadSlots = handler.payloadSlots,
                            exceptionReference = handler.includeExceptionReference,
                            stackSlots = handler.stackSlotCount,
                        )
                    },
                )
            },
            tailCalls = tailCalls?.toIntArray() ?: emptyIntArray,
        )
    }

    private class OpenExceptionRegion(
        val startOffset: Int,
        val parentRegion: Int,
        val controlDepth: Int,
        val catches: Array<CatchTarget>,
        var endOffset: Int = -1,
    )
}

internal class CatchTarget(
    val tag: TagIndex?,
    val target: ProgramTarget,
    val payloadSlots: IntArray,
    val includeExceptionReference: Boolean,
    val stackSlotCount: Int,
)

internal fun enterExceptionRegion(state: FunctionCompilationContext, catches: Array<CatchTarget>) {
    if (catches.isEmpty()) return
    state.flushCopies()
    val builder = state.exceptionTableBuilder ?: ExceptionTableBuilder().also { state.exceptionTableBuilder = it }
    builder.enterRegion(state.program.size, state.controls.size, catches)
}

internal fun exitExceptionRegion(state: FunctionCompilationContext) {
    val builder = state.exceptionTableBuilder ?: return
    state.flushCopies()
    builder.exitRegion(state.program.size, state.controls.size + 1)
}
