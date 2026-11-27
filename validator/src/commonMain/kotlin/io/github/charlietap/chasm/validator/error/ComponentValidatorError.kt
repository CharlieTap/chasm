package io.github.charlietap.chasm.validator.error

import io.github.charlietap.chasm.InternalChasmApi
import io.github.charlietap.chasm.config.ComponentFeature

@InternalChasmApi
sealed interface ComponentValidatorError {

    data class UnknownIndex(
        val sort: String,
        val index: UInt,
    ) : ComponentValidatorError

    data class SortMismatch(
        val expected: String,
        val actual: String,
    ) : ComponentValidatorError

    data class TypeMismatch(
        val expected: String? = null,
        val actual: String? = null,
    ) : ComponentValidatorError

    data class DuplicateName(val name: String) : ComponentValidatorError

    data class UnknownName(val name: String) : ComponentValidatorError

    data class InvalidName(val name: String) : ComponentValidatorError

    data class InvalidType(val reason: String) : ComponentValidatorError

    data class InvalidLiteral(val reason: String) : ComponentValidatorError

    data class InvalidAlias(val reason: String) : ComponentValidatorError

    data class InvalidInstantiation(val reason: String) : ComponentValidatorError

    data class InvalidCanonicalDefinition(val reason: String) : ComponentValidatorError

    data class InvalidValueUse(val index: UInt) : ComponentValidatorError

    data class UnconsumedValue(val index: UInt) : ComponentValidatorError

    data class FeatureDisabled(val feature: ComponentFeature) : ComponentValidatorError

    data class InvalidComponent(val reason: String) : ComponentValidatorError

    data class EmbeddedModule(val error: ModuleValidatorError) : ComponentValidatorError
}
