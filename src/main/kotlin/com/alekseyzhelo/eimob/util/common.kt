package com.alekseyzhelo.eimob.util

import loggersoft.kotlin.streams.*

data class Float2(var x: Float, var y: Float) {
    override fun toString(): String {
        return "x=$x, y=$y"
    }
}

data class Float3(var x: Float, var y: Float, var z: Float) {
    override fun toString(): String {
        return "x=$x, y=$y, z=$z"
    }
}

data class Float4(var x: Float, var y: Float, var z: Float, var w: Float) {
    override fun toString(): String {
        return "x=$x, y=$y, z=$z, w=$w"
    }
}

@ExperimentalUnsignedTypes
fun StreamInput.readUInt(byteOrder: ByteOrder = defaultByteOrder) = this.readInt(byteOrder).toUInt()

@ExperimentalUnsignedTypes
fun StreamOutput.writeUInt(value: UInt, byteOrder: ByteOrder = defaultByteOrder) =
    this.writeInt(value.toInt(), byteOrder)

@ExperimentalUnsignedTypes
fun ByteArray.binaryStream(size: Int = this.size, offset: Int = 0, readonly: Boolean = true) =
    StreamByteArea(ByteArea(this, size, offset), readOnly = readonly)

fun intToByteArray(value: Int): ByteArray {
    return when (nativeByteOrder) {
        ByteOrder.Unknown -> throw UnsupportedOperationException()
        ByteOrder.LittleEndian -> byteArrayOf(
            value.toByte(),
            (value ushr 8).toByte(),
            (value ushr 16).toByte(),
            (value ushr 24).toByte()
        )
        ByteOrder.BigEndian -> byteArrayOf(
            (value ushr 24).toByte(),
            (value ushr 16).toByte(),
            (value ushr 8).toByte(),
            value.toByte()
        )
    }
}