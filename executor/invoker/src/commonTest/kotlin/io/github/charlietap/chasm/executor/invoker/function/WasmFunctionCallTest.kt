package io.github.charlietap.chasm.executor.invoker.function

import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.expect
import io.github.charlietap.chasm.ast.type.AbstractHeapType
import io.github.charlietap.chasm.ast.type.ReferenceType
import io.github.charlietap.chasm.ast.type.ValueType
import io.github.charlietap.chasm.executor.invoker.dispatch.Dispatcher
import io.github.charlietap.chasm.executor.invoker.fixture.executionContext
import io.github.charlietap.chasm.executor.invoker.instruction.InstructionBlockExecutor
import io.github.charlietap.chasm.executor.runtime.Stack
import io.github.charlietap.chasm.executor.runtime.ext.default
import io.github.charlietap.chasm.executor.runtime.ext.pushFrame
import io.github.charlietap.chasm.executor.runtime.instance.FunctionInstance
import io.github.charlietap.chasm.executor.runtime.stack.ActivationFrame
import io.github.charlietap.chasm.executor.runtime.stack.LabelStackDepths
import io.github.charlietap.chasm.fixture.ast.module.local
import io.github.charlietap.chasm.fixture.ast.type.functionType
import io.github.charlietap.chasm.fixture.ast.type.i32ValueType
import io.github.charlietap.chasm.fixture.ast.type.i64ValueType
import io.github.charlietap.chasm.fixture.ast.type.refNullReferenceType
import io.github.charlietap.chasm.fixture.ast.type.referenceValueType
import io.github.charlietap.chasm.fixture.ast.type.resultType
import io.github.charlietap.chasm.fixture.executor.runtime.dispatch.dispatchableInstruction
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeExpression
import io.github.charlietap.chasm.fixture.executor.runtime.function.runtimeFunction
import io.github.charlietap.chasm.fixture.executor.runtime.instance.moduleInstance
import io.github.charlietap.chasm.fixture.executor.runtime.label
import io.github.charlietap.chasm.fixture.executor.runtime.stack
import io.github.charlietap.chasm.fixture.executor.runtime.stack.frame
import io.github.charlietap.chasm.fixture.executor.runtime.store
import io.github.charlietap.chasm.fixture.executor.runtime.value.i32
import io.github.charlietap.chasm.type.ext.definedType
import kotlin.test.Test
import kotlin.test.assertEquals

class WasmFunctionCallTest {

    @Test
    fun `can execute a function call and return a result`() {

        val store = store()
        val stack = stack()
        val context = executionContext(
            store = store,
            stack = stack,
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
            locals = arrayOf(
                local(
                    type = referenceValueType(
                        refNullReferenceType(AbstractHeapType.Func),
                    ),
                ),
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
                values = 0,
            ),
        )

        val params = listOf(
            i32(1),
            i32(2),
        )

        params.forEach { value ->
            stack.push(Stack.Entry.Value(value))
        }

        val locals = (
            params + function.locals.map { local ->
                local.type.default().expect { "Locals should have a default" }
            }
        ).toMutableList()

        val frame = frame(
            arity = functionType.results.types.size,
            locals = locals,
            instance = functionInstance.module,
        )
        val frameDispatchable = dispatchableInstruction()
        val frameDispatcher: Dispatcher<ActivationFrame> = { frame ->
            frameDispatchable
        }
        val expectedFrameInstruction = frameDispatchable

        val instructionBlockExecutor: InstructionBlockExecutor = { _stack, _label, _instructions, _handler ->
            assertEquals(stack, _stack)
            assertEquals(label, _label)
            assertEquals(function.body.instructions, _instructions)

            assertEquals(frame, stack.peekFrameOrNull())
            assertEquals(expectedFrameInstruction, stack.peekInstructionOrNull())
            assertEquals(1, stack.framesDepth())

            Ok(Unit)
        }

        val actual = WasmFunctionCall(
            context = context,
            instance = functionInstance,
            instructionBlockExecutor = instructionBlockExecutor,
            frameDispatcher = frameDispatcher,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.framesDepth())
    }

    @Test
    fun `can execute a tail recursive function call and return a result`() {

        val store = store()
        val stack = stack()
        val context = executionContext(
            store = store,
            stack = stack,
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
            locals = arrayOf(
                local(type = ValueType.Reference(ReferenceType.RefNull(AbstractHeapType.Func))),
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

        val params = listOf(
            i32(1),
            i32(2),
        )

        params.forEach { value ->
            stack.push(Stack.Entry.Value(value))
        }

        val locals = (
            params + function.locals.map { local ->
                local.type.default().expect { "Locals should have a default" }
            }
        ).toMutableList()

        val frame = frame(
            arity = functionType.results.types.size,
            locals = locals,
            instance = functionInstance.module,
        )

        stack.pushFrame(frame)
        stack.push(
            dispatchableInstruction(),
        )

        val actual = ReturnWasmFunctionCall(
            context = context,
            instance = functionInstance,
        )

        assertEquals(Ok(Unit), actual)
        assertEquals(1, stack.framesDepth())
    }
}
