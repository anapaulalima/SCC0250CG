#version 150

uniform vec3 u_color;

out vec4 fragColor;

void main(void)
{	 
  fragColor = vec4(u_color, 1.0);
  //fragColor = vec4(1.0, 0.0, 0.0, 1.0);
}
