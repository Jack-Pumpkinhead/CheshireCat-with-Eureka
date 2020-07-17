#version 460
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec3 inPosition;


layout(binding = 0) uniform Projection {
    mat4 proj;
};
layout(binding = 1) uniform View {
    mat4 view;
};
layout(binding = 2) uniform Model {
    mat4 model;
};

layout(location = 0) out vec4 modelPosition;


void main() {
    modelPosition = model * vec4(inPosition, 1.0);
    gl_Position = proj * view * modelPosition;
}