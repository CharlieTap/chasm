package io.github.charlietap.chasm.executor.instantiator.runtime.initialization

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.declarations.ConstantInstruction
import io.github.charlietap.chasm.declarations.DataSegmentDeclaration
import io.github.charlietap.chasm.executor.instantiator.ConstantExpressionEvaluator
import io.github.charlietap.chasm.executor.instantiator.initialization.MemoryInitializer
import io.github.charlietap.chasm.fixture.ast.module.memoryIndex
import io.github.charlietap.chasm.fixture.config.runtimeConfig
import io.github.charlietap.chasm.fixture.executor.instantiator.constantExpression
import io.github.charlietap.chasm.fixture.executor.instantiator.dataSegmentDeclaration
import io.github.charlietap.chasm.fixture.executor.instantiator.instantiationContext
import io.github.charlietap.chasm.fixture.executor.instantiator.moduleDeclarations
import io.github.charlietap.chasm.fixture.runtime.instance.dataAddress
import io.github.charlietap.chasm.fixture.runtime.instance.dataInstance
import io.github.charlietap.chasm.fixture.runtime.instance.memoryAddress
import io.github.charlietap.chasm.fixture.runtime.instance.memoryInstance
import io.github.charlietap.chasm.fixture.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.runtime.store
import io.github.charlietap.chasm.memory.init.LinearMemoryInitialiser
import kotlin.test.Test
import kotlin.test.assertEquals

class MemoryInitializerTest {

    @Test
    fun `can initialize a memory on a module instance`() {

        val offset = 117
        val activeOffsetExpression = constantExpression(ConstantInstruction.I32Const(offset))
        val activeSegment = dataSegmentDeclaration(
            mode = DataSegmentDeclaration.Mode.Active(
                memoryIndex = memoryIndex(0u),
                offset = activeOffsetExpression,
            ),
        )

        val config = runtimeConfig()
        val memInstance = memoryInstance()
        val dataInst = dataInstance()
        val store = store(
            memories = mutableListOf(memInstance),
            data = mutableListOf(dataInst),
        )
        val module = moduleDeclarations(
            dataSegments = listOf(activeSegment),
        )

        val instance = moduleInstance(
            memAddresses = mutableListOf(memoryAddress(0)),
            dataAddresses = mutableListOf(dataAddress(0)),
        )
        val context = instantiationContext(
            store = store,
            module = module,
            config = config,
            instance = instance,
        )

        val constantExpressionEvaluator: ConstantExpressionEvaluator = { _store, _instance, _, _expression ->
            assertEquals(store, _store)
            assertEquals(instance, _instance)
            assertEquals(activeOffsetExpression, _expression)

            Ok(offset.toLong())
        }

        val linearMemoryInitialiser: LinearMemoryInitialiser = { _src, _dst, _srcOffset, _dstOffset, _bytesToInit, _srcUpperBound, _dstUpperBound ->
            assertEquals(0, _srcOffset)
            assertEquals(offset, _dstOffset)
            assertEquals(activeSegment.payload.size, _bytesToInit)
        }

        val actual = MemoryInitializer(
            context = context,
            instance = instance,
            constantExpressionEvaluator = constantExpressionEvaluator,
            linearMemoryInitialiser = linearMemoryInitialiser,
        )

        assertEquals(Ok(Unit), actual)
    }
}
