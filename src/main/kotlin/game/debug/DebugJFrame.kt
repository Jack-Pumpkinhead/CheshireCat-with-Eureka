package game.debug

import game.Primitive
import game.main.TickTimeAction
import game.main.Univ
import glm_.vec2.Vec2
import glm_.vec3.Vec3
import kotlinx.coroutines.runBlocking
import math.matrix.FirstPersonView
import math.matrix.View
import org.joml.Vector3f
import java.awt.FlowLayout
import java.util.function.Supplier
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.WindowConstants

/**
 * Created by CowardlyLion on 2020/7/2 13:00
 */
class DebugJFrame(univ: Univ) : Primitive(univ) {


    val jframe = JFrame("的八阿哥")
    val record = mutableMapOf<JLabel, suspend () -> String>()

    override suspend fun gameloop(tick: Long, timemillis: Long) {
        for ((label, text) in record) {
            label.text = text()
        }
    }

    override suspend fun initialize() {

        with(jframe) {
            defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
            setSize(400, 300)
            layout = FlowLayout()
            isVisible = true
        }

        val view: FirstPersonView = univ.matrices.fpv

        record("CameraPos") { view.pos.p }
        record("CameraSpeed") { view.pos.v }
        record("CameraForce") { view.force }
        record("mouseRot") { view.mouseRotation.rot }
        record("viewVec") { View.viewVector(view.mouseRotation.rot) }
        record("mouseScroll") { univ.window.scroll }
        record("mousePos") { univ.window.mousePos }
        record("fps") { univ.frameLoop.fps.getTPS() }
        record("tps") { univ.gameloop.tps.getTPS() }
        record("fs") { univ.frameLoop.fps.counter }
        record("ts") { univ.gameloop.tps.counter }
        record("windowSize") { univ.window.windowSize }


    }

    override suspend fun destroy() {
        jframe.dispose()
    }


    fun format(a: Any): String {
        return when (a) {
            is Float -> String.format("%.2f", a)
            is Vec2 -> String.format("(%.2f, %.2f)", a.x, a.y)
            is Vec3 -> String.format("(%.2f, %.2f, %.2f)", a.x, a.y, a.z)
            else -> a.toString()
        }
    }


    suspend fun record(name: String, a: suspend () -> Any) {
        addRecordText {
            "$name: ${format(a())} "
        }
    }

    fun addRecordText(text: suspend () -> String) {
        val label = JLabel()
        record += label to text
        jframe.contentPane.add(label)
    }


}