/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.usp.icmc.vicg.gl.ex;

import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.SimpleModel;
import br.usp.icmc.vicg.gl.model.Sphere;
import br.usp.icmc.vicg.gl.model.WiredCube;
import br.usp.icmc.vicg.gl.util.Shader;
import br.usp.icmc.vicg.gl.util.ShaderFactory;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 *
 * @author AnaLima
 */
public class SimpleScene implements GLEventListener {

    private Shader shader;
    private SimpleModel cube;
    private Matrix4 modelMatrix;
    private SimpleModel sphere;
    private JWavefrontObject object;
    private float i;
    private float j;
    private float k;
    private float x;
    private float ylua;
    private float xlua;
    private float y;
    private float xcometa;
    private float ycometa;
    private float xcometa2;
    private float ycometa2;
    private float xcometa3;
    private float ycometa3;
    private int inc;
    private float max;
    int color_handle;
    private float [][] v = new float [2][20];
    private int l;
    private final JWavefrontObject model;
    
    public SimpleScene(){
        shader = ShaderFactory.getInstance(ShaderFactory.ShaderType.MODEL_MATRIX_SHADER);
        //cube = new WiredCube();
        modelMatrix = new Matrix4();
        //sphere = new Sphere();
        object = new JWavefrontObject(new File("./data/al.obj"));
        i = 0.0f;
        j = 0.0f;
        k = 0.1f;
        x = -0.5f;
        y = -0.5f;
        xcometa = ycometa = xcometa2 = ycometa2 = xcometa3 = ycometa3 = -0.5f;
        color_handle = 1;
        xlua = -0.5f;
        ylua = -0.5f;
        inc = 0;
        max = 1.0f;
        Random gerador = new Random();
        for (l=0; l<20; l++){
            v[0][l] = (float) (gerador.nextDouble()*2.0) - 1.0f;
            System.out.println(v[0][l]);
            v[1][l] = (float) (gerador.nextDouble()*2.0) - 1.0f;
            System.out.println(v[1][l]);
        }
        model = new JWavefrontObject(new File("./data/al.obj"));
    }
    
