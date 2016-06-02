/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.model;

import javax.media.opengl.GL;

/**
 *
 * @author paulovich
 */
public class Ellipse extends SimpleModel {

  private final int nr_vertices;

  public Ellipse(float multX, float multY) {
    nr_vertices = 1000;
    vertex_buffer = new float[nr_vertices * 3];
    
    int j = 0, k =0;
    float xi = ((float) Math.cos(j * Math.PI / 15)) * multX;
    float yi = ((float) Math.sin(j * Math.PI / 15)) * multY;
    float x = ((float) Math.cos(++j * Math.PI / 15)) * multX;
    float y = ((float) Math.sin(j * Math.PI / 15)) * multY;
    while (x!=xi && y!=yi){
      x = ((float) Math.cos(++j * Math.PI / 15)) * multX;
      y = ((float) Math.sin(j * Math.PI / 15)) * multY;
      float z = 0;

      vertex_buffer[k] = x;
      vertex_buffer[++k] = y;
      vertex_buffer[++k] = z;
    }
  }

  @Override
  public void draw() {
    draw(GL.GL_LINE_LOOP);
  }
}
