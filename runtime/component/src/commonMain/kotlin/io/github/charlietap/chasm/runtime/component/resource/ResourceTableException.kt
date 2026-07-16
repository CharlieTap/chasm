package io.github.charlietap.chasm.runtime.component.resource

class ResourceTableException(
    val error: ResourceTableError,
) : RuntimeException(error.name)

enum class ResourceTableError {
    InvalidHandle,
    InvalidPayload,
    TypeMismatch,
    OwnershipMismatch,
    BorrowScopeMismatch,
    ResourceLent,
    CapacityExhausted,
    TableUnavailable,
}
