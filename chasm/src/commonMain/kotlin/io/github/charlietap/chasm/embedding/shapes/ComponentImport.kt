package io.github.charlietap.chasm.embedding.shapes

data class ComponentImport(
    val name: String,
    val value: ComponentImportable,
)

sealed interface ComponentImportable {

    data class Function(
        val function: ComponentHostFunction,
    ) : ComponentImportable

    data class Instance(
        val imports: List<ComponentImport>,
    ) : ComponentImportable

    data class CoreModule(
        val module: Module,
    ) : ComponentImportable

    data class ResourceType(
        val type: ComponentResourceType,
    ) : ComponentImportable
}
