package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.GCStrategy
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ExecutionError
import io.github.charlietap.chasm.embedding.error.InstantiationError
import io.github.charlietap.chasm.embedding.instance
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Exports
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.value.ExecutionValue

interface TestScope {
    val engine: Engine
    val script: String
}

class IntegrationTestScope(
    override val engine: Engine,
    override val script: String,
) : TestScope

suspend fun TestScope.runner(
    store: Store = engine.store(),
    imports: List<Import> = [],
    config: RuntimeConfig = RuntimeConfig(gcStrategy = GCStrategy.TRADITIONAL),
    script: String = this.script,
): TestRunner {

    val bytes = Resource("$FILE_DIR$script.wasm").readBytes()
    val module = engine.module(bytes, store, config)

    return IntegrationTestRunner(store, instance(store, module, imports, config))
}

suspend fun TestScope.invoke(
    name: String,
    arguments: List<ExecutionValue> = [],
): ChasmResult<List<ExecutionValue>, ExecutionError> {
    return runner().invoke(
        name = name,
        arguments = arguments,
    )
}

suspend fun TestScope.exports(): ChasmResult<Exports, InstantiationError> {
    return runner().exports()
}

private const val FILE_DIR = "integration/"
