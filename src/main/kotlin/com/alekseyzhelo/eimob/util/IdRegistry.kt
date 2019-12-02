package com.alekseyzhelo.eimob.util

import com.alekseyzhelo.eimob.objects.MobMapEntity
import java.util.*
import kotlin.collections.HashMap

private class Gap(var start: Int, var end: Int) {
    fun contains(id: Int) = id in start..end

    fun next() = start++

    fun isValid() = start <= end

    override fun toString(): String {
        return "start=$start, end=$end"
    }
}

@ExperimentalUnsignedTypes
// TODO: tests
internal class IdRegistry(entities: Collection<MobMapEntity>) {

    private val logger by LoggerDelegate()
    private val gaps: MutableList<Gap>
    private val map: MutableMap<Int, MobMapEntity>
    private var maxId = 1

    init {
        val ids = entities.map { it.id }.sorted()
        if (ids.isNotEmpty()) {
            maxId = ids[ids.size - 1]
        }
        // TODO: test gaps
        gaps = calculateSortedSequenceGaps(ids)
        map = initIdToEntityMap(entities)
        logger.info("Id registry initialized")
    }

    internal fun getEntityById(id: Int) = map[id]

    internal fun registerNewEntity(entity: MobMapEntity) = addWithUniqueId(map, entity)

    internal fun registerDeleteEntity(entity: MobMapEntity) {
        map.remove(entity.id)
        freeId(entity.id)
    }

    private fun addWithUniqueId(
        target: MutableMap<Int, MobMapEntity>,
        entity: MobMapEntity
    ) {
        if (target.containsKey(entity.id)) {
            val oldId = entity.id
            entity.fixId(this)
            logger.warn("Duplicate id ${oldId}, reassigned to ${entity.id}")
        }
        target[entity.id] = entity
    }

    internal fun takeNextId(): Int {
        return if (gaps.isEmpty()) {
            ++maxId
        } else {
            val firstGap = gaps[0]
            val result = firstGap.next()
            if (!firstGap.isValid()) {
                gaps.remove(firstGap)
            }
            result
        }
    }

    private fun freeId(id: Int) {
        if (id == maxId) {
            maxId--
        }
        // omitting complex gap recalculation here
    }

    private fun calculateSortedSequenceGaps(ids: List<Int>): MutableList<Gap> {
        val gapsInit = LinkedList<Gap>()
        var currentGap = Gap(1, -1)

        ids.forEachIndexed { index, id ->
            if (index + 1 < ids.size) {
                if (ids[index + 1] != id + 1) {
                    if (currentGap.start == -1) {
                        currentGap.start = id + 1
                    } else {
                        if (currentGap.end == -1) {
                            currentGap.end = id - 1
                            gapsInit.addGap(currentGap)
                            currentGap = Gap(id + 1, -1)
                        }
                    }
                } else {
                    if (currentGap.start != -1) {
                        currentGap.end = id - 1
                        gapsInit.addGap(currentGap)
                        currentGap = Gap(-1, -1)
                    }
                }
            } else {
                if (currentGap.start != -1) {
                    currentGap.end = id - 1
                    gapsInit.addGap(currentGap)
                    currentGap = Gap(-1, -1)
                }
            }
        }
        return gapsInit
    }

    private fun LinkedList<Gap>.addGap(gap: Gap) {
        if (gap.isValid()) {
            add(gap)
        }
    }

    private fun initIdToEntityMap(entities: Collection<MobMapEntity>): MutableMap<Int, MobMapEntity> {
        val mapInit = HashMap<Int, MobMapEntity>(entities.size)
        entities.forEach { addWithUniqueId(mapInit, it) }
        return mapInit
    }
}