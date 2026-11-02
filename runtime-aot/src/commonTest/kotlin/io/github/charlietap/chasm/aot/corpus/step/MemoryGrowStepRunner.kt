package io.github.charlietap.chasm.aot.corpus.step

import io.github.charlietap.chasm.aot.corpus.ext.stringOrDefault
import io.github.charlietap.chasm.aot.corpus.value.describeBits
import io.github.charlietap.chasm.aot.corpus.value.parseIndex
import io.github.charlietap.chasm.aot.corpus.value.parseValue
import io.github.charlietap.chasm.embedding.memory.growMemory
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.value.NumberValue
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
    val actual = when (val result = growMemory(context.setup.store, memory, pages)) {
        is ChasmResult.Success -> NumberValue.I32(result.result)
        is ChasmResult.Error -> return context.failure("memory grow failed", result.error.toString())
    }

    step["capture"]?.jsonPrimitive?.contentOrNull?.let { capture ->
        context.captures[capture] = actual
    }

    val assertion = step["assert"]?.jsonObject ?: return CorpusResult.Success
    val expected = assertion["value"]?.jsonObject?.let { value -> parseValue(value, context.captures) }
        ?: return context.failure("unsupported memory grow assertion")

    return if (expected.toLongFromBoxed() == actual.toLongFromBoxed()) {
        CorpusResult.Success
    } else {
        context.failure("memory grow mismatch", expected = expected.describeBits(), actual = actual.describeBits())
    }
}
