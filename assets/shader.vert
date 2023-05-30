attribute vec3 a_position;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform vec3 u_cameraPosition;
uniform float u_fogStart;
uniform float u_fogEnd;
varying vec2 v_texCoords;
varying float v_fogFactor;

void main() {
	v_texCoords = a_texCoord0;
	gl_Position = u_projTrans * vec4(a_position, 1.0);
	float dist = clamp(distance(a_position, u_cameraPosition) - u_fogStart, 0.0, u_fogEnd);
	v_fogFactor = dist / u_fogEnd;
}