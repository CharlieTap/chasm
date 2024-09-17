package io.github.charlietap.chasm.executor.instantiator.classification

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.classification.function.FunctionClassifier
import io.github.charlietap.chasm.executor.instantiator.classification.global.GlobalClassifier
import io.github.charlietap.chasm.executor.instantiator.classification.memory.MemoryClassifier
import io.github.charlietap.chasm.executor.instantiator.classification.table.TableClassifier
import io.github.charlietap.chasm.executor.instantiator.classification.tag.TagClassifier
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store

internal typealias ExternalValueClassifier = (Store, ExternalValue) -> Result<ClassifiedExternalValue, ModuleTrapError>

internal fun ExternalValueClassifier(
    store: Store,
    value: ExternalValue,
): Result<ClassifiedExternalValue, ModuleTrapError> =
    ExternalValueClassifier(
        store = store,
        value = value,
        functionClassifier = ::FunctionClassifier,
        tableClassifier = ::TableClassifier,
        memoryClassifier = ::MemoryClassifier,
        tagClassifier = ::TagClassifier,
        globalClassifier = ::GlobalClassifier,
    )

internal fun ExternalValueClassifier(
    store: Store,
    value: ExternalValue,
    functionClassifier: FunctionClassifier,
    tableClassifier: TableClassifier,
    memoryClassifier: MemoryClassifier,
    tagClassifier: TagClassifier,
    globalClassifier: GlobalClassifier,
): Result<ClassifiedExternalValue, ModuleTrapError> = binding {
    when (value) {
        is ExternalValue.Function -> functionClassifier(store, value).bind()
        is ExternalValue.Table -> tableClassifier(store, value).bind()
        is ExternalValue.Memory -> memoryClassifier(store, value).bind()
        is ExternalValue.Tag -> tagClassifier(store, value).bind()
        is ExternalValue.Global -> globalClassifier(store, value).bind()
    }
}
