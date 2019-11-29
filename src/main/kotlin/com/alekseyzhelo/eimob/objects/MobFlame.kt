package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.*
import com.alekseyzhelo.eimob.blocks.WorldSetBlock
import com.alekseyzhelo.eimob.util.Float3
import com.alekseyzhelo.eimob.util.binaryStream
import loggersoft.kotlin.streams.StreamOutput

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class MobFlame(
    bytes: ByteArray
) : MobObjectBase() {

    override val signature: UInt = SIG_FLAME
    var flameOffset: Float3 // TODO: correct?
    var intensity: Float
    var sound: String

    init {
        with(bytes.binaryStream()) {
            flameOffset = readMobFloat3(SIG_FLAME_OFFSET, "Failed to read flame offset in MobFlame block")
            intensity = readMobFloat(SIG_INTENSITY, "Failed to read flame intensity in MobFlame block")
            sound = readMobString(SIG_SOUND, "Failed to read flame sound in MobFlame block")
            readCommonObjectData(this)
        }
    }

    override fun getSize(): Int = 4 * entryHeaderSize + 12 + 4 + sound.length + getCommonObjectDataSize()

    override fun serialize(out: StreamOutput) {
        with(out) {
            super.serialize(this)
            writeMobFloat3(SIG_FLAME_OFFSET, flameOffset)
            writeMobFloat(SIG_INTENSITY, intensity)
            writeMobString(SIG_SOUND, sound)
            writeCommonObjectData(this)
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitMobFlame(this)
    }

    override fun clone(): MobFlame = MobFlame(toByteArray())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as MobFlame

        if (flameOffset != other.flameOffset) return false
        if (intensity != other.intensity) return false
        if (sound != other.sound) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + flameOffset.hashCode()
        result = 31 * result + intensity.hashCode()
        result = 31 * result + sound.hashCode()
        return result
    }

    companion object {
        const val SIG_FLAME_OFFSET = 0xBBBF0002u
        const val SIG_INTENSITY = 0xBBBF0001u
        const val SIG_SOUND = 0xBBBF0003u
    }
}