package game.loop

class TPSCounter() {

    private val timeStamp = mutableListOf<Long>()
    var counter: Long = 0
        private set

    fun record(): Long {
        timeStamp.add(System.currentTimeMillis())
        counter++
        return counter
    }

    fun getTPS(): Int {
        forget(1000)
        return timeStamp.size
    }

    fun forget(age: Long) {
        val time = System.currentTimeMillis() - age
        timeStamp.removeAll { it < time }
    }

}