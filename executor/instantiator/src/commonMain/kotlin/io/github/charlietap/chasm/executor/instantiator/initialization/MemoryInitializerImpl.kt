package io.github.charlietap.chasm.executor.instantiator.initialization

import com.github.michaelbull.result.Err
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
import io.github.charlietap.chasm.executor.runtime.error.InstantiationError
import io.github.charlietap.chasm.executor.runtime.error.ModuleRuntimeError
import io.github.charlietap.chasm.executor.runtime.instance.ModuleInstance
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun MemoryInitializerImpl(
    store: Store,
    instance: ModuleInstance,
    module: Module,
): Result<Unit, ModuleRuntimeError> =
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
): Result<Unit, ModuleRuntimeError> = binding {

    module.dataSegments.filter { segment ->
        segment.mode is DataSegment.Mode.Active
    }.forEach { segment ->
        val mode = segment.mode as DataSegment.Mode.Active
        if (mode.memoryIndex.idx != 0u) {
            Err(InstantiationError.DataSegmentMemoryIndexNotZero).bind<ModuleInstance>()
        }
        val size = segment.initData.size
        val expression = Expression(
            mode.offset.instructions + listOf(
                NumericInstruction.I32Const(0),
                NumericInstruction.I32Const(size),
                MemoryInstruction.MemoryInit(segment.idx),
                MemoryInstruction.DataDrop(segment.idx),
            ),
        )
        evaluator(store, instance, expression, Arity.SIDE_EFFECT).bind()
    }
}
