package io.github.charlietap.chasm.executor.instantiator.initialization

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.ast.module.toInt
import io.github.charlietap.chasm.declarations.ElementSegmentDeclaration
import io.github.charlietap.chasm.executor.instantiator.ConstantExpressionEvaluator
import io.github.charlietap.chasm.executor.instantiator.context.InstantiationContext
import io.github.charlietap.chasm.executor.instantiator.ext.elementAddress
import io.github.charlietap.chasm.executor.instantiator.ext.tableAddress
import io.github.charlietap.chasm.runtime.error.InvocationError
import io.github.charlietap.chasm.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.runtime.ext.element
import io.github.charlietap.chasm.runtime.ext.table
import io.github.charlietap.chasm.runtime.instance.ModuleInstance

internal typealias TableInitializer = (InstantiationContext, ModuleInstance) -> Result<Unit, ModuleTrapError>

internal fun TableInitializer(
    context: InstantiationContext,
    instance: ModuleInstance,
): Result<Unit, ModuleTrapError> =
    TableInitializer(
        context = context,
        instance = instance,
        constantExpressionEvaluator = ::ConstantExpressionEvaluator,
    )

internal inline fun TableInitializer(
    context: InstantiationContext,
    instance: ModuleInstance,
    crossinline constantExpressionEvaluator: ConstantExpressionEvaluator,
): Result<Unit, ModuleTrapError> = binding {

    val store = context.store
    val segments = context.module.elementSegments

    segments.forEachIndexed { segmentIndex, segment ->
        val mode = segment.mode as? ElementSegmentDeclaration.Mode.Active ?: return@forEachIndexed
        val offset = constantExpressionEvaluator(store, instance, context.types, mode.offsetExpr).bind().toInt()
        val tableAddress = instance.tableAddress(mode.tableIndex.toInt()).bind()
        val tableInstance = store.table(tableAddress)
        val elementAddress = instance.elementAddress(segmentIndex).bind()
        val elementInstance = store.element(elementAddress)
        val elementsToInit = segment.initExpressions.size

        try {
            elementInstance.elements.copyInto(tableInstance.elements, offset, 0, elementsToInit)
        } catch (_: IndexOutOfBoundsException) {
            Err(InvocationError.TableOperationOutOfBounds).bind<Unit>()
        } catch (_: IllegalArgumentException) {
            Err(InvocationError.TableOperationOutOfBounds).bind<Unit>()
        }

        elementInstance.elements = longArrayOf()
    }

    segments.forEachIndexed { segmentIndex, segment ->
        if (segment.mode != ElementSegmentDeclaration.Mode.Declarative) return@forEachIndexed
        val elementAddress = instance.elementAddress(segmentIndex).bind()
        val elementInstance = store.element(elementAddress)
        elementInstance.elements = longArrayOf()
    }
}
