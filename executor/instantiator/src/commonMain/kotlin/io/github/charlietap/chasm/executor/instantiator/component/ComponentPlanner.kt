package io.github.charlietap.chasm.executor.instantiator.component

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import com.github.michaelbull.result.toResultOr
import io.github.charlietap.chasm.ast.component.AliasDefinition
import io.github.charlietap.chasm.ast.component.Component
import io.github.charlietap.chasm.ast.component.CoreExportTarget
import io.github.charlietap.chasm.ast.component.CoreInstanceDefinition
import io.github.charlietap.chasm.ast.component.Definition
import io.github.charlietap.chasm.ast.component.ExportTarget
import io.github.charlietap.chasm.ast.component.InstanceDefinition
import io.github.charlietap.chasm.ast.component.InstanceExportAliasTarget
import io.github.charlietap.chasm.ast.component.OuterAliasTarget
import io.github.charlietap.chasm.ast.component.TypeDefinition
import io.github.charlietap.chasm.config.RuntimeConfig
import io.github.charlietap.chasm.executor.instantiator.ModuleCompiler
import io.github.charlietap.chasm.executor.instantiator.component.canonical.planCanonical
import io.github.charlietap.chasm.executor.instantiator.component.initializer.ComponentInitializer
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreExternalValue
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreImport
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreModuleSource
import io.github.charlietap.chasm.executor.instantiator.component.translation.ComponentTranslation
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerComponentInstanceReference
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerComponentReference
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerCoreExportReference
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerCoreInstanceReference
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerCoreModuleReference
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerExternalReference
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerFrame
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerFrameSnapshot
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerTypeReference
import io.github.charlietap.chasm.executor.instantiator.component.translation.TranslatedComponentDefinition
import io.github.charlietap.chasm.executor.instantiator.component.translation.translateComponent
import io.github.charlietap.chasm.runtime.component.error.ComponentPreparationError
import io.github.charlietap.chasm.runtime.component.error.UnsupportedComponentFeature
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.info.ComponentRuntimeInfo
import io.github.charlietap.chasm.runtime.component.info.PreparedComponentExport
import io.github.charlietap.chasm.runtime.component.instance.ComponentInstanceCounts
import io.github.charlietap.chasm.type.NumberType
import io.github.charlietap.chasm.type.ValueType
import io.github.charlietap.chasm.type.component.ComponentDefinedType
import io.github.charlietap.chasm.type.component.ComponentTypes

typealias ComponentPlanner = (
    RuntimeConfig,
    Component,
    ComponentTypes,
) -> Result<PreparedComponent, ComponentPreparationError>

fun ComponentPlanner(
    config: RuntimeConfig,
    component: Component,
    types: ComponentTypes,
): Result<PreparedComponent, ComponentPreparationError> = ComponentPlanner(
    config = config,
    component = component,
    types = types,
    moduleCompiler = ::ModuleCompiler,
)

internal inline fun ComponentPlanner(
    config: RuntimeConfig,
    component: Component,
    types: ComponentTypes,
    crossinline moduleCompiler: ModuleCompiler,
): Result<PreparedComponent, ComponentPreparationError> = planPreparedComponent(
    config = config,
    component = component,
    types = types,
    moduleCompiler = { compilerConfig, module -> moduleCompiler(compilerConfig, module) },
)

@PublishedApi
internal fun planPreparedComponent(
    config: RuntimeConfig,
    component: Component,
    types: ComponentTypes,
    moduleCompiler: ModuleCompiler,
): Result<PreparedComponent, ComponentPreparationError> = binding {
    val context = ComponentPlanningContext(config)
    val translation = translateComponent(
        context = context,
        component = component,
        types = types.root,
        moduleCompiler = moduleCompiler,
    ).bind()

    val root = context.nextComponentInstance()
    planComponent(
        context = context,
        translation = translation,
        owner = root,
        captures = emptyList(),
        bindings = null,
        root = true,
    ).bind()

    PreparedComponent(
        runtimeInfo = ComponentRuntimeInfo(
            types = types,
            exports = context.exports.toList(),
            functions = context.functions.toList(),
            linearMemoryLayouts = context.layoutCompiler.layouts,
            callPlans = context.callPlans.toList(),
            resourceTypes = context.resourceTypes.toMap(),
        ),
        modules = context.modules.toList(),
        initializers = context.initializers.toList(),
        imports = context.imports.toList(),
        counts = ComponentInstanceCounts(
            componentInstances = context.componentInstanceCount,
            coreInstances = context.coreInstanceCount,
            coreFunctions = context.coreFunctionCount,
            memories = context.memoryCount,
            reallocs = context.reallocCount,
            postReturns = context.postReturnCount,
            resourceTypes = context.resourceTypeCount,
            hostFunctions = context.hostFunctionCount,
        ),
        componentInstanceParents = context.componentInstanceParents.toIntArray(),
    )
}

