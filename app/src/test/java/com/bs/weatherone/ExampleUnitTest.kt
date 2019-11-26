package com.bs.weatherone

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)

        val plasticDoor = PlasticDoor(5, "plastic")
        val wall = Wall(plasticDoor)
        wall.makeWall()
    }



}


class Wall(val door:Door){

    fun makeWall(){
        putWoodenBar(1)
        putWoodenBar(2)
        putDoor(door)
        putWoodenBar(3)
        putWoodenBar(4)
    }

    private fun putWoodenBar(i: Int) {
        print(i)
    }

    private fun putDoor(door: Door){
        print(door.id)
    }

}

abstract class Door(val id:Int):wallable{
    override fun getEngraving(): Int {
        return id
    }
}

interface wallable{
    fun getEngraving(): Int
}

class PlasticDoor(id:Int, val material:String):Door(id){

}

class WoodenDoor(id:Int, val material:String):Door(id){

}

