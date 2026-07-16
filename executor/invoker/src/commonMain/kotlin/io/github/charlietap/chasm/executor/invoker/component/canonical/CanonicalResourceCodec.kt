package io.github.charlietap.chasm.executor.invoker.component.canonical

import io.github.charlietap.chasm.runtime.address.RuntimeResourceTypeAddress
import io.github.charlietap.chasm.runtime.component.canonical.CanonicalLayoutKind
import io.github.charlietap.chasm.runtime.component.canonical.LinearMemoryLayout
import io.github.charlietap.chasm.runtime.component.resource.ResourceTableException
import io.github.charlietap.chasm.runtime.component.resource.RuntimeResourceType
import io.github.charlietap.chasm.runtime.store.identity
import io.github.charlietap.chasm.runtime.value.component.ComponentValue

internal fun CanonicalResourceLifter(
    context: CanonicalCallContext,
    layout: LinearMemoryLayout,
    handle: Int,
): ComponentValue.Resource = resourceOperation {
    val type = context.resourceType(layout)
    val table = context.state.states.handleTable(context.owner)
    when (layout.kind) {
        CanonicalLayoutKind.Own -> {
            val representation = table.removeOwn(handle, type)
            ComponentValue.Resource.Own(
                store = context.store.identity(),
                handle = context.componentStore.hostResourceHandles.insertOwn(type, representation),
            )
        }
        CanonicalLayoutKind.Borrow -> {
            val lend = table.lend(handle, type)
            if (lend.ownsLender) {
                context.scope.recordGuestLender(table, handle)
            }
            val hostHandle = context.componentStore.hostResourceHandles.insertBorrow(
                type = type,
                representation = lend.representation,
                callToken = context.scope.callToken,
            )
            context.scope.recordHostBorrow(hostHandle)
            ComponentValue.Resource.Borrow(
                store = context.store.identity(),
                handle = hostHandle,
                callToken = context.scope.callToken,
            )
        }
        else -> error("canonical resource codec requires a resource layout")
    }
}

internal fun CanonicalResourceLowerer(
    context: CanonicalCallContext,
    layout: LinearMemoryLayout,
    value: ComponentValue,
): Int = resourceOperation {
    val resource = value as? ComponentValue.Resource
        ?: invalidValue("component value does not match its resource type")
    if (resource.store !== context.store.identity()) invalidValue("component resource belongs to another store")

    val type = context.resourceType(layout)
    val table = context.state.states.handleTable(context.owner)
    when (layout.kind) {
        CanonicalLayoutKind.Own -> {
            val own = resource as? ComponentValue.Resource.Own
                ?: invalidValue("component resource is borrowed")
            val representation = context.componentStore.hostResourceHandles.removeOwn(own.handle, type)
            table.insertOwn(type, representation)
        }
        CanonicalLayoutKind.Borrow -> {
            val representation = when (resource) {
                is ComponentValue.Resource.Own -> context.componentStore.hostResourceHandles.lend(
                    resource.handle,
                    type,
                ).also {
                    context.scope.recordHostLender(resource.handle)
                }
                is ComponentValue.Resource.Borrow -> {
                    if (!context.componentStore.isCallActive(resource.callToken)) {
                        invalidValue("component resource borrow has expired")
                    }
                    context.componentStore.hostResourceHandles.borrowRepresentation(
                        resource.handle,
                        type,
                        resource.callToken,
                    )
                }
            }
            val origin = context.componentStore.resourceTypes[type]
            if (
                origin is RuntimeResourceType.Guest &&
                origin.root == context.root &&
                origin.owner == context.owner
            ) {
                representation
            } else {
                table.insertBorrow(type, representation, context.scope.callToken).also { handle ->
                    context.scope.recordGuestBorrow(table, handle)
                }
            }
        }
        else -> error("canonical resource codec requires a resource layout")
    }
}

private fun CanonicalCallContext.resourceType(layout: LinearMemoryLayout): RuntimeResourceTypeAddress {
    val slot = layout.resourceType?.index
        ?: invalidValue("canonical resource layout has no runtime type")
    return state.resourceTypes.getOrNull(slot)
        ?.takeIf { address -> address >= 0 }
        ?.let(::RuntimeResourceTypeAddress)
        ?: invalidValue("canonical resource type is unavailable")
}

private inline fun <T> resourceOperation(block: () -> T): T = try {
    block()
} catch (exception: ResourceTableException) {
    invalidValue(exception.error.name)
}
