uniform sampler2D u_texture;
uniform vec4 u_fogColor;
varying vec2 v_texCoords;
varying float v_fogFactor;

void main() {
	vec4 pixel = texture2D(u_texture, v_texCoords);
	if (pixel.a <= 0.0) discard;
	gl_FragColor = pixel;
	gl_FragColor.rgb = mix(gl_FragColor.rgb, u_fogColor.rgb, v_fogFactor);
}