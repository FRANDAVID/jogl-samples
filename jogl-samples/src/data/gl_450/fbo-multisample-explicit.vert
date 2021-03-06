#version 430 core

#define POSITION	0
#define TEXCOORD	4
#define TRANSFORM0	1

precision highp float;
precision highp int;
layout(std140, column_major) uniform;

layout(binding = TRANSFORM0) uniform Transform
{
    mat4 mvp;
} transform;

layout(location = POSITION) in vec2 position;
layout(location = TEXCOORD) in vec2 texCoord;

out Block
{
    vec2 texCoord;
} outBlock;

out gl_PerVertex
{
    vec4 gl_Position;
};

void main()
{	
    outBlock.texCoord = texCoord;
    gl_Position = transform.mvp * vec4(position, float(gl_InstanceID) * 1.0 - 2.0, 1.0);
}