    @Override
    public void init(GLAutoDrawable glad) {
        GL3 gl = glad.getGL().getGL3();
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        
        shader.init(gl);
        shader.bind();
        
        cube.init(gl, shader);
        
        
        
        sphere.init(gl, shader);
        try {
            object.init(gl, shader);
        } catch (IOException ex) {
            Logger.getLogger(SimpleScene.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            //init the model
            model.init(gl, shader);
            model.unitize();
            model.dump();
        } catch (IOException ex) {
            Logger.getLogger(SimpleScene.class.getName()).log(Level.SEVERE, null, ex);
        }
        modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
        
        color_handle = shader.getUniformLocation("u_color");
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
        model.dispose();
    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL3 gl = glad.getGL().getGL3();
        
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        
       //        projectionMatrix.loadIdentity();
//    projectionMatrix.ortho(
//            -delta, delta, 
//            -delta, delta, 
//            -2 * delta, 2 * delta);
//    projectionMatrix.bind();

    modelMatrix.loadIdentity();
    modelMatrix.rotate(0, 0, 1.0f, 0);
    modelMatrix.rotate(0, 1.0f, 0, 0);
    modelMatrix.bind();

//    viewMatrix.loadIdentity();
//    viewMatrix.lookAt(
//            1, 1, 1, 
//            0, 0, 0, 
//            0, 1, 0);
//    viewMatrix.bind();

    //light.bind();
        model.draw();
        gl.glUniform3f(color_handle, 1.0f, 1.0f, 0.0f);
        i += 0.1f;
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.rotate(60*i, 1, 1, 0);
        modelMatrix.scale(0.1f, 0.1f, 0.1f);
        modelMatrix.bind();
        
        sphere.bind();
        sphere.draw();
        
        gl.glUniform3f(color_handle, (255.0f/255.0f), (255.0f/255.0f), (255.0f/255.0f));
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(0.8f, 0.8f, 0);
        modelMatrix.rotate(15*j, 1, 1, 0);
        modelMatrix.scale(0.01f, 0.01f, 0.01f);
        modelMatrix.bind();
        
        cube.bind();
        cube.draw();
        
        int l;
        for (l=0; l<20; l++){
            modelMatrix.loadIdentity();
            modelMatrix.rotate(30, 1, 0, 0);
            modelMatrix.translate(v[0][l], v[1][l], 0);
            modelMatrix.rotate(15*j, 1, 1, 0);
            modelMatrix.scale(0.01f, 0.01f, 0.01f);
            modelMatrix.bind();

            cube.bind();
            cube.draw();
        }
        
        
        
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(-0.8f, -0.8f, 0);
        modelMatrix.rotate(15*j, 1, 1, 0);
        modelMatrix.scale(0.01f, 0.01f, 0.01f);
        modelMatrix.bind();
        
        cube.bind();
        cube.draw();
        
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(0.0f, -0.8f, 0);
        modelMatrix.rotate(15*j, 1, 1, 0);
        modelMatrix.scale(0.01f, 0.01f, 0.01f);
        modelMatrix.bind();
        
        cube.bind();
        cube.draw();
        
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(0.05f, 0.4f, 0);
        modelMatrix.rotate(15*j, 1, 1, 0);
        modelMatrix.scale(0.01f, 0.01f, 0.01f);
        modelMatrix.bind();
        
        cube.bind();
        cube.draw();
        
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(0.05f, 0.4f, 0);
        modelMatrix.rotate(15*j, 1, 1, 0);
        modelMatrix.scale(0.01f, 0.01f, 0.01f);
        modelMatrix.bind();
        
        cube.bind();
        cube.draw();
        
        
        
        
        
        
        j+=0.1;
        x = ((float) Math.cos(j*Math.PI/40)) * 0.5f;
        y = ((float) Math.sin(j*Math.PI/40)) * 0.5f;
        x *= 0.7f;
        
        gl.glUniform3f(color_handle, 0.0f, 1.0f, 0.0f);
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(x, y, 0);
        modelMatrix.rotate(30*j, 1, 1, 0);
        modelMatrix.scale(0.05f, 0.05f, 0.05f);
        modelMatrix.bind();
        
        sphere.bind();
        sphere.draw();
        
        
        
        j+=0.1;
        xlua = ((float) Math.cos(j*Math.PI/15)) * 0.1f;
        ylua = ((float) Math.sin(j*Math.PI/15)) * 0.1f;
        
        gl.glUniform3f(color_handle, 1.0f, 1.0f, 1.0f);
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(x+xlua, y+ylua, 0);
        modelMatrix.rotate(30*j, 1, 1, 0);
        modelMatrix.scale(0.02f, 0.02f, 0.02f);
        modelMatrix.bind();
        
        sphere.bind();
        sphere.draw();
        
        x = ((float) Math.cos(j*Math.PI/80)) * 0.7f;
        y = ((float) Math.sin(j*Math.PI/80)) * 0.7f;
        
        gl.glUniform3f(color_handle, (255.0f/255.0f), (153.0f/255.0f), (51.0f/255.0f));
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(x, y, 0);
        modelMatrix.rotate(15*j, 1, 1, 0);
        modelMatrix.scale(0.03f, 0.03f, 0.03f);
        modelMatrix.bind();
        
        sphere.bind();
        sphere.draw();
        
        
        x = ((float) Math.cos(j*Math.PI/100)) * 0.9f;
        y = ((float) Math.sin(j*Math.PI/100)) * 0.9f;
        y *= 0.8;
        //laranja
        gl.glUniform3f(color_handle, (255.0f/255.0f), (51.0f/255.0f), (255.0f/255.0f));
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(x, y, 0);
        modelMatrix.rotate(30*j, 1, 1, 0);
        modelMatrix.scale(0.015f, 0.015f, 0.015f);
        modelMatrix.bind();
        
        sphere.bind();
        sphere.draw();
        
        gl.glUniform3f(color_handle, (255.0f/255.0f), (51.0f/255.0f), (255.0f/255.0f));
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(x, y, 0);
        modelMatrix.rotate(30*j, 1, 1, 0);
        modelMatrix.scale(0.025f, 0.0f, 0.025f);
        modelMatrix.bind();
        
        sphere.bind();
        sphere.draw();
        
        k += 0.1;
        xcometa = ((float) Math.cos((k)*Math.PI/100)) * 0.9f;
        ycometa = ((float) Math.sin((k)*Math.PI/100)) * 0.9f;
        ycometa -= 0.5;
        xcometa -= 0.5;
        
        gl.glUniform3f(color_handle, (255.0f/255.0f), (255.0f/255.0f), (255.0f/255.0f));
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(xcometa, ycometa, 0);
        modelMatrix.rotate(30*j, 1, 1, 0);
        modelMatrix.scale(0.015f, 0.015f, 0.015f);
        modelMatrix.bind();
        
        cube.bind();
        cube.draw();
        
        xcometa2 = ((float) Math.cos((k-0.5f)*Math.PI/100)) * 0.9f;
        ycometa2 = ((float) Math.sin((k-0.5f)*Math.PI/100)) * 0.9f;
        ycometa2 -= 0.5;
        xcometa2 -= 0.5;
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(xcometa2, ycometa2, 0);
        modelMatrix.rotate(15*j, 1, 1, 0);
        modelMatrix.scale(0.01f, 0.01f, 0.01f);
        modelMatrix.bind();
        
        cube.bind();
        cube.draw();
        
        xcometa3 = ((float) Math.cos((k-0.8f)*Math.PI/100)) * 0.9f;
        ycometa3 = ((float) Math.sin((k-0.8f)*Math.PI/100)) * 0.9f;
        ycometa3 -= 0.5;
        xcometa3 -= 0.5;
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(xcometa3, ycometa3, 0);
        modelMatrix.rotate(15*j, 1, 1, 0);
        modelMatrix.scale(0.005f, 0.005f, 0.005f);
        modelMatrix.bind();
        
        cube.bind();
        cube.draw();
        

    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {}
    
}
