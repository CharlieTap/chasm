package io.github.charlietap.chasm.script

import io.github.charlietap.chasm.config.Config
import io.github.charlietap.chasm.embedding.exports
import io.github.charlietap.chasm.embedding.shapes.Component
import io.github.charlietap.chasm.embedding.shapes.ComponentExportInstance
import io.github.charlietap.chasm.embedding.shapes.ComponentImport
import io.github.charlietap.chasm.embedding.shapes.Import
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.sweet.lib.SemanticPhase

data class ScriptContext(
    val config: Config,
    val binaryDirectory: String,
    val phaseSupport: SemanticPhase,
    val store: Store,
    val modules: MutableMap<String?, ScriptModule>,
    val instances: MutableMap<String?, ScriptInstance>,
    val imports: MutableList<Import> = [],
) {

    private val componentSpectestImports by lazy { ComponentSpectestImports(store) }

    fun instance(name: String?): ScriptInstance = instances[name]!!

    fun componentImports(component: Component): List<ComponentImport> {
        val names = component.component.definitions
            .filterIsInstance<io.github.charlietap.chasm.ast.component.Import>()
            .mapTo(mutableSetOf()) { definition -> definition.name.name.name }
        val linked = instances.mapNotNull { (name, instance) ->
            val componentInstance = instance as? ScriptInstance.ComponentModel ?: return@mapNotNull null
            name?.takeIf(names::contains)?.let {
                ComponentImport(it, ComponentExportInstance(componentInstance.instance.exports))
            }
        }
        return linked + componentSpectestImports.filter { import -> import.name in names }
    }

    fun registerImports(moduleName: String, instance: ScriptInstance): Boolean {
        val core = instance as? ScriptInstance.Core ?: return false

        val exports = exports(core.instance)
        val imports = exports.map { export ->
            Import(
                moduleName,
                export.name,
                export.value,
            )
        }

        this.imports += imports
        return true
    }
}
