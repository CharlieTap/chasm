package io.github.charlietap.chasm.script

import io.github.charlietap.chasm.executor.runtime.instance.ExportInstance
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store
import io.github.charlietap.chasm.import.Import

data class ScriptContext(
    val binaryDirectory: String,
    val store: Store,
    val instances: MutableMap<String?, ModuleInstance>,
    val imports: MutableList<Import> = mutableListOf(),
) {

    fun instance(name: String?): ModuleInstance = instances[name]!!

    fun registerImports(moduleName: String, exports: List<ExportInstance>) {

        val imports = exports.map { export ->
            Import(
                moduleName,
                export.name.name,
                export.value,
            )
        }

        this.imports += imports
    }
}