private fun planComponent(
    context: ComponentPlanningContext,
    translation: ComponentTranslation,
    owner: RuntimeComponentInstanceIndex,
    captures: List<PlannerFrameSnapshot>,
    bindings: Map<String, PlannerExternalReference>?,
    root: Boolean,
): Result<PlannerFrame, ComponentPreparationError> = binding {
    val frame = PlannerFrame(owner, translation.types, captures)

    translation.definitions.forEach { definition ->
        when (definition) {
            is TranslatedComponentDefinition.CoreModule -> {
                val type = frame.types.coreModules.getOrNull(frame.coreModules.size)
                    .toResultOr { invalidPreparation("core module index space does not match validated types") }
                    .bind()
                frame.coreModules += PlannerCoreModuleReference(
                    type = type,
                    source = PreparedCoreModuleSource.Embedded(definition.moduleIndex),
                )
            }
            is TranslatedComponentDefinition.NestedComponent -> {
                frame.components += PlannerComponentReference(definition.component, frame.capture())
            }
            is TranslatedComponentDefinition.Source -> planDefinition(
                context = context,
                frame = frame,
                definition = definition.definition,
                bindings = bindings,
                root = root,
            ).bind()
        }
    }

    frame
}

private fun planDefinition(
    context: ComponentPlanningContext,
    frame: PlannerFrame,
    definition: Definition,
    bindings: Map<String, PlannerExternalReference>?,
    root: Boolean,
): Result<Unit, ComponentPreparationError> = binding {
    when (definition) {
        is io.github.charlietap.chasm.ast.component.CoreModule,
        is io.github.charlietap.chasm.ast.component.NestedComponent,
        -> error("translated definitions must not retain module or component syntax")
        is io.github.charlietap.chasm.ast.component.CoreInstance -> planCoreInstance(
            context,
            frame,
            definition.instance,
        ).bind()
        is io.github.charlietap.chasm.ast.component.CoreType -> {
            frame.coreTypes += frame.types.coreTypes.getOrNull(frame.coreTypes.size)
                .toResultOr { invalidPreparation("core type index space does not match validated types") }
                .bind()
        }
        is io.github.charlietap.chasm.ast.component.Instance -> planInstance(
            context,
            frame,
            definition.instance,
        ).bind()
        is io.github.charlietap.chasm.ast.component.Alias -> planAlias(frame, definition.alias).bind()
        is io.github.charlietap.chasm.ast.component.Type -> {
            val type = frame.types.types.getOrNull(frame.componentTypes.size)
                .toResultOr { invalidPreparation("component type index space does not match validated types") }
                .bind()
            val resourceType = (type.type as? ComponentDefinedType.Resource)?.let {
                val syntax = definition.type as? TypeDefinition.Resource
                    ?: invalidPreparation("validated resource type does not match component syntax")
                        .let { error -> Err(error).bind() }
                if (syntax.representation != I32) {
                    unsupported(UnsupportedComponentFeature.Memory64).bind<Unit>()
                }
                val resourceType = context.resourceType(it.id)
                val destructor = syntax.destructor?.let { index ->
                    val function = frame.coreFunctions.getOrNull(index.idx.toInt())
                        .toResultOr { invalidPreparation("resource destructor references an unknown core function") }
                        .bind()
                    function
                }
                context.initializers += ComponentInitializer.DefineResourceType(
                    resourceType = resourceType,
                    owner = frame.owner,
                    representation = syntax.representation,
                    destructor = destructor,
                )
                resourceType
            }
            frame.componentTypes += PlannerTypeReference(type, resourceType)
        }
        is io.github.charlietap.chasm.ast.component.Canon -> planCanonical(
            context = context,
            frame = frame,
            canonical = definition.canon,
        ).bind()
        is io.github.charlietap.chasm.ast.component.Start -> unsupported(
            UnsupportedComponentFeature.ComponentStart,
        ).bind<Unit>()
        is io.github.charlietap.chasm.ast.component.Value -> unsupported(
            UnsupportedComponentFeature.ComponentValue,
        ).bind<Unit>()
        is io.github.charlietap.chasm.ast.component.Import -> {
            val name = definition.name.name.name
            val expected = frame.types.type.imports[name]
                .toResultOr { invalidPreparation("missing validated import type for $name") }
                .bind()
            val reference = if (bindings == null) {
                val prepared = context.prepareImport(
                    name = name,
                    type = expected,
                    owner = frame.owner,
                    resourceTypes = frame.importedResourceTypes,
                ).bind()
                prepared.import?.let(context.imports::add)
                prepared.reference
            } else {
                bindings[name].toResultOr { invalidPreparation("missing nested component argument $name") }.bind()
            }
            frame.append(reference)
        }
        is io.github.charlietap.chasm.ast.component.Export -> {
            val name = definition.name.name.name
            val reference = frame.resolve(definition.target).bind()
            frame.append(reference)
            frame.exports[name] = reference
            if (root) {
                reference.runtimeExport().bind()?.let { value ->
                    context.exports += PreparedComponentExport(name, value)
                }
            }
        }
    }
}

