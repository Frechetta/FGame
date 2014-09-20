#version 330

uniform mat4 vertModel;

in vec2 vertPosition;
in vec4 vertColor;
in vec2 vertTexcoord;

out vec4 fragColor;
out vec2 fragTexcoord;

void main()
{
	gl_Position = vertModel * vec4(vertPosition, 0.0, 1.0);
	fragColor = vertColor;
	fragTexcoord = vertTexcoord;
}
