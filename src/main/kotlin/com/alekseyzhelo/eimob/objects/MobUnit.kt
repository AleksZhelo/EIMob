@file:Suppress("MemberVisibilityCanBePrivate")

package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.*
import com.alekseyzhelo.eimob.blocks.Block
import com.alekseyzhelo.eimob.blocks.ObjectsBlock.Companion.SIG_UNIT
import com.alekseyzhelo.eimob.blocks.UnknownBlock
import com.alekseyzhelo.eimob.util.binaryStream
import loggersoft.kotlin.streams.StreamOutput

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class MobUnit(
    bytes: ByteArray
) : Block, MobObjectDataHolder() {

    override val signature: UInt = SIG_UNIT
    override var nPlayer: Byte = super.nPlayer
        set(value) {
            require(value in 0..31) { "Bad player number: $value" }
            field = value
        }
    var useMobInfo: Boolean
    var prototype: String
    val armors: Array<String>
    val weapons: Array<String>
    val spells: Array<String>
    val quickItems: Array<String>
    val questItems: Array<String>
    val stats: MobUnitStats
    val behaviours: ArrayList<UnknownBlock> = ArrayList()

    init {
        with(bytes.binaryStream()) {
            useMobInfo = readMobBoolean(SIG_USE_MOB_INFO, "Failed to read useMobInfo in MobUnit block")
            prototype = readMobString(SIG_PROTOTYPE, "Failed to read prototype in MobUnit block")
            armors = readMobStringArray(
                SIG_ARMORS, "Failed to read armors in MobUnit block",
                "Unexpected signature in armors array"
            )
            weapons = readMobStringArray(
                SIG_WEAPONS, "Failed to read weapons in MobUnit block",
                "Unexpected signature in weapons array"
            )
            spells = readMobStringArray(
                SIG_SPELLS, "Failed to read spells in MobUnit block",
                "Unexpected signature in spells array"
            )
            quickItems = readMobStringArray(
                SIG_QUICK_ITEMS, "Failed to read quick items in MobUnit block",
                "Unexpected signature in quick items array"
            )
            questItems = readMobStringArray(
                SIG_QUEST_ITEMS, "Failed to read quest items in MobUnit block",
                "Unexpected signature in quest items array"
            )
            with(readMobEntry()) {
                testSignature(
                    first,
                    SIG_STATS,
                    "Unexpected data in unit stats block, aborting!"
                )
                stats = MobUnitStats(first, second)
            }
            readObjectData(this)
            while (!isEof) {
                val (signature, blockBytes) = readMobEntry()
                testSignature(
                    signature,
                    SIG_UNIT_BEHAVIOUR,
                    "Unknown data in unit block tail, aborting!"
                )
                behaviours.add(UnknownBlock(signature, blockBytes))
            }
        }
    }

    override fun getSize(): Int = 8 * entryHeaderSize + 1 + prototype.length +
            armors.mobEntrySize() + weapons.mobEntrySize() + spells.mobEntrySize() +
            quickItems.mobEntrySize() + questItems.mobEntrySize() + stats.getSize() +
            getObjectDataSize() + behaviours.fold(0, { acc, block -> acc + block.getSize() })

    override fun serialize(out: StreamOutput) {
        with(out) {
            super.serialize(this)
            writeMobBoolean(SIG_USE_MOB_INFO, useMobInfo)
            writeMobString(SIG_PROTOTYPE, prototype)
            writeMobStringArray(SIG_ARMORS, armors)
            writeMobStringArray(SIG_WEAPONS, weapons)
            writeMobStringArray(SIG_SPELLS, spells)
            writeMobStringArray(SIG_QUICK_ITEMS, quickItems)
            writeMobStringArray(SIG_QUEST_ITEMS, questItems)
            stats.serialize(this)
            writeObjectData(this)
            behaviours.forEach { it.serialize(this) }
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitMobUnit(this)
    }

    companion object {
        const val SIG_USE_MOB_INFO = 0xBBBB000Au
        const val SIG_PROTOTYPE = 0xBBBB0002u
        const val SIG_ARMORS = 0xBBBB0009u
        const val SIG_WEAPONS = 0xBBBB0008u
        const val SIG_SPELLS = 0xBBBB0007u
        const val SIG_QUICK_ITEMS = 0xBBBB0006u
        const val SIG_QUEST_ITEMS = 0xBBBB0005u
        const val SIG_STATS = 0xBBBB0004u
        const val SIG_UNIT_BEHAVIOUR = 0xBBBC0000u
    }
}
