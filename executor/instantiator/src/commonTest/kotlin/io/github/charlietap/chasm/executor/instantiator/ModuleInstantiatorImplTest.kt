package io.github.charlietap.chasm.executor.instantiator

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.ReferenceInstruction
import io.github.charlietap.chasm.executor.instantiator.allocation.ModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.allocation.PartialModuleAllocator
import io.github.charlietap.chasm.executor.instantiator.classification.ClassifiedExternalValue
import io.github.charlietap.chasm.executor.instantiator.classification.ExternalValueClassifier
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.executor.instantiator.initialization.TableInitializer
import io.github.charlietap.chasm.executor.instantiator.validation.ImportValidator
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.executor.invoker.FunctionInvoker
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Address
import io.github.charlietap.chasm.executor.runtime.type.ExternalType
import io.github.charlietap.chasm.executor.runtime.value.ReferenceValue
import io.github.charlietap.chasm.fixture.instance.moduleInstance
import io.github.charlietap.chasm.fixture.module.elementSegment
import io.github.charlietap.chasm.fixture.module.function
import io.github.charlietap.chasm.fixture.module.global
import io.github.charlietap.chasm.fixture.module.import
import io.github.charlietap.chasm.fixture.module.module
import io.github.charlietap.chasm.fixture.module.startFunction
import io.github.charlietap.chasm.fixture.module.table
import io.github.charlietap.chasm.fixture.store
import io.github.charlietap.chasm.fixture.type.functionType
import io.github.charlietap.chasm.fixture.type.heapType
import kotlin.test.Test
import kotlin.test.assertEquals

class ModuleInstantiatorImplTest {

    @Test
    fun `can instantiate a module instance`() {

        val store = store()
        val import = import()
        val global = global()
        val tableInitExpression = Expression(listOf(ReferenceInstruction.RefNull(heapType())))
        val table = table(
            initExpression = tableInitExpression,
        )
        val elementSegment = elementSegment()
        val function = function()
        val startFunction = startFunction(function.idx)
        val module = module(
            functions = listOf(function),
            imports = listOf(import),
            globals = listOf(global),
            tables = listOf(table),
            elementSegments = listOf(elementSegment),
            startFunction = startFunction,
        )
        val imports = listOf(ExternalValue.Function(Address.Function(0)))
        val classified = ClassifiedExternalValue(
            type = ExternalType.Function(functionType()),
            value = imports[0],
        )
        val classifier: ExternalValueClassifier = { eStore, eExtern ->
            assertEquals(store, eStore)
            assertEquals(imports[0], eExtern)

            Ok(classified)
        }

        val validator: ImportValidator = { iModule, iImport, iClassifiedExtern ->
            assertEquals(module, iModule)
            assertEquals(import, iImport)
            assertEquals(classified, iClassifiedExtern)

            Ok(Unit)
        }

        val partialInstance = moduleInstance(
            functionAddresses = mutableListOf(Address.Function(0)),
        )
        val pallocator: PartialModuleAllocator = { eStore, eModule, eExterns ->
            assertEquals(store, eStore)
            assertEquals(module, eModule)
            assertEquals(imports, eExterns)

            Ok(partialInstance)
        }

        val allocator: ModuleAllocator = { eStore, eModule, eInstance, eExterns, eGlobalInit, eTableInit, eElemRefs ->
            assertEquals(store, eStore)
            assertEquals(module, eModule)
            assertEquals(partialInstance, eInstance)
            assertEquals(imports, eExterns)
            assertEquals(emptyList(), eGlobalInit)
            assertEquals(listOf(ReferenceValue.Null(heapType())), eTableInit)
            assertEquals(listOf(emptyList()), eElemRefs)

            Ok(partialInstance)
        }

        val evaluator: ExpressionEvaluator = { eStore, eInstance, eExpression, eArity ->
            assertEquals(store, eStore)
            assertEquals(partialInstance, eInstance)
            assertEquals(Arity(1), eArity)

            if (eExpression.instructions.isEmpty()) {
                Ok(null)
            } else {
                Ok(ReferenceValue.Null(heapType()))
            }
        }

        val invoker: FunctionInvoker = { eStore, eAddress, eLocals ->
            assertEquals(store, eStore)
            assertEquals(Address.Function(0), eAddress)
            assertEquals(emptyList(), eLocals)

            Ok(emptyList())
        }

        val tableInitializer: TableInitializer = { eStore, eInstance, eModule ->
            assertEquals(store, eStore)
            assertEquals(partialInstance, eInstance)
            assertEquals(module, eModule)

            Ok(Unit)
        }

        val memoryInitializer: MemoryInitializer = { eStore, eInstance, eModule ->
            assertEquals(store, eStore)
            assertEquals(partialInstance, eInstance)
            assertEquals(module, eModule)

            Ok(Unit)
        }

        val actual = ModuleInstantiatorImpl(
            store = store,
            module = module,
            imports = imports,
            classifier = classifier,
            pallocator = pallocator,
            allocator = allocator,
            validator = validator,
            invoker = invoker,
            evaluator = evaluator,
            tableInitializer = tableInitializer,
            memoryInitializer = memoryInitializer,
        )

        assertEquals(Ok(partialInstance), actual)
    }
}
