package io.github.charlietap.chasm.embedding.shapes

sealed interface ImportableType {
    data object Function : ImportableType

    data object Global : ImportableType

    data object Memory : ImportableType

    data object Table : ImportableType
}
