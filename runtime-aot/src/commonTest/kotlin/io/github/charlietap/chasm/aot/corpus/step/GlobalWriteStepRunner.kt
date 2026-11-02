package io.github.charlietap.chasm.aot.corpus.step

import io.github.charlietap.chasm.aot.corpus.ext.string
import io.github.charlietap.chasm.aot.corpus.value.parseValue
import io.github.charlietap.chasm.embedding.global.writeGlobal
import io.github.charlietap.chasm.embedding.shapes.fold
import io.github.charlietap.corpus.lib.CorpusResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

internal typealias GlobalWriteStepRunner = (StepContext, JsonObject) -> CorpusResult

internal fun GlobalWriteStepRunner(
    context: StepContext,
    step: JsonObject,
): CorpusResult {
    val name = step.string("global")
    val global = context.setup.global(name) ?: return context.failure("global not found", name)
    val value = parseValue(step.getValue("value").jsonObject, context.captures)

    return writeGlobal(context.setup.store, global, value).fold(
        { CorpusResult.Success },
        { error -> context.failure("global write failed", error.toString()) },
    )
}
