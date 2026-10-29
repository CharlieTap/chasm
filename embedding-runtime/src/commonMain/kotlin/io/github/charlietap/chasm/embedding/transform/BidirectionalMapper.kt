package io.github.charlietap.chasm.embedding.transform

internal interface BidirectionalMapper<A, B> : Mapper<A, B> {
    fun bimap(input: B): A
}
