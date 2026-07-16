package io.github.charlietap.chasm.runtime.store

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.runtime.instance.moduleAllocation
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.runtime.error.InstantiationError
import io.github.charlietap.chasm.runtime.error.InvocationError
import kotlin.test.Test
import kotlin.test.assertEquals

class InstanceLifetimeRegistryTest {

    @Test
    fun `a provider remains live until its consumer is dropped`() {
        val registry = InstanceLifetimeRegistry()
        val functionAddress = functionAddress()
        val provider = moduleInstance()
        registry.begin(provider, emptyList())
        val providerAllocation = moduleAllocation(
            functionAddresses = listOf(functionAddress),
        )
        registry.register(provider, providerAllocation)
        registry.publish(provider)

        val consumer = moduleInstance()
        val importedFunction = functionExternalValue(functionAddress)
        registry.begin(consumer, listOf(importedFunction, importedFunction))
        val consumerAllocation = moduleAllocation(
            providers = registry.providers(consumer),
        )
        registry.register(consumer, consumerAllocation)
        registry.publish(consumer)

        val providerBeforeConsumer = registry.prepareDrop(provider)
        val consumerDrop = registry.prepareDrop(consumer)
        registry.completeDrop(consumer)
        val providerAfterConsumer = registry.prepareDrop(provider)

        val actual = listOf(providerBeforeConsumer, consumerDrop, providerAfterConsumer)
        val expected = listOf(
            Err(InvocationError.InstanceHasDependants),
            Ok(consumerAllocation),
            Ok(providerAllocation),
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `an address owned by a dropped provider cannot be imported`() {
        val registry = InstanceLifetimeRegistry()
        val functionAddress = functionAddress()
        val provider = moduleInstance()
        registry.begin(provider, emptyList())
        registry.register(
            provider,
            moduleAllocation(functionAddresses = listOf(functionAddress)),
        )
        registry.publish(provider)
        registry.prepareDrop(provider)
        registry.completeDrop(provider)
        val consumer = moduleInstance()

        val actual = registry.begin(
            consumer,
            listOf(functionExternalValue(functionAddress)),
        )

        val expected = Err(InstantiationError.ImportedFromDeallocatedInstance)
        assertEquals(expected, actual)
    }

    @Test
    fun `a failed import does not hide providers from the next instance`() {
        val registry = InstanceLifetimeRegistry()
        val liveAddress = functionAddress()
        val liveProvider = moduleInstance()
        registry.begin(liveProvider, emptyList())
        registry.register(
            liveProvider,
            moduleAllocation(functionAddresses = listOf(liveAddress)),
        )
        registry.publish(liveProvider)

        val deadAddress = functionAddress(1)
        val deadProvider = moduleInstance()
        registry.begin(deadProvider, emptyList())
        registry.register(
            deadProvider,
            moduleAllocation(functionAddresses = listOf(deadAddress)),
        )
        registry.publish(deadProvider)
        registry.prepareDrop(deadProvider)
        registry.completeDrop(deadProvider)

        val failedConsumer = moduleInstance()
        val failedBegin = registry.begin(
            failedConsumer,
            listOf(
                functionExternalValue(liveAddress),
                functionExternalValue(deadAddress),
            ),
        )

        val liveConsumer = moduleInstance()
        registry.begin(liveConsumer, listOf(functionExternalValue(liveAddress)))
        registry.register(liveConsumer, moduleAllocation())
        registry.publish(liveConsumer)
        val providerDrop = registry.prepareDrop(liveProvider)
        val actual = failedBegin to providerDrop

        val expected = Err(InstantiationError.ImportedFromDeallocatedInstance) to Err(InvocationError.InstanceHasDependants)
        assertEquals(expected, actual)
    }

    @Test
    fun `an instance cannot be dropped through another registry`() {
        val firstRegistry = InstanceLifetimeRegistry()
        val firstInstance = moduleInstance()
        firstRegistry.begin(firstInstance, emptyList())
        firstRegistry.register(firstInstance, moduleAllocation())
        firstRegistry.publish(firstInstance)

        val secondRegistry = InstanceLifetimeRegistry()
        val secondInstance = moduleInstance()
        secondRegistry.begin(secondInstance, emptyList())
        secondRegistry.register(secondInstance, moduleAllocation())
        secondRegistry.publish(secondInstance)

        val actual = secondRegistry.prepareDrop(firstInstance)

        val expected = Err(InvocationError.InstanceNotOwnedByStore)
        assertEquals(expected, actual)
    }

    @Test
    fun `an orphan can only be released during store teardown`() {
        val registry = InstanceLifetimeRegistry()
        val instance = moduleInstance()
        val allocation = moduleAllocation()
        registry.begin(instance, emptyList())
        registry.register(instance, allocation)
        registry.abandon(instance)

        val ordinaryDrop = registry.prepareDrop(instance)
        val teardown = registry.prepareTeardown(instance)
        val actual = ordinaryDrop to teardown

        val expected = Err(InvocationError.InvocationOfADeinstantiatedInstance) to Ok(allocation)
        assertEquals(expected, actual)
    }

    @Test
    fun `rollback abandons a live instance whose allocation may have escaped`() {
        val registry = InstanceLifetimeRegistry()
        val instance = moduleInstance()
        val allocation = moduleAllocation()
        registry.begin(instance, emptyList())
        registry.register(instance, allocation)
        registry.publish(instance, allocationMayHaveEscaped = true)

        val rollback = registry.prepareRollback(instance)
        val teardown = registry.prepareTeardown(instance)
        val actual = rollback to teardown

        val expected = Ok(null) to Ok(allocation)
        assertEquals(expected, actual)
    }

    @Test
    fun `dropping an instance releases its allocation metadata`() {
        val registry = InstanceLifetimeRegistry()
        val instance = moduleInstance()
        registry.begin(instance, emptyList())
        registry.register(instance, moduleAllocation())
        registry.publish(instance)
        registry.prepareDrop(instance)

        registry.completeDrop(instance)

        assertEquals(null, instance.allocation)
    }
}
