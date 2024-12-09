Bolt is a gradle plugin that consumes static libraries for use in Kotlin
Native projects. Its purpose is to keep the native source sets and
toolchains out of Kotlin Multiplatform projects by downloading precompiled
artifacts.

### Usage

```kotlin
bolt {
    library = "liblinmem"
    url = "Url for static libraries"
}
```

Bolt works by convention, given a url it will look at the multiplatform
targets you have enabled and expect to find a header and a specific archive/s. Archives
can be zip or tar.

| **Kotlin Native Target**  | **LLVM Target Triple**      |
|---------------------------|-----------------------------|
| `macosArm64()`            | `aarch64-apple-darwin`      |
| `macosX64()`              | `x86_64-apple-darwin`       |
| `iosArm64()`              | `aarch64-apple-ios`         |
| `iosSimulatorArm64()`     | `aarch64-apple-ios-sim`     |
| `iosX64()`                | `x86_64-apple-ios`          |
| `linuxArm64()`            | `aarch64-unknown-linux-gnu` |
| `linuxX64()`              | `x86_64-unknown-linux-gnu`  |
| `mingwX64()`              | `x86_64-pc-windows-gnu`     |

For example if you have the following configuration:

```gradle

bolt {
    library = "libfoo"
    url = "https://bar.com/0.1/"
}

kotlin {
    iosArm64()
    linuxArm64()
}
```

Then the plugin will look for

libfoo.h<br>
libfoo-aarch64-apple-ios-sim.a.[zip/tar]<br>
libfoo-aarch64-unknown-linux-gnu.a.[zip/tar]<br>

Archives should contain one file, the static library itself i.e libfoo.a

Provided all the above is all correct then bolt will configure cinterop bindings such that you can now import libfoo
