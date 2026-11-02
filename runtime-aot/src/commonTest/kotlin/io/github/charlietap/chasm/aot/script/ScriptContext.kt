package io.github.charlietap.chasm.aot.script

import io.github.charlietap.chasm.aot.CompiledModule
import io.github.charlietap.chasm.aot.instance
import io.github.charlietap.chasm.config.Config
import io.github.charlietap.chasm.config.StoreConfig
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.embedding.shapes.flatMap
import io.github.charlietap.sweet.lib.SemanticPhase

data class ScriptContext(
    val config: Config,
    val binaryDirectory: String,
    val phaseSupport: SemanticPhase,
    val storeConfig: StoreConfig,
    val store: Store,
    val modules: MutableMap<String?, CompiledModule>,
    val instances: MutableMap<String?, Instance>,
    val imports: MutableList<Import> = mutableListOf(),
) {
    fun instance(
        name: String?,
    ): Instance = instances[name]!!

    fun registerImports(
        moduleName: String,
        instance: Instance,
    ) {
        imports += exports(instance).map { export -> Import(moduleName, export.name, export.value) }
    }

    fun compile(
        bytes: ByteArray,
    ): ChasmResult<CompiledModule, ChasmError> =
        ScriptModules.compile(bytes, config.moduleConfig, storeConfig)

    fun instantiate(
        module: CompiledModule,
    ): ChasmResult<Instance, ChasmError> =
        instance(store, module, imports, config.runtimeConfig)

    fun instantiate(
        bytes: ByteArray,
    ): ChasmResult<Instance, ChasmError> = compile(bytes).flatMap(::instantiate)
}
