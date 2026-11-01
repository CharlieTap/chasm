package io.github.charlietap.chasm.integration

import com.goncalossilva.resources.Resource
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.chasm.runtime.value.ExecutionValue

interface TestRunner {
    fun invoke(
        name: String,
        arguments: List<ExecutionValue> = [],
    ): ChasmResult<List<ExecutionValue>, ChasmError>
}

class IntegrationTestRunner(
    private val engine: Engine,
    script: String,
    private val store: Store,
    imports: List<Import>,
    config: RuntimeConfig,
) : TestRunner {

    private val instance = engine.instantiate(
        Resource("$FILE_DIR$script.wasm").readBytes(),
        store,
        imports,
        config,
    )

    override fun invoke(
        name: String,
        arguments: List<ExecutionValue>,
    ): ChasmResult<List<ExecutionValue>, ChasmError> {
        return instance.flatMap { instance ->
            engine.invoke(store, instance, name, arguments)
        }
    }

    private companion object {
        const val FILE_DIR = "integration/"
    }
}