private fun planCoreInstance(
    context: ComponentPlanningContext,
    frame: PlannerFrame,
    definition: CoreInstanceDefinition,
): Result<Unit, ComponentPreparationError> = binding {
    when (definition) {
        is CoreInstanceDefinition.Instantiate -> {
            val module = frame.coreModules.getOrNull(definition.moduleIndex.idx.toInt())
                .toResultOr { invalidPreparation("unknown core module ${definition.moduleIndex.idx}") }
                .bind()
            val arguments = definition.args.associate { argument ->
                val instance = frame.coreInstances.getOrNull(argument.instanceIndex.idx.toInt())
                    .toResultOr { invalidPreparation("unknown core instance ${argument.instanceIndex.idx}") }
                    .bind()
                argument.name.name to instance
            }
            val imports = module.type.imports.map { (name, _) ->
                val argument = arguments[name.module]
                    .toResultOr { invalidPreparation("missing core module argument ${name.module}") }
                    .bind()
                val export = argument.exports[name.entity]
                    .toResultOr { invalidPreparation("missing core instance export ${name.entity}") }
                    .bind()
                PreparedCoreImport(
                    moduleName = name.module,
                    entityName = name.entity,
                    value = export.externalValue().bind(),
                )
            }
            val runtimeIndex = context.nextCoreInstance()
            context.initializers += ComponentInitializer.InstantiateCoreModule(
                module = module.source,
                instance = runtimeIndex,
                imports = imports,
            )
            frame.coreInstances += PlannerCoreInstanceReference(
                runtimeIndex = runtimeIndex,
                exports = context.moduleExports(module, runtimeIndex).bind(),
            )
        }
        is CoreInstanceDefinition.InlineExports -> {
            val exports = linkedMapOf<String, PlannerCoreExportReference>()
            definition.exports.forEach { export ->
                exports[export.name.name] = frame.resolve(export.target).bind()
            }
            frame.coreInstances += PlannerCoreInstanceReference(
                runtimeIndex = null,
                exports = exports,
            )
        }
    }
}

private fun planInstance(
    context: ComponentPlanningContext,
    frame: PlannerFrame,
    definition: InstanceDefinition,
): Result<Unit, ComponentPreparationError> = binding {
    val owner = context.nextComponentInstance(frame.owner)
    val exports = when (definition) {
        is InstanceDefinition.Instantiate -> {
            val component = frame.components.getOrNull(definition.componentIndex.idx.toInt())
                .toResultOr { invalidPreparation("unknown component ${definition.componentIndex.idx}") }
                .bind()
            val arguments = linkedMapOf<String, PlannerExternalReference>()
            definition.args.forEach { argument ->
                arguments[argument.name.name] = frame.resolve(argument.target).bind()
            }
            planComponent(
                context = context,
                translation = component.translation,
                owner = owner,
                captures = component.captures,
                bindings = arguments,
                root = false,
            ).bind().exports
        }
        is InstanceDefinition.InlineExports -> buildMap {
            definition.exports.forEach { export ->
                put(export.name.name.name, frame.resolve(export.target).bind())
            }
        }
    }
    frame.instances += PlannerComponentInstanceReference(owner, exports)
}

