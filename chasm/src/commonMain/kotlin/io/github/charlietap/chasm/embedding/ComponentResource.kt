package io.github.charlietap.chasm.embedding

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.fold
import com.github.michaelbull.result.mapError
import io.github.charlietap.chasm.embedding.error.ChasmError
import io.github.charlietap.chasm.embedding.shapes.ChasmResult
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Error
import io.github.charlietap.chasm.embedding.shapes.ChasmResult.Success
import io.github.charlietap.chasm.embedding.shapes.ComponentHostFunctionContext
import io.github.charlietap.chasm.embedding.shapes.ComponentResourceDestructor
import io.github.charlietap.chasm.embedding.shapes.ComponentResourceType
import io.github.charlietap.chasm.embedding.shapes.Store
import io.github.charlietap.chasm.executor.invoker.drop.ComponentResourceDropper
import io.github.charlietap.chasm.host.HostFunctionException
import io.github.charlietap.chasm.runtime.component.error.ComponentInvocationError
import io.github.charlietap.chasm.runtime.component.resource.ResourceTableException
import io.github.charlietap.chasm.runtime.component.resource.RuntimeHostResourceDestructor
import io.github.charlietap.chasm.runtime.component.resource.RuntimeResourceType
import io.github.charlietap.chasm.runtime.value.component.ComponentValue

fun componentResourceType(
    store: Store,
    destructor: ComponentResourceDestructor,
): ComponentResourceType {
    val hostContext = ComponentHostFunctionContext(store)
    val runtimeDestructor = RuntimeHostResourceDestructor { value ->
        try {
            with(hostContext) {
                destructor(value)
            }
            Ok(Unit)
        } catch (exception: HostFunctionException) {
            Err(ComponentInvocationError.HostFunctionFailure(exception.reason))
        }
    }
    return ComponentResourceType(
        store = store.identity,
        address = store.componentStore().resourceTypes.define(RuntimeResourceType.Host(runtimeDestructor)),
    )
}

fun ComponentHostFunctionContext.resource(
    type: ComponentResourceType,
    value: Any?,
): ComponentValue.Resource.Own {
    require(type.store === store.identity) { "component resource type belongs to another store" }

    val componentStore = store.componentStore()
    require(componentStore.resourceTypes[type.address] is RuntimeResourceType.Host) {
        "component resource type is not host-defined"
    }
    val representation = componentStore.hostResourcePayloads().insert(value)
    return try {
        ComponentValue.Resource.Own(
            store = store.identity,
            handle = componentStore.hostResourceHandles.insertOwn(type.address, representation),
        )
    } catch (exception: ResourceTableException) {
        componentStore.hostResourcePayloads().remove(representation)
        throw exception
    }
}

fun ComponentHostFunctionContext.resourceValue(
    type: ComponentResourceType,
    resource: ComponentValue.Resource,
): Any? {
    require(type.store === store.identity) { "component resource type belongs to another store" }
    require(resource.store === store.identity) { "component resource belongs to another store" }

    val componentStore = store.componentStore()
    require(componentStore.resourceTypes[type.address] is RuntimeResourceType.Host) {
        "component resource type is not host-defined"
    }
    val representation = when (resource) {
        is ComponentValue.Resource.Own -> componentStore.hostResourceHandles.ownRepresentation(
            resource.handle,
            type.address,
        )
        is ComponentValue.Resource.Borrow -> {
            require(componentStore.isCallActive(resource.callToken)) { "component resource borrow has expired" }
            componentStore.hostResourceHandles.borrowRepresentation(
                resource.handle,
                type.address,
                resource.callToken,
            )
        }
    }
    return componentStore.hostResourcePayloads()[representation]
}

fun dropResource(
    store: Store,
    resource: ComponentValue.Resource.Own,
): ChasmResult<Unit, ChasmError.ExecutionError> = dropResource(
    store = store,
    resource = resource,
    resourceDropper = ::ComponentResourceDropper,
)

internal fun dropResource(
    store: Store,
    resource: ComponentValue.Resource.Own,
    resourceDropper: ComponentResourceDropper,
): ChasmResult<Unit, ChasmError.ExecutionError> = resourceDropper(
    store.store,
    store.componentStore(),
    resource,
).mapError(ComponentInvocationError::toString)
    .mapError(ChasmError::ExecutionError)
    .fold(::Success, ::Error)
