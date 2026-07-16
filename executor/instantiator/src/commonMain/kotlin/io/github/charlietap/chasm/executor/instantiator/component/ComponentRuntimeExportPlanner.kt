package io.github.charlietap.chasm.executor.instantiator.component

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.component.initializer.PreparedCoreModuleSource
import io.github.charlietap.chasm.executor.instantiator.component.translation.PlannerExternalReference
import io.github.charlietap.chasm.runtime.component.error.ComponentPreparationError
import io.github.charlietap.chasm.runtime.component.error.UnsupportedComponentFeature
import io.github.charlietap.chasm.runtime.component.info.PreparedComponentCoreModule
import io.github.charlietap.chasm.runtime.component.info.PreparedComponentExport
import io.github.charlietap.chasm.runtime.component.info.PreparedComponentExportValue

internal fun PlannerExternalReference.runtimeExport(): Result<PreparedComponentExportValue?, ComponentPreparationError> =
    when (this) {
        is PlannerExternalReference.Function -> Ok(PreparedComponentExportValue.Function(value))
        is PlannerExternalReference.Instance -> binding {
            PreparedComponentExportValue.Instance(
                exports = buildList {
                    value.exports.forEach { (name, reference) ->
                        reference.runtimeExport().bind()?.let { export ->
                            add(PreparedComponentExport(name, export))
                        }
                    }
                },
            )
        }
        is PlannerExternalReference.Type -> value.resourceType?.let { resourceType ->
            Ok(PreparedComponentExportValue.ResourceType(resourceType))
        } ?: Ok(null)
        is PlannerExternalReference.CoreModule -> Ok(
            PreparedComponentExportValue.CoreModule(
                when (val source = value.source) {
                    is PreparedCoreModuleSource.Embedded -> PreparedComponentCoreModule.Embedded(source.moduleIndex)
                    is PreparedCoreModuleSource.Import -> PreparedComponentCoreModule.Import(source.importIndex)
                },
            ),
        )
        is PlannerExternalReference.Component -> unsupported(
            UnsupportedComponentFeature.DynamicComponentInstantiation,
        )
    }
