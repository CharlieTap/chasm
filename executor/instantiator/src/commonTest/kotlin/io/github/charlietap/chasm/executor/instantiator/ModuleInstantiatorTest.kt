package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.map
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.type.TypeAllocator
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.invoker.drop.ModuleInstanceDropper
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.ir.module.startFunction
import io.github.charlietap.chasm.fixture.runtime.error.invocationError
import io.github.charlietap.chasm.fixture.runtime.error.moduleRuntimeError
import io.github.charlietap.chasm.fixture.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.runtime.store.instanceLifetimes
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class ModuleInstantiatorTest {

    @Test
    fun `rolls back an instance when store specific instantiation fails`() {
        val store = store()
        val config = runtimeConfig()
        val compiled = CompiledModule(module())
        val error = moduleRuntimeError()
        val partialAllocator: PartialModuleAllocator = { context, instance, _, journal ->
            journal.markImports()
            context.store.instanceLifetimes().begin(instance, emptyList())
            Err(error)
        }
        var rollbackCalled = false
        val rollback: ModuleInstanceDropper = { actualStore, instance ->
            assertSame(store, actualStore)
            assertEquals(emptyList(), instance.allocation?.functionAddresses)
            rollbackCalled = true
            Ok(Unit)
        }

        val actual = ModuleInstantiator(
            config = config,
            store = store,
            module = compiled,
            imports = emptyList(),
            partialAllocator = partialAllocator,
            allocator = ::ModuleAllocator,
            typeAllocator = ::TypeAllocator,
            invoker = ::FunctionInvoker,
            constantExpressionEvaluator = ::ConstantExpressionEvaluator,
            tableInitializer = ::TableInitializer,
            memoryInitializer = ::MemoryInitializer,
            rollback = rollback,
        )

        val expected = Err(error)
        assertEquals(expected, actual)
        assertEquals(true, rollbackCalled)
    }

    @Test
    fun `rolls back a trapping start when its allocation cannot escape`() {
        val store = store()
        val config = runtimeConfig()
        val compiled = CompiledModule(module(startFunction = startFunction()))
        val error = invocationError()
        val partialAllocator: PartialModuleAllocator = { context, instance, _, journal ->
            journal.markImports()
            instance.functionAddresses += functionAddress()
            context.store.instanceLifetimes()
                .begin(instance, emptyList())
                .map { instance }
        }
        val allocator: ModuleAllocator = { _, instance, _ -> Ok(instance) }
        val typeAllocator: TypeAllocator = { _, _ -> emptyList() }
        val invoker: FunctionInvoker = { _, _, _, _, _ -> Err(error) }
        val tableInitializer: TableInitializer = { _, _, _ -> Ok(Unit) }
        val memoryInitializer: MemoryInitializer = { _, _ -> Ok(Unit) }
        var rollbackCalled = false
        val rollback: ModuleInstanceDropper = { _, _ ->
            rollbackCalled = true
            Ok(Unit)
        }

        val result = ModuleInstantiator(
            config = config,
            store = store,
            module = compiled,
            imports = emptyList(),
            partialAllocator = partialAllocator,
            allocator = allocator,
            typeAllocator = typeAllocator,
            invoker = invoker,
            constantExpressionEvaluator = ::ConstantExpressionEvaluator,
            tableInitializer = tableInitializer,
            memoryInitializer = memoryInitializer,
            rollback = rollback,
        )
        val actual = result to rollbackCalled

        val expected = Err(error) to true
        assertEquals(expected, actual)
    }
}
