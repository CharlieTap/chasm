package io.github.charlietap.chasm.runtime.component.function

import io.github.charlietap.chasm.runtime.component.canonical.CanonicalValueTupleLayout
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLiftPlan
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.type.component.ComponentFunctionType

sealed interface PreparedComponentFunction {

    val owner: RuntimeComponentInstanceIndex

    val entryPolicy: ComponentEntryPolicy

    data class LiftedCore(
        val liftPlan: LinearMemoryLiftPlan,
        override val entryPolicy: ComponentEntryPolicy,
    ) : PreparedComponentFunction {

        override val owner: RuntimeComponentInstanceIndex
            get() = liftPlan.optionOwner
    }

    data class HostImport(
        val importSlot: Int,
        override val owner: RuntimeComponentInstanceIndex,
        val functionType: ComponentFunctionType,
        val parameterTuple: CanonicalValueTupleLayout,
        val resultTuple: CanonicalValueTupleLayout,
        val preparedHostCompatible: Boolean,
        override val entryPolicy: ComponentEntryPolicy,
    ) : PreparedComponentFunction
}
