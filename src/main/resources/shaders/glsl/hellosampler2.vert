#version 460
#extension GL_ARB_separate_shader_objects : enable

layout(location = 0) in vec3 inPosition;
layout(location = 2) in vec2 inTexCoord;

layout(location = 1) out vec2 fragTexCoord;


layout(binding = 0) uniform Projection {
    mat4 proj;
};
layout(binding = 1) uniform View {
    mat4 view;
};
layout(binding = 2) uniform Model {
    mat4 model;
};

void main() {
    gl_Position = proj * view * model * vec4(inPosition, 1.0);
    fragTexCoord = inTexCoord;
}