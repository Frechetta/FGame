#version 330

uniform vec4 vertColor;

in vec2 vertPosition;
in vec2 vertTexcoord;

out vec4 fragColor;
out vec2 fragTexcoord;

void main()
{
	gl_Position = vec4(vertPosition, 0.0, 1.0);
	fragColor = vertColor;
	fragTexcoord = vertTexcoord;
}
