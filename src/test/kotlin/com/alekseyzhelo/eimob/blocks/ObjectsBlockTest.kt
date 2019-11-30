package com.alekseyzhelo.eimob.blocks

import com.alekseyzhelo.eimob.MobFile
import com.alekseyzhelo.eimob.MobVisitor
import com.alekseyzhelo.eimob.objects.MobMapEntity
import io.kotlintest.matchers.collections.shouldNotContain
import io.kotlintest.matchers.types.shouldBeSameInstanceAs
import io.kotlintest.matchers.types.shouldNotBeSameInstanceAs
import io.kotlintest.matchers.withClue
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import java.io.File


@ExperimentalUnsignedTypes
internal class ObjectsBlockTest : StringSpec() {

    init {
        val testMob = getTestMob()
        val objects = testMob.objectsBlock

        "check zone2 object counts" {
            (objects != null) shouldBe true
            objects!!.units.size shouldBe 15
            objects.objects.size shouldBe 713
            objects.levers.size shouldBe 4
            objects.traps.size shouldBe 7
            objects.flames.size shouldBe 1
            objects.lights.size shouldBe 1
            objects.sounds.size shouldBe 2
            objects.particles.size shouldBe 3
        }

        "clone test" {
            testClone(objects!!)
            testClone(testMob.scriptBlock!!)
            testClone(testMob.diplomacyBlock!!)
            testClone(testMob.worldSetBlock!!)
            testClone(objects.units[0])
            testClone(objects.objects[0])
            testClone(objects.levers[0])
            testClone(objects.traps[0])
            testClone(objects.flames[0])
            testClone(objects.lights[0])
            testClone(objects.sounds[0])
            testClone(objects.particles[0])
        }

        "add entity test" {
            val added = objects!!.addEntity(objects.units[0])
            objects.units.indexOf(added) shouldBe objects.units.size - 1
            objects.getEntityById(added.id) shouldBe added
            objects.units.size shouldBe 16
        }

        "remove entity test" {
            val toRemove = objects!!.units[objects.units.size - 1]
            objects.removeEntity(toRemove) shouldBe true
            objects.units shouldNotContain toRemove
            objects.getEntityById(toRemove.id) shouldBe null
            objects.units.size shouldBe 15
        }

        "remove entity by ID test" {
            val toRemove = objects!!.units[0]
            objects.removeEntityById(toRemove.id) shouldBe true
            objects.units shouldNotContain toRemove
            objects.getEntityById(toRemove.id) shouldBe null
            objects.units.size shouldBe 14
        }

        "check test mob registry consistency after modifications" {
            testIdRegistry(objects!!)
        }

        "reload test mob and check registry consistency" {
            val freshTestMobObjects = getTestMob().objectsBlock!!
            freshTestMobObjects.verifyIds()
            testIdRegistry(freshTestMobObjects)
        }
    }

    private fun getTestMob(): MobFile {
        val mobResource = this.javaClass.getResource("/zone2.mob")
        return MobFile(File(mobResource.path).absolutePath)
    }

    private fun testClone(block: Block) {
        val clone = block.clone()
        withClue(block.javaClass.name) {
            clone shouldNotBeSameInstanceAs block
            clone.signature shouldBe block.signature
            clone.getSize() shouldBe block.getSize()
            (clone == block) shouldBe true
        }
    }

    private fun testIdRegistry(objects: ObjectsBlock) {
        objects.acceptChildren(object : MobVisitor() {
            private val idSet = HashSet<Int>()
            override fun visitMobMapEntity(value: MobMapEntity) {
                withClue("IDs should be unique") { idSet shouldNotContain value.id }
                withClue("entity should be found by its ID") {
                    value shouldBeSameInstanceAs objects.getEntityById(value.id)
                }
                idSet.add(value.id)
            }
        })
    }
}