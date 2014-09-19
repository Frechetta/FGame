#version 330

uniform sampler2D tex;

in vec4 fragColor;
in vec2 fragTexcoord;

out vec4 outColor;

void main()
{
	outColor = texture(tex, fragTexcoord) * fragColor;
}
