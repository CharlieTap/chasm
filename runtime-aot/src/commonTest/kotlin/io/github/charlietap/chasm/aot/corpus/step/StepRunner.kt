package io.github.charlietap.chasm.aot.corpus.step

import io.github.charlietap.chasm.aot.corpus.ext.string
import io.github.charlietap.corpus.lib.CorpusResult
import kotlinx.serialization.json.JsonObject

internal typealias StepRunner = (StepContext, JsonObject) -> CorpusResult

internal fun StepRunner(
    context: StepContext,
    step: JsonObject,
): CorpusResult = StepRunner(
    context = context,
    step = step,
    functionInvokeStepRunner = ::FunctionInvokeStepRunner,
    memoryWriteStepRunner = ::MemoryWriteStepRunner,
    memoryReadStepRunner = ::MemoryReadStepRunner,
    memoryGrowStepRunner = ::MemoryGrowStepRunner,
    globalWriteStepRunner = ::GlobalWriteStepRunner,
    globalReadStepRunner = ::GlobalReadStepRunner,
)

private fun StepRunner(
    context: StepContext,
    step: JsonObject,
    functionInvokeStepRunner: FunctionInvokeStepRunner,
    memoryWriteStepRunner: MemoryWriteStepRunner,
    memoryReadStepRunner: MemoryReadStepRunner,
    memoryGrowStepRunner: MemoryGrowStepRunner,
    globalWriteStepRunner: GlobalWriteStepRunner,
    globalReadStepRunner: GlobalReadStepRunner,
): CorpusResult = when (val type = step.string("type")) {
    "function.invoke" -> functionInvokeStepRunner(context, step)
    "memory.write" -> memoryWriteStepRunner(context, step)
    "memory.read" -> memoryReadStepRunner(context, step)
    "memory.grow" -> memoryGrowStepRunner(context, step)
    "global.write" -> globalWriteStepRunner(context, step)
    "global.read" -> globalReadStepRunner(context, step)
    "table.write", "table.read" -> CorpusResult.Skipped(context.fixture.name, "$type steps are not supported yet")
    else -> context.failure("unsupported corpus step", type)
}
