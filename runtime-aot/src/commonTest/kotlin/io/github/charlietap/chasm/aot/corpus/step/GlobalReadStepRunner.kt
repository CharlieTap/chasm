package io.github.charlietap.chasm.aot.corpus.step

import io.github.charlietap.chasm.aot.corpus.ext.string
import io.github.charlietap.chasm.aot.corpus.value.describeBits
import io.github.charlietap.chasm.aot.corpus.value.parseValue
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.corpus.lib.CorpusResult
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

internal typealias GlobalReadStepRunner = (StepContext, JsonObject) -> CorpusResult

internal fun GlobalReadStepRunner(
    context: StepContext,
    step: JsonObject,
): CorpusResult {
    val name = step.string("global")
    val global = context.setup.global(name) ?: return context.failure("global not found", name)
    val actual = when (val result = readGlobal(context.setup.store, global)) {
        is ChasmResult.Success -> result.result
        is ChasmResult.Error -> return context.failure("global read failed", result.error.toString())
    }
    val expected = parseValue(step.getValue("assert").jsonObject.getValue("value").jsonObject, context.captures)

    return if (expected.toLongFromBoxed() == actual.toLongFromBoxed()) {
        CorpusResult.Success
    } else {
        context.failure("global mismatch", expected = expected.describeBits(), actual = actual.describeBits())
    }
}
