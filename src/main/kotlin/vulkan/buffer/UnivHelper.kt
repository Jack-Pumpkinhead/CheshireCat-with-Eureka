package vulkan.buffer

import game.main.Univ
import kool.BYTES
import vulkan.drawing.DataVI

/**
 * Created by CowardlyLion on 2020/7/22 14:06
 */

suspend fun Univ.makeVertexData_DeviceLocal(arr: FloatArray): VertexData {
    return VertexData(
        vulkan.buffer,
        vulkan.vma,
        vulkan.commands,
        arr,
        vulkan.buffer.vertexBuffer_device_local(arr),
        arr.size * Float.BYTES,
        false
    )
}
fun Univ.makeVertexData_Dynamic(arr: FloatArray): VertexData {
    return VertexData(
        vulkan.buffer,
        vulkan.vma,
        vulkan.commands,
        arr,
        vulkan.buffer.vertexBuffer(arr),
        arr.size * Float.BYTES,
        true
    )
}

suspend fun Univ.makeIndexData_DeviceLocal(arr: IntArray): IndexData {
    return IndexData(
        vulkan.buffer,
        vulkan.vma,
        vulkan.commands.copyBuffer,
        arr,
        vulkan.buffer.indexBuffer_device_local(arr),
        arr.size * Int.BYTES,
        false
    )
}
fun Univ.makeIndexData_Dynamic(arr: IntArray): IndexData {
    return IndexData(
        vulkan.buffer,
        vulkan.vma,
        vulkan.commands.copyBuffer,
        arr,
        vulkan.buffer.indexBuffer(arr),
        arr.size * Int.BYTES,
        true
    )
}

suspend fun Univ.makeDataVI(vertexData: FloatArray, indexData: IntArray): DataVI {
    return DataVI(
        makeVertexData_DeviceLocal(vertexData),
        makeIndexData_DeviceLocal(indexData)
    )
}
fun Univ.makeDataVI_Dynamic(vertexData: FloatArray, indexData: IntArray): DataVI {
    return DataVI(
        makeVertexData_Dynamic(vertexData),
        makeIndexData_Dynamic(indexData)
    )
}
