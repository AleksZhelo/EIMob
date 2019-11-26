package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.*
import com.alekseyzhelo.eimob.blocks.Block
import com.alekseyzhelo.eimob.blocks.ObjectsBlock.Companion.SIG_FLAME
import com.alekseyzhelo.eimob.util.Float3
import com.alekseyzhelo.eimob.util.binaryStream
import loggersoft.kotlin.streams.StreamOutput

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class MobFlame(
    bytes: ByteArray
) : Block, MobObjectDataHolder() {

    override val signature: UInt = SIG_FLAME
    val flameOffset: Float3 // TODO: correct?
    var intensity: Float
    var sound: String

    init {
        with(bytes.binaryStream()) {
            flameOffset = readMobFloat3(SIG_FLAME_OFFSET, "Failed to read flame offset in MobFlame block")
            intensity = readMobFloat(SIG_INTENSITY, "Failed to read flame intensity in MobFlame block")
            sound = readMobString(SIG_SOUND, "Failed to read flame sound in MobFlame block")
            readObjectData(this)
        }
    }

    override fun getSize(): Int = 4 * entryHeaderSize + 12 + 4 + sound.length + getObjectDataSize()

    override fun serialize(out: StreamOutput) {
        with(out) {
            super.serialize(this)
            writeMobFloat3(SIG_FLAME_OFFSET, flameOffset)
            writeMobFloat(SIG_INTENSITY, intensity)
            writeMobString(SIG_SOUND, sound)
            writeObjectData(this)
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitMobFlame(this)
    }

    companion object {
        const val SIG_FLAME_OFFSET = 0xBBBF0002u
        const val SIG_INTENSITY = 0xBBBF0001u
        const val SIG_SOUND = 0xBBBF0003u
    }
}