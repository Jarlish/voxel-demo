uniform sampler2D u_texture;
varying vec2 v_texCoords;

void main() {
	vec4 pixel = texture2D(u_texture, v_texCoords);
	if (pixel.a <= 0.0) discard;
	gl_FragColor = pixel;
}