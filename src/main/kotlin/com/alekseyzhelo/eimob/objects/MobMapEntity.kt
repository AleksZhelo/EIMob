package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.MobException
import com.alekseyzhelo.eimob.blocks.Block
import com.alekseyzhelo.eimob.readMobEntry
import com.alekseyzhelo.eimob.util.Float3
import com.alekseyzhelo.eimob.util.IdRegistry
import loggersoft.kotlin.streams.StreamInput

@ExperimentalUnsignedTypes
// TODO: documentation
abstract class MobMapEntity internal constructor() : Block {

    var id: Int = -1
        protected set
    var name: String = ""
    var location: Float3 = Float3(-1f, -1f, -1f)

    internal fun fixId(registry: IdRegistry) {
        id = registry.takeNextId()
    }

    abstract override fun clone() : MobMapEntity

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MobMapEntity

        if (id != other.id) return false
        if (name != other.name) return false
        if (location != other.location) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + name.hashCode()
        result = 31 * result + location.hashCode()
        return result
    }

    companion object {
        const val SIG_UNIT = 0xBBBB0000u
        const val SIG_OBJECT = 0x0000B001u
        const val SIG_LEVER = 0xBBAC0000u
        const val SIG_TRAP = 0xBBAB0000u
        const val SIG_FLAME = 0xBBBF0000u
        const val SIG_LIGHT = 0x0000AA01u
        const val SIG_SOUND = 0x0000CC01u
        const val SIG_PARTICLE = 0x0000DD01u

        fun createMapEntity(input: StreamInput): MobMapEntity {
            val (subSignature, blockBytes) = input.readMobEntry()
            return when (subSignature) {
                SIG_UNIT -> MobUnit(blockBytes)
                SIG_OBJECT -> MobObject(blockBytes)
                SIG_LEVER -> MobLever(blockBytes)
                SIG_TRAP -> MobTrap(blockBytes)
                SIG_FLAME -> MobFlame(blockBytes)
                SIG_LIGHT -> MobLight(blockBytes)
                SIG_SOUND -> MobSound(blockBytes)
                SIG_PARTICLE -> MobParticle(blockBytes)
                else -> throw MobException("Unexpected data in objects block, aborting!")
            }
        }
    }
}