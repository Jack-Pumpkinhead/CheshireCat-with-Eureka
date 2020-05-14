#version 460
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec3 inColor;

layout(location = 0) out vec3 fragColor;

layout(binding = 0) uniform MVP{
    mat4 mvp;
};

void main() {
    gl_Position = mvp * vec4(inPosition, 1.0);
    fragColor = inColor;
}