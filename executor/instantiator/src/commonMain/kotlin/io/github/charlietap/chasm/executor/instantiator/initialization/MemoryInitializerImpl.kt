package io.github.charlietap.chasm.executor.instantiator.initialization

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.instruction.Expression
import io.github.charlietap.chasm.ast.instruction.MemoryInstruction
import io.github.charlietap.chasm.ast.instruction.NumericInstruction
import io.github.charlietap.chasm.ast.module.DataSegment
import io.github.charlietap.chasm.ast.module.Module
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluator
import io.github.charlietap.chasm.executor.invoker.ExpressionEvaluatorImpl
import io.github.charlietap.chasm.executor.runtime.Arity
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun MemoryInitializerImpl(
    store: Store,
    instance: ModuleInstance,
    module: Module,
): Result<Unit, ModuleTrapError> =
    MemoryInitializerImpl(
        store = store,
        instance = instance,
        module = module,
        evaluator = ::ExpressionEvaluatorImpl,
    )

internal fun MemoryInitializerImpl(
    store: Store,
    instance: ModuleInstance,
    module: Module,
    evaluator: ExpressionEvaluator,
): Result<Unit, ModuleTrapError> = binding {

    module.dataSegments.filter { segment ->
        segment.mode is DataSegment.Mode.Active
    }.forEach { segment ->
        val mode = segment.mode as DataSegment.Mode.Active
        val size = segment.initData.size
        val expression = Expression(
            mode.offset.instructions + listOf(
                NumericInstruction.I32Const(0),
                NumericInstruction.I32Const(size),
                MemoryInstruction.MemoryInit(mode.memoryIndex, segment.idx),
                MemoryInstruction.DataDrop(segment.idx),
            ),
        )

        evaluator(store, instance, expression, Arity.Return.SIDE_EFFECT).bind()
    }
}
