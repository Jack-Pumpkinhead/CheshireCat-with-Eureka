#version 460
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec4 inPosition;
layout(location = 1) in vec2 inTexCoord;

layout(location = 0) out vec2 fragTexCoord;

layout(binding = 0) uniform ModelViewProjection {
    mat4 model;
    mat4 projection_view;
} mvp;

void main() {
    gl_Position = mvp.projection_view * mvp.model * inPosition;
    fragTexCoord = inTexCoord;
}