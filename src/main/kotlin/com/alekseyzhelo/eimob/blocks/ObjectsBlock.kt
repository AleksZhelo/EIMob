package com.alekseyzhelo.eimob.blocks

import com.alekseyzhelo.eimob.MobException
import com.alekseyzhelo.eimob.MobVisitor
import com.alekseyzhelo.eimob.blocks.Block.Companion.SIG_OBJECTS
import com.alekseyzhelo.eimob.entryHeaderSize
import com.alekseyzhelo.eimob.objects.*
import com.alekseyzhelo.eimob.readMobEntry
import com.alekseyzhelo.eimob.util.binaryStream
import loggersoft.kotlin.streams.StreamOutput

@Suppress("MemberVisibilityCanBePrivate")
@ExperimentalUnsignedTypes
class ObjectsBlock(
    bytes: ByteArray
) : Block {

    override val signature: UInt = SIG_OBJECTS
    // TODO: one list of <Block> instead, units/etc as views of that?
    val units: ArrayList<MobUnit> = ArrayList()
    val objects: ArrayList<MobObject> = ArrayList()
    val levers: ArrayList<MobLever> = ArrayList()
    val traps: ArrayList<MobTrap> = ArrayList()
    val flames: ArrayList<MobFlame> = ArrayList()
    val lights: ArrayList<MobLight> = ArrayList()
    val sounds: ArrayList<MobSound> = ArrayList()
    val particles: ArrayList<MobParticle> = ArrayList()

    init {
        with(bytes.binaryStream()) {
            while (!isEof) {
                val (subSignature, blockBytes) = readMobEntry()
                when (subSignature) {
                    SIG_UNIT -> units.add(MobUnit(blockBytes))
                    SIG_OBJECT -> objects.add(MobObject(blockBytes))
                    SIG_LEVER -> levers.add(MobLever(blockBytes))
                    SIG_TRAP -> traps.add(MobTrap(blockBytes))
                    SIG_FLAME -> flames.add(MobFlame(blockBytes))
                    SIG_LIGHT -> lights.add(MobLight(blockBytes))
                    SIG_SOUND -> sounds.add(MobSound(blockBytes))
                    SIG_PARTICLE -> particles.add(MobParticle(blockBytes))
                    else -> throw MobException("Unexpected data in objects block, aborting!")
                }
            }
        }
    }

    override fun getSize(): Int = entryHeaderSize +
            units.fold(0, { acc, block -> acc + block.getSize() }) +
            objects.fold(0, { acc, block -> acc + block.getSize() }) +
            levers.fold(0, { acc, block -> acc + block.getSize() }) +
            traps.fold(0, { acc, block -> acc + block.getSize() }) +
            flames.fold(0, { acc, block -> acc + block.getSize() }) +
            lights.fold(0, { acc, block -> acc + block.getSize() }) +
            sounds.fold(0, { acc, block -> acc + block.getSize() }) +
            particles.fold(0, { acc, block -> acc + block.getSize() })

    override fun serialize(out: StreamOutput) {
        super.serialize(out)
        applyToChildren { x -> x.serialize(out) }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitObjectsBlock(this)
    }

    override fun acceptChildren(visitor: MobVisitor) {
        applyToChildren { x -> x.accept(visitor) }
    }

    private inline fun applyToChildren(f: (x: Block) -> Unit) {
        units.forEach { x -> f(x) }
        objects.forEach { x -> f(x) }
        levers.forEach { x -> f(x) }
        traps.forEach { x -> f(x) }
        flames.forEach { x -> f(x) }
        lights.forEach { x -> f(x) }
        sounds.forEach { x -> f(x) }
        particles.forEach { x -> f(x) }
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
    }
}