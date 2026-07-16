package io.github.charlietap.chasm.fixture.runtime.component.resource

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.fixture.runtime.component.address.componentRootAddress
import io.github.charlietap.chasm.fixture.runtime.component.index.runtimeComponentInstanceIndex
import io.github.charlietap.chasm.fixture.runtime.component.index.runtimeResourceTypeIndex
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.runtime.address.Address
import io.github.charlietap.chasm.runtime.address.ComponentRootAddress
import io.github.charlietap.chasm.runtime.component.index.RuntimeComponentInstanceIndex
import io.github.charlietap.chasm.runtime.component.index.RuntimeResourceTypeIndex
import io.github.charlietap.chasm.runtime.component.resource.CanonicalHandleTable
import io.github.charlietap.chasm.runtime.component.resource.CanonicalResourceFunction
import io.github.charlietap.chasm.runtime.component.resource.CanonicalResourceFunctionKind
import io.github.charlietap.chasm.runtime.component.resource.HostResourceHandleTable
import io.github.charlietap.chasm.runtime.component.resource.HostResourcePayloadTable
import io.github.charlietap.chasm.runtime.component.resource.ResourceTypeTable
import io.github.charlietap.chasm.runtime.component.resource.RuntimeGuestResourceDestructor
import io.github.charlietap.chasm.runtime.component.resource.RuntimeHostResourceDestructor
import io.github.charlietap.chasm.runtime.component.resource.RuntimeResourceType
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

fun canonicalHandleTable() = CanonicalHandleTable()

fun hostResourceHandleTable() = HostResourceHandleTable()

fun hostResourcePayloadTable() = HostResourcePayloadTable()

fun resourceTypeTable() = ResourceTypeTable()

fun guestRuntimeResourceType(
    root: ComponentRootAddress = componentRootAddress(),
    owner: RuntimeComponentInstanceIndex = runtimeComponentInstanceIndex(),
    destructor: Address.Function? = null,
    destructorInstance: ModuleInstance = moduleInstance(),
): RuntimeResourceType.Guest = RuntimeResourceType.Guest(
    root = root,
    owner = owner,
    destructor = destructor?.let { function ->
        RuntimeGuestResourceDestructor(destructorInstance, function)
    },
)

fun hostRuntimeResourceType(
    destructor: RuntimeHostResourceDestructor = RuntimeHostResourceDestructor { Ok(Unit) },
): RuntimeResourceType.Host = RuntimeResourceType.Host(destructor)

fun canonicalResourceFunction(
    kind: CanonicalResourceFunctionKind = CanonicalResourceFunctionKind.ResourceNew,
    owner: RuntimeComponentInstanceIndex = runtimeComponentInstanceIndex(),
    resourceType: RuntimeResourceTypeIndex = runtimeResourceTypeIndex(),
): CanonicalResourceFunction = CanonicalResourceFunction(
    kind = kind,
    owner = owner,
    resourceType = resourceType,
)