private fun planAlias(
    frame: PlannerFrame,
    alias: AliasDefinition,
): Result<Unit, ComponentPreparationError> = binding {
    when (alias) {
        is AliasDefinition.InstanceExport -> {
            val instanceIndex = when (val target = alias.target) {
                is InstanceExportAliasTarget.Module -> target.instance
                is InstanceExportAliasTarget.Function -> target.instance
                is InstanceExportAliasTarget.Value -> target.instance
                is InstanceExportAliasTarget.Type -> target.instance
                is InstanceExportAliasTarget.Component -> target.instance
                is InstanceExportAliasTarget.Instance -> target.instance
            }
            val name = when (val target = alias.target) {
                is InstanceExportAliasTarget.Module -> target.name
                is InstanceExportAliasTarget.Function -> target.name
                is InstanceExportAliasTarget.Value -> target.name
                is InstanceExportAliasTarget.Type -> target.name
                is InstanceExportAliasTarget.Component -> target.name
                is InstanceExportAliasTarget.Instance -> target.name
            }
            val reference = frame.instances.getOrNull(instanceIndex.idx.toInt())?.exports?.get(name.name)
                .toResultOr { invalidPreparation("unknown component instance export ${name.name}") }
                .bind()
            frame.append(reference)
        }
        is AliasDefinition.CoreInstanceExport -> {
            val target = alias.target
            val instanceIndex = when (target) {
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Function -> target.instance
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Table -> target.instance
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Memory -> target.instance
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Global -> target.instance
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Tag -> target.instance
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Type -> target.instance
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Module -> target.instance
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Instance -> target.instance
            }
            val name = when (target) {
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Function -> target.name
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Table -> target.name
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Memory -> target.name
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Global -> target.name
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Tag -> target.name
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Type -> target.name
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Module -> target.name
                is io.github.charlietap.chasm.ast.component.CoreInstanceExportAliasTarget.Instance -> target.name
            }
            val reference = frame.coreInstances.getOrNull(instanceIndex.idx.toInt())?.exports?.get(name.name)
                .toResultOr { invalidPreparation("unknown core instance export ${name.name}") }
                .bind()
            frame.append(reference)
        }
        is AliasDefinition.Outer -> frame.appendOuter(alias.target).bind()
    }
}

private fun PlannerFrame.resolve(
    target: ExportTarget,
): Result<PlannerExternalReference, ComponentPreparationError> = when (target) {
    is ExportTarget.Module -> coreModules.getOrNull(target.index.idx.toInt())
        ?.let(PlannerExternalReference::CoreModule)
    is ExportTarget.Function -> functions.getOrNull(target.index.idx.toInt())
        ?.let(PlannerExternalReference::Function)
    is ExportTarget.Value -> return unsupported(UnsupportedComponentFeature.ComponentValue)
    is ExportTarget.Type -> componentTypes.getOrNull(target.index.idx.toInt())
        ?.let(PlannerExternalReference::Type)
    is ExportTarget.Component -> components.getOrNull(target.index.idx.toInt())
        ?.let(PlannerExternalReference::Component)
    is ExportTarget.Instance -> instances.getOrNull(target.index.idx.toInt())
        ?.let(PlannerExternalReference::Instance)
}.toResultOr { invalidPreparation("unknown component export target $target") }

