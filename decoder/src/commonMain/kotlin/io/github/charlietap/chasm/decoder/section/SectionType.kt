package io.github.charlietap.chasm.decoder.section

internal enum class SectionType(val id: UByte) {
    Custom(0x00u),
    Type(0x01u),
    Import(0x02u),
    Function(0x03u),
    Table(0x04u),
    Memory(0x05u),
    Tag(0x0Du),
    Global(0x06u),
    Export(0x07u),
    Start(0x08u),
    Element(0x09u),
    DataCount(0x0Cu),
    Code(0x0Au),
    Data(0x0Bu),
}
