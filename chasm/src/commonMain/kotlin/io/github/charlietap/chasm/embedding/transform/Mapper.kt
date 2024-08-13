package io.github.charlietap.chasm.embedding.transform

internal interface Mapper<in A, out B> {
    fun map(input: A): B
}
