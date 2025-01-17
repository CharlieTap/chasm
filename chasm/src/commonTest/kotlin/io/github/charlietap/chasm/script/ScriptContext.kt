package io.github.charlietap.chasm.script

import io.github.charlietap.chasm.config.Config
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Instance
import io.github.charlietap.chasm.embedding.shapes.Store

data class ScriptContext(
    val config: Config,
    val binaryDirectory: String,
    val store: Store,
    val instances: MutableMap<String?, Instance>,
    val imports: MutableList<Import> = mutableListOf(),
) {

    fun instance(name: String?): Instance = instances[name]!!

    fun registerImports(moduleName: String, instance: Instance) {

        val exports = exports(instance)
        val imports = exports.map { export ->
            Import(
                moduleName,
                export.name,
                export.value,
            )
        }

        this.imports += imports
    }
}
