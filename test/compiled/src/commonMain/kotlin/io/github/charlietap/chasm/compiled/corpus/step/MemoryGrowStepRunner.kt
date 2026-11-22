package io.github.charlietap.chasm.compiled.corpus.step

import io.github.charlietap.chasm.compiled.corpus.ext.stringOrDefault
import io.github.charlietap.chasm.compiled.corpus.value.describe
import io.github.charlietap.chasm.compiled.corpus.value.matches
import io.github.charlietap.chasm.compiled.corpus.value.parseIndex
import io.github.charlietap.chasm.compiled.corpus.value.parseValue
import io.github.charlietap.chasm.embedding.memory.growMemory
import io.github.charlietap.chasm.embedding.value.NumberValue
import io.github.charlietap.corpus.lib.CorpusResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

internal typealias MemoryGrowStepRunner = (StepContext, JsonObject) -> CorpusResult

internal fun MemoryGrowStepRunner(
    context: StepContext,
    step: JsonObject,
): CorpusResult {
    val memory = context.setup.memory(step.stringOrDefault("memory", "memory"))
        ?: return context.failure("memory not found", step["memory"]?.toString())
    val pages = parseIndex(step.getValue("pages"), context.captures)
    val actual = NumberValue.I32(context.timings.execute { growMemory(memory, pages) })

    step["capture"]?.jsonPrimitive?.contentOrNull?.let { capture ->
        context.captures[capture] = actual
    }

    val assertion = step["assert"]?.jsonObject ?: return CorpusResult.Success
    val expected = assertion["value"]?.jsonObject?.let { value -> parseValue(value, context.captures) }
        ?: return context.failure("unsupported memory grow assertion")

    return if (expected.matches(actual)) {
        CorpusResult.Success
    } else {
        context.failure("memory grow mismatch", expected = expected.describe(), actual = actual.describe())
    }
}
