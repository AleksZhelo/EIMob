package com.alekseyzhelo.eimob.blocks

import com.alekseyzhelo.eimob.MobException
import com.alekseyzhelo.eimob.MobObjectsListener
import com.alekseyzhelo.eimob.MobVisitor
import com.alekseyzhelo.eimob.blocks.Block.Companion.SIG_OBJECTS
import com.alekseyzhelo.eimob.entryHeaderSize
import com.alekseyzhelo.eimob.objects.*
import com.alekseyzhelo.eimob.util.IdRegistry
import com.alekseyzhelo.eimob.util.binaryStream
import com.alekseyzhelo.eimob.util.toByteArraySkipHeader
import loggersoft.kotlin.streams.StreamOutput
import java.util.*
import kotlin.collections.ArrayList

@Suppress("MemberVisibilityCanBePrivate", "unused")
@ExperimentalUnsignedTypes
class ObjectsBlock(
    bytes: ByteArray
) : Block {

    override val signature: UInt = SIG_OBJECTS
    private val listeners: MutableList<MobObjectsListener> = ArrayList()
    private val registry by lazy { IdRegistry(children) }
    private val children = ArrayList<MobMapEntity>()
    private val unitsInner = ArrayList<MobUnit>()
    private val objectsInner = ArrayList<MobObject>()
    private val leversInner = ArrayList<MobLever>()
    private val trapsInner = ArrayList<MobTrap>()
    private val flamesInner = ArrayList<MobFlame>()
    private val lightsInner = ArrayList<MobLight>()
    private val soundsInner = ArrayList<MobSound>()
    private val particlesInner = ArrayList<MobParticle>()

    val units: List<MobUnit> = Collections.unmodifiableList(unitsInner)
    val objects: List<MobObject> = Collections.unmodifiableList(objectsInner)
    val levers: List<MobLever> = Collections.unmodifiableList(leversInner)
    val traps: List<MobTrap> = Collections.unmodifiableList(trapsInner)
    val flames: List<MobFlame> = Collections.unmodifiableList(flamesInner)
    val lights: List<MobLight> = Collections.unmodifiableList(lightsInner)
    val sounds: List<MobSound> = Collections.unmodifiableList(soundsInner)
    val particles: List<MobParticle> = Collections.unmodifiableList(particlesInner)

    init {
        with(bytes.binaryStream()) {
            while (!isEof) {
                addChild(MobMapEntity.createMapEntity(this), register = false, fireListeners = false)
            }
        }
    }

    /**
     * Force non-unique object ID reassignment
     * (otherwise deferred until the first of any of the getEntityById, add or remove entity calls).
     */
    fun verifyIds(): Unit {
        registry.toString()
    }

    fun getEntityById(id: Int) = registry.getEntityById(id)

    /**
     * Adds a copy of the given entity to this block.
     * @return the entity copy that was added to the block, possibly with updated ID.
     */
    fun addEntity(entity: MobMapEntity): MobMapEntity {
        return addChild(entity.clone(), register = true, fireListeners = true)
    }

    /**
     * @return whether the entity with the given ID was found among the block's children and removed.
     */
    fun removeEntityById(id: Int): Boolean {
        return getEntityById(id)?.let {
            removeEntity(it)
        } ?: false
    }

    /**
     * @return whether the given entity was found among the block's children and removed.
     */
    fun removeEntity(entity: MobMapEntity): Boolean = removeChild(entity)

    fun objectCount() = children.size

    override fun getSize(): Int = entryHeaderSize + children.fold(0, { acc, block -> acc + block.getSize() })

    override fun serialize(out: StreamOutput) {
        super.serialize(out)
        children.forEach { x -> x.serialize(out) }
    }

    override fun accept(visitor: MobVisitor) {
        visitor.visitObjectsBlock(this)
    }

    override fun acceptChildren(visitor: MobVisitor) {
        children.forEach { x -> x.accept(visitor) }
    }

    override fun clone(): ObjectsBlock = ObjectsBlock(toByteArraySkipHeader())

    fun addListener(listener: MobObjectsListener) = listeners.add(listener)

    fun removeListener(listener: MobObjectsListener) = listeners.remove(listener)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ObjectsBlock

        if (children != other.children) return false

        return true
    }

    override fun hashCode(): Int {
        return children.hashCode()
    }

    private fun addChild(child: MobMapEntity, register: Boolean = true, fireListeners: Boolean = true): MobMapEntity {
        when (child) {
            is MobFlame -> flamesInner.add(child)
            is MobLever -> leversInner.add(child)
            is MobLight -> lightsInner.add(child)
            is MobObject -> objectsInner.add(child)
            is MobParticle -> particlesInner.add(child)
            is MobSound -> soundsInner.add(child)
            is MobTrap -> trapsInner.add(child)
            is MobUnit -> unitsInner.add(child)
            else -> throw MobException("Unsupported object type: ${child.javaClass}")
        }
        if (register) {
            registry.registerNewEntity(child)
        }
        children.add(child)
        if (fireListeners) {
            listeners.forEach { it.onEntityAdded(child) }
        }
        return child
    }

    private fun removeChild(child: MobMapEntity): Boolean {
        val result = children.remove(child)
        if (result) {
            when (child) {
                is MobFlame -> flamesInner.remove(child)
                is MobLever -> leversInner.remove(child)
                is MobLight -> lightsInner.remove(child)
                is MobObject -> objectsInner.remove(child)
                is MobParticle -> particlesInner.remove(child)
                is MobSound -> soundsInner.remove(child)
                is MobTrap -> trapsInner.remove(child)
                is MobUnit -> unitsInner.remove(child)
                else -> throw MobException("Unsupported object type: ${child.javaClass}")
            }
            registry.registerDeleteEntity(child)
            listeners.forEach { it.onEntityRemoved(child) }
        }
        return result
    }
}