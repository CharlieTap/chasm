package io.github.charlietap.chasm.compiler.driver

sealed interface SourceFailure {

    val message: String

    data class Unreadable(override val message: String) : SourceFailure

    data class Undecodable(override val message: String) : SourceFailure

    data class Invalid(override val message: String) : SourceFailure

    data class OverLimit(override val message: String) : SourceFailure

    data class Unsupported(override val message: String) : SourceFailure
}
