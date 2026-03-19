package io.github.charlietap.chasm.type.matching

interface TypeMatcherContext {
    val lookup: DefinedTypeLookup
}

object EmptyTypeMatcherContext : TypeMatcherContext {
    override val lookup: DefinedTypeLookup = { index ->
        throw IllegalStateException("Defined type lookup on empty matcher context")
    }
}
