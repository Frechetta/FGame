#version 330

uniform sampler2D fragTexture;

in vec4 fragColor;
in vec2 fragTexcoord;

out vec4 outColor;

void main()
{
	//outColor = fragColor;
	outColor = texture2D(fragTexture, fragTexcoord) * fragColor;
}
