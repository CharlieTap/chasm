package io.github.charlietap.chasm.executor.invoker.thread

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.runtime.Configuration
import io.github.charlietap.chasm.runtime.dispatch.DispatchableInstruction
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.exception.InvocationException
import io.github.charlietap.chasm.runtime.execution.ExecutionContext
import io.github.charlietap.chasm.runtime.execution.InstructionPointer
import io.github.charlietap.chasm.runtime.ext.depth
import io.github.charlietap.chasm.runtime.ext.toLongFromBoxed
import io.github.charlietap.chasm.runtime.stack.ControlStack
import io.github.charlietap.chasm.runtime.stack.ValueStack
import io.github.charlietap.chasm.runtime.value.ExecutionValue

internal typealias ThreadExecutor = (Configuration, List<ExecutionValue>, LongArray) -> Result<List<Long>, InvocationError>

internal fun ThreadExecutor(
    configuration: Configuration,
    params: List<ExecutionValue>,
    locals: LongArray,
): Result<List<Long>, InvocationError> = binding {

    val thread = configuration.thread
    val store = configuration.store

    var instructions: Array<DispatchableInstruction> = thread.instructions

    val cstack = ControlStack()
    val vstack = ValueStack()
    val context = ExecutionContext(
        cstack = cstack,
        vstack = vstack,
        store = configuration.store,
        instance = thread.frame.instance,
        config = configuration.config,
        getInstructions = { instructions },
        setInstructions = { inst -> instructions = inst },
    )

    cstack.push(thread.frame)
    params.forEach { param ->
        vstack.push(param.toLongFromBoxed())
    }
    vstack.push(locals)

    var ip = InstructionPointer(0)

    try {
        while (ip.pointer >= 0) {
            ip = instructions[ip.pointer](ip, vstack, cstack, store, context)
        }
    } catch (exception: InvocationException) {
        Err(exception.error).bind()
    }

    if (context.depth() != thread.frame.arity) {
        Err(InvocationError.ProgramFinishedInconsistentState).bind<List<ExecutionValue>>()
    }

    List(thread.frame.arity) {
        vstack.pop()
    }.asReversed()
}
