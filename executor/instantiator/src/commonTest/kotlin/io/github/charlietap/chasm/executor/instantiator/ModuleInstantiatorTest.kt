package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.config.runtimeConfig
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionAddress
import io.github.charlietap.chasm.fixture.executor.runtime.instance.functionExternalValue
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.returnArity
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.ir.instruction.expression
import io.github.charlietap.chasm.fixture.ir.module.elementSegment
import io.github.charlietap.chasm.fixture.ir.module.function
import io.github.charlietap.chasm.fixture.ir.module.global
import io.github.charlietap.chasm.fixture.ir.module.import
import io.github.charlietap.chasm.fixture.ir.module.module
import io.github.charlietap.chasm.fixture.ir.module.startFunction
import io.github.charlietap.chasm.fixture.ir.module.table
import io.github.charlietap.chasm.fixture.ir.type.heapType
import io.github.charlietap.chasm.ir.factory.ModuleFactory
import io.github.charlietap.chasm.ir.instruction.Expression
import io.github.charlietap.chasm.ir.instruction.ReferenceInstruction
import io.github.charlietap.chasm.optimiser.Optimiser
import io.github.charlietap.chasm.predecoder.Predecoder
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import io.github.charlietap.chasm.executor.runtime.function.Expression as RuntimeExpression
import io.github.charlietap.chasm.fixture.ast.module.module as astModule
import io.github.charlietap.chasm.fixture.executor.runtime.instance.import as runtimeImport

class ModuleInstantiatorTest {

    @Test
    fun `can instantiate a module instance`() {

        val config = runtimeConfig()
        val store = store()
        val import = import()
        val global = global()
        val tableInitExpression = expression(
            instructions = listOf(
                ReferenceInstruction.RefNull(heapType()),
            ),
        )
        val table = table(
            initExpression = tableInitExpression,
        )
        val elementSegment = elementSegment()
        val function = function()
        val startFunction = startFunction(function.idx)
        val astModule = astModule()
        val module = module(
            functions = listOf(function),
            imports = listOf(import),
            globals = listOf(global),
            tables = listOf(table),
            elementSegments = listOf(elementSegment),
            startFunction = startFunction,
        )
        val context = instantiationContext(store, module)
        val imports = listOf(runtimeImport(externalValue = functionExternalValue(functionAddress(0))))

        val partialInstance = moduleInstance(
            functionAddresses = mutableListOf(functionAddress(0)),
        )

        val moduleFactory: ModuleFactory = { _module ->
            assertEquals(astModule, _module)
            module
        }

        val optimiser: Optimiser = { _module ->
            assertEquals(module, _module)
            module
        }

        val pallocator: PartialModuleAllocator = { _context, _imports ->
            assertEquals(context, _context)
            assertEquals(imports, _imports)

            context.instance = partialInstance
            _context.instance = partialInstance

            Ok(partialInstance)
        }

        val allocator: ModuleAllocator = { _context, _instance, _tableInitExpressions ->
            assertEquals(context, _context)
            assertEquals(partialInstance, _instance)
            assertContentEquals(longArrayOf(ReferenceValue.Null(heapType()).toLongFromBoxed()), _tableInitExpressions)

            Ok(partialInstance)
        }

        val evaluator: ExpressionEvaluator = { _config, _store, _instance, _expression, _arity ->
            assertEquals(config, _config)
            assertEquals(store, _store)
            assertEquals(partialInstance, _instance)
            assertEquals(returnArity(1), _arity)

            Ok(ReferenceValue.Null(heapType()).toLong())
        }

        val invoker: FunctionInvoker = { _config, _store, _address, _locals ->
            assertEquals(config, _config)
            assertEquals(store, _store)
            assertEquals(functionAddress(0), _address)
            assertEquals(emptyList(), _locals)

            Ok(emptyList())
        }

        val tableInitializer: TableInitializer = { _context, _instance ->
            assertEquals(context, _context)
            assertEquals(partialInstance, _instance)

            Ok(Unit)
        }

        val memoryInitializer: MemoryInitializer = { _context, _instance ->
            assertEquals(context, _context)
            assertEquals(partialInstance, _instance)

            Ok(Unit)
        }

        val runtimeTableInitExpression = runtimeExpression()
        val expressionPredecoder: Predecoder<Expression, RuntimeExpression> = { _context, _expression ->
            assertEquals(tableInitExpression, _expression)

            Ok(runtimeTableInitExpression)
        }

        val actual = ModuleInstantiator(
            config = config,
            store = store,
            module = astModule,
            imports = imports,
            moduleFactory = moduleFactory,
            optimiser = optimiser,
            partialAllocator = pallocator,
            allocator = allocator,
            invoker = invoker,
            evaluator = evaluator,
            tableInitializer = tableInitializer,
            memoryInitializer = memoryInitializer,
            expressionPredecoder = expressionPredecoder,
        )

        assertEquals(Ok(partialInstance), actual)
    }
}
