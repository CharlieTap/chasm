package io.github.charlietap.chasm.aot.corpus.fixture

import com.github.michaelbull.result.getOrElse
import io.github.charlietap.chasm.aot.corpus.instance.ModuleInstantiator
import io.github.charlietap.chasm.aot.corpus.step.StepContext
import io.github.charlietap.chasm.aot.corpus.step.StepRunner
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.ExecutionValue
import io.github.charlietap.corpus.lib.CorpusPhase
import io.github.charlietap.corpus.lib.CorpusResult
import io.github.charlietap.corpus.lib.fixture.FixtureTest

internal typealias TestRunner = (FixtureContext, PreparedModule, IndexedValue<FixtureTest>?) -> CorpusResult

internal fun TestRunner(
    context: FixtureContext,
    module: PreparedModule,
    test: IndexedValue<FixtureTest>?,
): CorpusResult = TestRunner(
    context = context,
    module = module,
    test = test,
    moduleInstantiator = ::ModuleInstantiator,
    stepRunner = ::StepRunner,
)

private fun TestRunner(
    context: FixtureContext,
    module: PreparedModule,
    test: IndexedValue<FixtureTest>?,
    moduleInstantiator: ModuleInstantiator,
    stepRunner: StepRunner,
): CorpusResult {
    val timings = context.timings
    val setup = timings.instantiate {
        moduleInstantiator(context, store(), module, test?.value)
    }.getOrElse { error -> return error }
    timings.moduleBuildCount++

    try {
        if (test == null || context.phase == CorpusPhase.INSTANTIATION) return CorpusResult.Success

        val captures = mutableMapOf<String, ExecutionValue>()
        test.value.steps.forEachIndexed { index, step ->
            val result = timings.execute {
                stepRunner(StepContext(context.fixture, "test[${test.index}] step[$index]", setup, captures), step)
            }
            if (result != CorpusResult.Success) return result
        }
        return CorpusResult.Success
    } finally {
        setup.close()
    }
}
