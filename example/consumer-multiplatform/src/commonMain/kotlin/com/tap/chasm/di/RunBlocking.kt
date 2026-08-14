package com.tap.chasm.di

internal expect fun <T> runBlocking(block: suspend () -> T): T
