package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Ok
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.ext.toLong
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.stack.LabelStackDepths
import io.github.charlietap.chasm.fixture.ast.type.functionHeapType
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import io.github.charlietap.chasm.fixture.ast.type.i64ValueType
import io.github.charlietap.chasm.fixture.ast.type.resultType
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.stack.cstack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.stack.label
import io.github.charlietap.chasm.fixture.executor.runtime.stack.vstack
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.nullReferenceValue
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals

class WasmFunctionCallTest {

    @Test
    fun `can execute a function call and return a result`() {

        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        val functionType = functionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
            results = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
        )
        val definedType = functionType.definedType()

        val function = runtimeFunction(
            locals = longArrayOf(
                nullReferenceValue(
                    heapType = functionHeapType(),
                ).toLong(),
            ),
            body = runtimeExpression(
                arrayOf(
                    dispatchableInstruction(),
                    dispatchableInstruction(),
                ),
            ),
        )

        val functionInstance = FunctionInstance.WasmFunction(
            type = definedType,
            functionType = functionType,
            module = moduleInstance(),
            function = function,
        )

        val label = label(
            arity = functionType.params.types.size,
            depths = LabelStackDepths(
                instructions = 1,
                labels = 0,
                values = 3,
            ),
        )

        vstack.pushI32(1)
        vstack.pushI32(2)

        val frame = frame(
            arity = functionType.results.types.size,
            instance = functionInstance.module,
        )
        val frameDispatchable = dispatchableInstruction()
        val expectedFrameInstruction = frameDispatchable

        val instructionBlockExecutor: InstructionBlockExecutor = { _stack, _label, _instructions, _handler ->
            assertEquals(cstack, _stack)
            assertEquals(label, _label)
            assertEquals(function.body.instructions, _instructions)

            assertEquals(frame, cstack.peekFrame())
            assertEquals(expectedFrameInstruction, cstack.peekInstructionOrNull())
            assertEquals(1, cstack.framesDepth())

            Ok(Unit)
        }

        val actual = WasmFunctionCall(
            context = context,
            instance = functionInstance,
            instructionBlockExecutor = instructionBlockExecutor,
            frameCleaner = frameDispatchable,
        )

        assertEquals(Unit, actual)
        assertEquals(1, cstack.framesDepth())
    }

    @Test
    fun `can execute a tail recursive function call and return a result`() {

        val store = store()
        val cstack = cstack()
        val vstack = vstack()
        val context = executionContext(
            store = store,
            cstack = cstack,
            vstack = vstack,
        )

        val functionType = functionType(
            params = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
            results = resultType(
                listOf(
                    i32ValueType(),
                    i64ValueType(),
                ),
            ),
        )
        val definedType = functionType.definedType()

        val function = runtimeFunction(
            locals = longArrayOf(),
            body = runtimeExpression(
                arrayOf(
                    dispatchableInstruction(),
                    dispatchableInstruction(),
                ),
            ),
        )

        val functionInstance = FunctionInstance.WasmFunction(
            type = definedType,
            functionType = functionType,
            module = moduleInstance(),
            function = function,
        )

        vstack.pushI32(1)
        vstack.pushI32(2)

        val frame = frame(
            arity = functionType.results.types.size,
            instance = functionInstance.module,
        )

        cstack.push(frame)
        cstack.push(
            dispatchableInstruction(),
        )

        val actual = ReturnWasmFunctionCall(
            context = context,
            instance = functionInstance,
        )

        assertEquals(Unit, actual)
        assertEquals(1, cstack.framesDepth())
    }
}
