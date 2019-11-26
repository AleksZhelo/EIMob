package com.alekseyzhelo.eimob.objects

import com.alekseyzhelo.eimob.*
import com.alekseyzhelo.eimob.blocks.Block
import com.alekseyzhelo.eimob.blocks.ObjectsBlock.Companion.SIG_LEVER
import com.alekseyzhelo.eimob.util.binaryStream
import loggersoft.kotlin.streams.StreamOutput

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class MobLever(
    bytes: ByteArray
) : Block, MobObjectDataHolder() {

    override val signature: UInt = SIG_LEVER
    val stats: LeverStats
    // TODO: experiment with, determine which ones are Booleans (last three?)
    var curState: Byte
    var totalState: Byte
    var isCycled: Byte
    var isDoor: Byte
    var recalcGraph: Byte

    init {
        with(bytes.binaryStream()) {
            stats = readMobLeverStats(SIG_STATS, "Failed to read stats in MobLever block")
            curState = readMobByte(SIG_CUR_STATE, "Failed to read curState in MobLever block")
            totalState = readMobByte(SIG_TOTAL_STATE, "Failed to read totalState in MobLever block")
            isCycled = readMobByte(SIG_IS_CYCLED, "Failed to read isCycled in MobLever block")
            isDoor = readMobByte(SIG_IS_DOOR, "Failed to read isDoor in MobLever block")
            recalcGraph = readMobByte(SIG_RECALC_GRAPH, "Failed to read recalcGraph in MobLever block")
            readObjectData(this)
        }
    }

    override fun getSize(): Int = entryHeaderSize * 7 + 12 + 1 + 1 + 1 + 1 + 1 + getObjectDataSize()

    override fun serialize(out: StreamOutput) {
        with(out) {
            super.serialize(this)
            writeMobLeverStats(SIG_STATS, stats)
            writeMobByte(SIG_CUR_STATE, curState)
            writeMobByte(SIG_TOTAL_STATE, totalState)
            writeMobByte(SIG_IS_CYCLED, isCycled)
            writeMobByte(SIG_IS_DOOR, isDoor)
            writeMobByte(SIG_RECALC_GRAPH, recalcGraph)
            writeObjectData(this)
        }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitMobLever(this)
    }

    companion object {
        const val SIG_STATS = 0xBBAC0006u
        const val SIG_CUR_STATE = 0xBBAC0002u
        const val SIG_TOTAL_STATE = 0xBBAC0003u
        const val SIG_IS_CYCLED = 0xBBAC0004u
        const val SIG_IS_DOOR = 0xBBAC0007u
        const val SIG_RECALC_GRAPH = 0xBBAC0008u
    }

    data class LeverStats(var openType: Int, var key: Int, var skillToOpen: Int)
}
