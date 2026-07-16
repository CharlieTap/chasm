package io.github.charlietap.chasm.runtime.component.error

sealed interface ComponentPreparationError : ComponentError {

    data class UnsupportedFeature(
        val feature: UnsupportedComponentFeature,
    ) : ComponentPreparationError

    data class InvalidCanonicalOptions(
        val reason: String,
    ) : ComponentPreparationError

    data class InvalidPreparedComponent(
        val reason: String,
    ) : ComponentPreparationError

    data class CanonicalLayoutUnavailable(
        val reason: String,
    ) : ComponentPreparationError
}

enum class UnsupportedComponentFeature {
    Async,
    ComponentStart,
    ComponentValue,
    CanonicalExecution,
    DynamicComponentInstantiation,
    ErrorContext,
    FixedLengthList,
    Future,
    GarbageCollectedCanonicalAbi,
    LazyCanonicalAbi,
    Map,
    Memory64,
    Stream,
    Thread,
}