private fun PlannerFrame.resolve(
    target: CoreExportTarget,
): Result<PlannerCoreExportReference, ComponentPreparationError> = when (target) {
    is CoreExportTarget.Function -> coreFunctions.getOrNull(target.index.idx.toInt())
        ?.let(PlannerCoreExportReference::Function)
    is CoreExportTarget.Table -> coreTables.getOrNull(target.index.idx.toInt())
        ?.let(PlannerCoreExportReference::Table)
    is CoreExportTarget.Memory -> coreMemories.getOrNull(target.index.idx.toInt())
        ?.let(PlannerCoreExportReference::Memory)
    is CoreExportTarget.Global -> coreGlobals.getOrNull(target.index.idx.toInt())
        ?.let(PlannerCoreExportReference::Global)
    is CoreExportTarget.Tag -> coreTags.getOrNull(target.index.idx.toInt())
        ?.let(PlannerCoreExportReference::Tag)
    is CoreExportTarget.Type -> coreTypes.getOrNull(target.index.idx.toInt())
        ?.let(PlannerCoreExportReference::Type)
    is CoreExportTarget.Module -> coreModules.getOrNull(target.index.idx.toInt())
        ?.let(PlannerCoreExportReference::Module)
    is CoreExportTarget.Instance -> coreInstances.getOrNull(target.index.idx.toInt())
        ?.let(PlannerCoreExportReference::Instance)
}.toResultOr { invalidPreparation("unknown core export target $target") }

private fun PlannerFrame.append(reference: PlannerExternalReference) {
    when (reference) {
        is PlannerExternalReference.CoreModule -> coreModules += reference.value
        is PlannerExternalReference.Function -> functions += reference.value
        is PlannerExternalReference.Type -> componentTypes += reference.value
        is PlannerExternalReference.Component -> components += reference.value
        is PlannerExternalReference.Instance -> instances += reference.value
    }
}

private fun PlannerFrame.append(reference: PlannerCoreExportReference) {
    when (reference) {
        is PlannerCoreExportReference.Function -> coreFunctions += reference.value
        is PlannerCoreExportReference.Table -> coreTables += reference.value
        is PlannerCoreExportReference.Memory -> coreMemories += reference.value
        is PlannerCoreExportReference.Global -> coreGlobals += reference.value
        is PlannerCoreExportReference.Tag -> coreTags += reference.value
        is PlannerCoreExportReference.Type -> coreTypes += reference.value
        is PlannerCoreExportReference.Module -> coreModules += reference.value
        is PlannerCoreExportReference.Instance -> coreInstances += reference.value
    }
}

private fun PlannerFrame.appendOuter(
    target: OuterAliasTarget,
): Result<Unit, ComponentPreparationError> = binding {
    val outer = outer(target.count())
        .toResultOr { invalidPreparation("unknown outer component scope") }
        .bind()
    when (target) {
        is OuterAliasTarget.Module -> coreModules += outer.coreModules.getOrNull(target.index.idx.toInt())
            .toResultOr { invalidPreparation("unknown outer core module ${target.index.idx}") }
            .bind()
        is OuterAliasTarget.CoreType -> coreTypes += outer.coreTypes.getOrNull(target.index.idx.toInt())
            .toResultOr { invalidPreparation("unknown outer core type ${target.index.idx}") }
            .bind()
        is OuterAliasTarget.Type -> componentTypes += outer.componentTypes.getOrNull(target.index.idx.toInt())
            .toResultOr { invalidPreparation("unknown outer component type ${target.index.idx}") }
            .bind()
        is OuterAliasTarget.Component -> components += outer.components.getOrNull(target.index.idx.toInt())
            .toResultOr { invalidPreparation("unknown outer component ${target.index.idx}") }
            .bind()
    }
}

private fun OuterAliasTarget.count(): UInt = when (this) {
    is OuterAliasTarget.Module -> count
    is OuterAliasTarget.CoreType -> count
    is OuterAliasTarget.Type -> count
    is OuterAliasTarget.Component -> count
}

private fun PlannerCoreExportReference.externalValue(): Result<PreparedCoreExternalValue, ComponentPreparationError> =
    when (this) {
        is PlannerCoreExportReference.Function -> com.github.michaelbull.result.Ok(value)
        is PlannerCoreExportReference.Table -> com.github.michaelbull.result.Ok(value)
        is PlannerCoreExportReference.Memory -> com.github.michaelbull.result.Ok(value)
        is PlannerCoreExportReference.Global -> com.github.michaelbull.result.Ok(value)
        is PlannerCoreExportReference.Tag -> com.github.michaelbull.result.Ok(value)
        is PlannerCoreExportReference.Type,
        is PlannerCoreExportReference.Module,
        is PlannerCoreExportReference.Instance,
        -> Err(invalidPreparation("core module imports require a core external value"))
    }

private val I32 = ValueType.Number(NumberType.I32)
