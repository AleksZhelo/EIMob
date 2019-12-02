package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.*
import com.alekseyzhelo.eimob.types.Color
import com.alekseyzhelo.eimob.util.binaryStream
import com.alekseyzhelo.eimob.util.toByteArraySkipHeader
import loggersoft.kotlin.streams.StreamOutput

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class MobLight(
    bytes: ByteArray
) : MobMapEntity() {

    override val signature: UInt = SIG_LIGHT
    var showShadow: Boolean
    val color: Color
    var particleSize: Float
    var comment: String

    init {
        with(bytes.binaryStream()) {
            id = readMobInt(SIG_ID, "Failed to read id in MobLight block")
            showShadow = readMobBoolean(SIG_SHOW_SHADOW, "Failed to read showShadow in MobLight block")
            location = readMobFloat3(SIG_LOCATION, "Failed to read location in MobLight block")
            color = Color(
                readMobFloat3(
                    SIG_COLOR,
                    "Failed to read color in MobLight block"
                )
            )
            particleSize = readMobFloat(SIG_PARTICLE_SIZE, "Failed to read particleSize in MobLight block")
            name = readMobString(SIG_NAME, "Failed to read name in MobLight block")
            comment = readMobString(SIG_COMMENT, "Failed to read comment in MobLight block")
        }
    }

    override fun getSize(): Int = 8 * entryHeaderSize + 4 + 1 + 12 + 12 + 4 + name.length + comment.length

    override fun serialize(out: StreamOutput) {
        with(out) {
            super.serialize(this)
            writeMobInt(SIG_ID, id)
            writeMobBoolean(SIG_SHOW_SHADOW, showShadow)
            writeMobFloat3(SIG_LOCATION, location)
            writeMobFloat3(SIG_COLOR, color.toFloat3())
            writeMobFloat(SIG_PARTICLE_SIZE, particleSize)
            writeMobString(SIG_NAME, name)
            writeMobString(SIG_COMMENT, comment)
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitMobLight(this)
    }

    override fun clone(): MobLight = MobLight(toByteArraySkipHeader())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MobLight

        if (showShadow != other.showShadow) return false
        if (color != other.color) return false
        if (particleSize != other.particleSize) return false
        if (comment != other.comment) return false

        return true
    }

    override fun hashCode(): Int {
        var result = showShadow.hashCode()
        result = 31 * result + color.hashCode()
        result = 31 * result + particleSize.hashCode()
        result = 31 * result + comment.hashCode()
        return result
    }

    companion object {
        const val SIG_ID = 0x0000AA05u
        const val SIG_SHOW_SHADOW = 0x0000AA06u
        const val SIG_LOCATION = 0x0000AA04u
        const val SIG_COLOR = 0x0000AA07u
        const val SIG_PARTICLE_SIZE = 0x0000AA02u
        const val SIG_NAME = 0x0000AA03u
        const val SIG_COMMENT = 0x0000AA08u
    }
}