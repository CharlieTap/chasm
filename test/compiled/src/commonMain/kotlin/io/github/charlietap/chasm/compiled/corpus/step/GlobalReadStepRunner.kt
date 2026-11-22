package io.github.charlietap.chasm.compiled.corpus.step

import io.github.charlietap.chasm.compiled.corpus.ext.string
import io.github.charlietap.chasm.compiled.corpus.value.describe
import io.github.charlietap.chasm.compiled.corpus.value.matches
import io.github.charlietap.chasm.compiled.corpus.value.numberValue
import io.github.charlietap.chasm.compiled.corpus.value.parseValue
import io.github.charlietap.chasm.compiled.corpus.value.referenceValue
import io.github.charlietap.chasm.embedding.global.readGlobal
import io.github.charlietap.chasm.embedding.global.readGlobalReference
import io.github.charlietap.chasm.type.ValueType
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
    val actual = when (val type = global.type.valueType) {
        is ValueType.Number -> numberValue(context.timings.execute { readGlobal(global) }, type)
        is ValueType.Reference -> referenceValue(context.timings.execute { readGlobalReference(global) }, type.referenceType.heapType)
        else -> return context.failure("unsupported global type", type.toString())
    }
    val expected = parseValue(step.getValue("assert").jsonObject.getValue("value").jsonObject, context.captures)

    return if (expected.matches(actual)) {
        CorpusResult.Success
    } else {
        context.failure("global mismatch", expected = expected.describe(), actual = actual.describe())
    }
}
