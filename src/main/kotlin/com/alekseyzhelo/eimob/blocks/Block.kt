package com.alekseyzhelo.eimob.blocks

import com.alekseyzhelo.eimob.MobVisitor
import com.alekseyzhelo.eimob.readMobEntry
import com.alekseyzhelo.eimob.writeMobEntryHeader
import loggersoft.kotlin.streams.StreamInput
import loggersoft.kotlin.streams.StreamOutput

@ExperimentalUnsignedTypes
interface Block {
    val signature: UInt

    fun getSize(): Int
    fun serialize(out: StreamOutput) = out.writeMobEntryHeader(signature, getSize())
    fun accept(visitor: MobVisitor)
    fun acceptChildren(visitor: MobVisitor) {}

    companion object {
        const val SIG_SCRIPT = 0xACCEECCBu
        const val SIG_SCRIPT_PLAIN_TEXT = 0xACCEECCAu
        const val SIG_DIPLOMACY = 0xDDDDDDD1u
        const val SIG_WORLD_SET = 0x0000ABD0u
        const val SIG_OBJECTS = 0x0000B000u
        const val SIG_AIGRAPH = 0x31415926u

        fun createBlock(input: StreamInput): Block {
            val (signature, bytes) = input.readMobEntry()
            return when (signature) {
                SIG_SCRIPT -> EncryptedScriptBlock(bytes)
                SIG_SCRIPT_PLAIN_TEXT -> PlainTextScriptBlock(bytes)
                SIG_DIPLOMACY -> DiplomacyBlock(bytes)
                SIG_WORLD_SET -> WorldSetBlock(bytes)
                SIG_OBJECTS -> ObjectsBlock(bytes)
                else -> UnknownBlock(signature, bytes)
            }
        }
    }
}