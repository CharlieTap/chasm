package io.github.charlietap.chasm.corpus.fixture

import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.corpus.instance.ModuleInstantiator
import io.github.charlietap.chasm.corpus.step.StepContext
import io.github.charlietap.chasm.corpus.step.StepRunner
import io.github.charlietap.chasm.embedding.shapes.Module
import io.github.charlietap.chasm.embedding.value.ExecutionValue
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.FixtureTest

internal typealias TestRunner = (FixtureContext, Module, IndexedValue<FixtureTest>?) -> CorpusResult

internal fun TestRunner(
    context: FixtureContext,
    module: Module,
    test: IndexedValue<FixtureTest>?,
): CorpusResult {
    return TestRunner(
        context = context,
        module = module,
        test = test,
        moduleInstantiator = ::ModuleInstantiator,
        stepRunner = ::StepRunner,
    )
}

private fun TestRunner(
    context: FixtureContext,
    module: Module,
    test: IndexedValue<FixtureTest>?,
    moduleInstantiator: ModuleInstantiator,
    stepRunner: StepRunner,
): CorpusResult {

    val timings = context.timings
    val setup = timings.instantiate {
        moduleInstantiator(context, context.engine.store(module, context.runtimeConfig), module, test?.value)
    }.getOrElse { error -> return error }
    timings.moduleBuildCount++

    try {
        if (test == null || context.phase == CorpusPhase.INSTANTIATION) {
            return CorpusResult.Success
        }

        val captures = mutableMapOf<String, ExecutionValue>()
        test.value.steps.forEachIndexed { index, step ->
            val result = stepRunner(StepContext(context.fixture, "test[${test.index}] step[$index]", setup, captures, timings), step)
            if (result != CorpusResult.Success) {
                return result
            }
        }

        return CorpusResult.Success
    } finally {
        setup.close()
    }
}
