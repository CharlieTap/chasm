package io.github.charlietap.chasm.executor.instantiator.classification

import com.github.michaelbull.result.Result
import com.github.michaelbull.result.binding
import io.github.charlietap.chasm.executor.instantiator.classification.function.FunctionClassifier
import io.github.charlietap.chasm.executor.instantiator.classification.function.FunctionClassifierImpl
import io.github.charlietap.chasm.executor.instantiator.classification.global.GlobalClassifier
import io.github.charlietap.chasm.executor.instantiator.classification.global.GlobalClassifierImpl
import io.github.charlietap.chasm.executor.instantiator.classification.memory.MemoryClassifier
import io.github.charlietap.chasm.executor.instantiator.classification.memory.MemoryClassifierImpl
import io.github.charlietap.chasm.executor.instantiator.classification.table.TableClassifier
import io.github.charlietap.chasm.executor.instantiator.classification.table.TableClassifierImpl
import io.github.charlietap.chasm.executor.instantiator.classification.tag.TagClassifier
import io.github.charlietap.chasm.executor.runtime.error.ModuleTrapError
import io.github.charlietap.chasm.executor.runtime.instance.ExternalValue
import io.github.charlietap.chasm.executor.runtime.store.Store

internal fun ExternalValueClassifierImpl(
    store: Store,
    value: ExternalValue,
): Result<ClassifiedExternalValue, ModuleTrapError> =
    ExternalValueClassifierImpl(
        store = store,
        value = value,
        functionClassifier = ::FunctionClassifierImpl,
        tableClassifier = ::TableClassifierImpl,
        memoryClassifier = ::MemoryClassifierImpl,
        tagClassifier = ::TagClassifier,
        globalClassifier = ::GlobalClassifierImpl,
    )

internal fun ExternalValueClassifierImpl(
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
