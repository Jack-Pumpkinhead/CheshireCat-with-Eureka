#version 460
#extension GL_ARB_separate_shader_objects : enable


layout(location = 1) in vec2 fragTexCoord;

layout(set = 1, binding = 0) uniform sampler2D texSampler;
//layout(set = 1, binding = 0) uniform Sampler{
//    sampler2D texSampler;
//};


layout(location = 0) out vec4 outColor;


void main() {
//    outColor = vec4(fragTexCoord,0.0,1.0);
//    outColor = vec4(0.0,0.3,0.0,1.0);
    outColor = texture(texSampler, fragTexCoord);
}