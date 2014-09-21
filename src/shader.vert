#version 330

uniform mat4 vertTranslation;
uniform mat4 vertRotation;
uniform mat4 vertScale;

uniform mat4 vertProjection;

in vec2 vertPosition;
in vec4 vertColor;
in vec2 vertTexcoord;

out vec4 fragColor;
out vec2 fragTexcoord;

void main()
{
	mat4 model = vertTranslation * vertRotation * vertScale;

	gl_Position = vertProjection * model * vec4(vertPosition, 0.0, 1.0);
	fragColor = vertColor;
	fragTexcoord = vertTexcoord;
}
