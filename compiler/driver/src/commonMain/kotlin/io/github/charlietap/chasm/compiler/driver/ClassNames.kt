package io.github.charlietap.chasm.compiler.driver

import io.github.charlietap.chasm.InternalChasmApi

@InternalChasmApi
class ClassNames {

    fun valid(name: String): Boolean {
        return name.split('.').all { segment ->
            segment.isNotEmpty() &&
                (segment[0].isJavaIdentifierStart()) &&
                segment.all { character -> character.isJavaIdentifierPart() && character != '$' } &&
                segment !in JAVA_KEYWORDS
        }
    }

    private companion object {
        val JAVA_KEYWORDS = setOf(
            "abstract",
            "assert",
            "boolean",
            "break",
            "byte",
            "case",
            "catch",
            "char",
            "class",
            "const",
            "continue",
            "default",
            "do",
            "double",
            "else",
            "enum",
            "extends",
            "false",
            "final",
            "finally",
            "float",
            "for",
            "goto",
            "if",
            "implements",
            "import",
            "instanceof",
            "int",
            "interface",
            "long",
            "native",
            "new",
            "null",
            "package",
            "private",
            "protected",
            "public",
            "return",
            "short",
            "static",
            "strictfp",
            "super",
            "switch",
            "synchronized",
            "this",
            "throw",
            "throws",
            "transient",
            "true",
            "try",
            "void",
            "volatile",
            "while",
            "_",
        )
    }
}
