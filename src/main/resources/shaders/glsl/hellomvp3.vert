#version 460
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec3 inPosition;
layout(location = 1) in vec3 inColor;


layout(binding = 0) uniform ModelViewProjection {
    mat4 model;
    mat4 projection_view;
} mvp;

layout(location = 0) out vec3 fragColor;


void main() {
    gl_Position = mvp.projection_view * mvp.model * vec4(inPosition, 1.0);
//    gl_Position = vec4(gl_Position.x, gl_Position.y, 0.34, 1.0);
//    gl_Position = vec4(inPosition, 1.0);
    fragColor = inColor;
}