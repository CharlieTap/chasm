package io.github.charlietap.chasm.runtime.component.resource

import com.github.michaelbull.result.Result
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

sealed interface RuntimeResourceType {

    data class Guest(
        val root: ComponentRootAddress,
        val owner: RuntimeComponentInstanceIndex,
        val destructor: RuntimeGuestResourceDestructor? = null,
    ) : RuntimeResourceType

    data class Host(
        val destructor: RuntimeHostResourceDestructor,
    ) : RuntimeResourceType
}

data class RuntimeGuestResourceDestructor(
    val instance: ModuleInstance,
    val function: Address.Function,
)

fun interface RuntimeHostResourceDestructor {

    operator fun invoke(value: Any?): Result<Unit, ComponentInvocationError>
}
