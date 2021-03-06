#version 460
#extension GL_ARB_separate_shader_objects : enable


layout(location = 0) in vec4 modelPosition;

layout(location = 0) out vec4 outColor;


void main() {
    outColor = vec4(sin(modelPosition.x),sin(modelPosition.y),sin(modelPosition.z),1);
}