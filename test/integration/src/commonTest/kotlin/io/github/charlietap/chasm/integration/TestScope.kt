package io.github.charlietap.chasm.integration

import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Export
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.store
import io.github.charlietap.chasm.runtime.value.ExecutionValue

interface TestScope {
    val engine: Engine
    val script: String
}

class IntegrationTestScope(
    override val engine: Engine,
    override val script: String,
) : TestScope

fun TestScope.runner(
    store: Store = store(),
    imports: List<Import> = [],
    config: RuntimeConfig = RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL),
    script: String = this.script,
): TestRunner {
    return IntegrationTestRunner(engine, script, store, imports, config)
}

fun TestScope.invoke(
    name: String,
    arguments: List<ExecutionValue> = [],
): ChasmResult<List<ExecutionValue>, ChasmError> {
    return runner().invoke(
        name = name,
        arguments = arguments,
    )
}

fun TestScope.exports(): ChasmResult<List<Export>, ChasmError> {
    return runner().exports()
}
