package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.core.Material;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
import br.usp.icmc.vicg.gl.model.Ellipse;
import br.usp.icmc.vicg.gl.model.WiredCube;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL3;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;

import br.usp.icmc.vicg.gl.util.Shader;
import br.usp.icmc.vicg.gl.util.ShaderFactory;
import br.usp.icmc.vicg.gl.util.ShaderFactory.ShaderType;

import com.jogamp.opengl.util.AnimatorBase;
import com.jogamp.opengl.util.FPSAnimator;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Point3D;

public class Example12 extends KeyAdapter implements GLEventListener {
    
    private final Shader shader; // Gerenciador dos shaders
    private final Matrix4 modelMatrix;
    private final Matrix4 projectionMatrix;
    private final Matrix4 viewMatrix;
    //private final JWavefrontObject model;
    private final Vector<JWavefrontObject> objVector = new Vector<JWavefrontObject>(50);
    private final Light light;
    private float alpha;
    private float beta;
    private float delta;
    private float i;
    private float j;
    private float x;
    private float y;
    private float z;
    private final float [] starPoints;
    private final Vector<Point3D> lastPoints = new Vector<Point3D>(50);
    private final Vector<Ellipse> ellipses = new Vector<Ellipse>(50);
    private float xlua;
    private float ylua;
    private float epslon;
    private float gama;
    private final Material material;
    private float mult;
    private int nstar;
    private final WiredCube star;
    
    public Example12() {
        // Carrega os shaders
        shader = ShaderFactory.getInstance(ShaderType.COMPLETE_SHADER);
        modelMatrix = new Matrix4();
        projectionMatrix = new Matrix4();
        viewMatrix = new Matrix4();
        
        //model = new JWavefrontObject(new File("./data/planet/AlienPlanet/p1/AlienPlanet.obj"));
        objVector.add(new JWavefrontObject(new File("./data/planet/AlienPlanet/p1/AlienPlanet.obj")));
        objVector.add(new JWavefrontObject(new File("./data/planet/AlienPlanet/p2/AlienPlanet.obj")));
        objVector.add(new JWavefrontObject(new File("./data/planet/AlienPlanet/p3/AlienPlanet.obj")));
        objVector.add(new JWavefrontObject(new File("./data/planet/AlienPlanet/p4/AlienPlanet.obj")));
        objVector.add(new JWavefrontObject(new File("./data/planet/AlienPlanet/p5/AlienPlanet.obj")));
        objVector.add(new JWavefrontObject(new File("./data/planet/AlienPlanet/p6/AlienPlanet.obj")));
        
        ellipses.add(new Ellipse(0.35f, 0.5f));
        ellipses.add(new Ellipse(0.7f, 0.7f));
        ellipses.add(new Ellipse(0.2f, 0.3f));
        ellipses.add(new Ellipse(0.7f, 0.9f));
        
        light = new Light();
        
        alpha = 0;
        beta = gama = epslon = 0;
        delta = 1.0f;
        i = 0.1f;
        j = 0.1f;
        x = y = z = xlua = ylua = 0.0f;
        mult = 1.0f;
        material = new Material();
        nstar = 2000;
        starPoints = new float[nstar];
        star = new WiredCube();
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        // Get pipeline
        GL3 gl = drawable.getGL().getGL3();
        
        // Print OpenGL version
        System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) + "\n");
        
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0f);
        
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glEnable(GL.GL_CULL_FACE);
        
        //inicializa os shaders
        shader.init(gl);
        
        //ativa os shaders
        shader.bind();
        
        //inicializa a matrix Model and Projection
        modelMatrix.init(gl, shader.getUniformLocation("u_modelMatrix"));
        projectionMatrix.init(gl, shader.getUniformLocation("u_projectionMatrix"));
        viewMatrix.init(gl, shader.getUniformLocation("u_viewMatrix"));
        
        Iterator it = objVector.iterator();
        try {
            //init the model
            while (it.hasNext()) {
                JWavefrontObject atual = (JWavefrontObject) it.next();
                atual.init(gl, shader);
                atual.unitize();
                atual.dump();
            }
            /*model.init(gl, shader);
            model.unitize();
            model.dump();*/
        } catch (IOException ex) {
            Logger.getLogger(Example12.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // init the light
        light.init(gl, shader);
        light.setPosition(new float[]{0.0f, 1.0f, 2.0f, 0.0f});
        light.setAmbientColor(new float[]{0.9f, 0.9f, 0.9f, 0.0f});
        light.setDiffuseColor(new float[]{1.0f, 1.0f, 1.0f, 0.0f});
        light.setSpecularColor(new float[]{0.9f, 0.9f, 0.9f, 0.0f});
        light.bind();
        
        material.init(gl, shader);
        material.setAmbientColor(new float[]{0.5f, 0.5f, 0.5f, 0.0f});
        material.setDiffuseColor(new float[]{1.0f, 0.0f, 0.0f, 0.0f});
        material.setSpecularColor(new float[]{0.9f, 0.9f, 0.9f, 0.0f});
        material.setSpecularExponent(32);
        
        
        star.init(gl, shader);
        for (Ellipse atual : ellipses) {
            atual.init(gl, shader);
        }
        for (int i=0; i<(nstar/2); i++){
            starPoints[i*2]=((new Random().nextFloat())*800.0f-400.0f);
            starPoints[i*2+1]=((new Random().nextFloat())*800.0f-400.0f);
        }
    }
    
    public void drawLine(GLAutoDrawable drawable, Point3D i, Point3D f){
        
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        // Recupera o pipeline
        GL3 gl = drawable.getGL().getGL3();
        
        // Limpa o frame buffer com a cor definida
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        
        projectionMatrix.loadIdentity();
        projectionMatrix.perspective(45.0f+delta, 1.0f, 0.1f, 10.0f);
        projectionMatrix.bind();

        for (int l = 0; l<(nstar/2); l++){
            modelMatrix.loadIdentity();
            modelMatrix.scale(0.008f, 0.008f, 0.008f);
            modelMatrix.translate(starPoints[l*2],starPoints[l*2+1],0);
            modelMatrix.rotate(15*j, 0, 0, 1);
            modelMatrix.bind();
            material.bind();
            star.bind();
            star.draw(GL3.GL_LINE_LOOP);
        }
        
        modelMatrix.loadIdentity();
        modelMatrix.rotate(15*j, 0, 0, 1);
        modelMatrix.scale(0.03f, 0.03f, 0.03f);
        modelMatrix.bind();
        i+=0.1f;
        Iterator it = objVector.iterator();
        JWavefrontObject atual = (JWavefrontObject) it.next();
        //model.draw();
        atual.draw();//sol
        
        
        lastPoints.add(0, new Point3D(x, y, 0));
        x = ((float) Math.cos(j*Math.PI/40)) * 0.5f;
        y = ((float) Math.sin(j*Math.PI/40)) * 0.5f;
        x *= 0.7f;
        
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(x, y, 0);
        modelMatrix.rotate(30*j, 1, 1, 0);
        modelMatrix.scale(0.03f, 0.03f, 0.03f);
        modelMatrix.bind();
        
        atual = (JWavefrontObject) it.next();
        atual.draw();//mercurio
        
        Iterator itEllipse = ellipses.iterator();
        Ellipse atualEllipse = (Ellipse) itEllipse.next();
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.bind();
        material.bind();
        atualEllipse.bind();
        atualEllipse.draw(GL3.GL_LINE_LOOP);
        
        j+=(0.1f*mult);
        

        xlua = ((float) Math.cos(j*Math.PI/15)) * 0.1f;
        ylua = ((float) Math.sin(j*Math.PI/15)) * 0.1f;

        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(x+xlua, y+ylua, 0);
        modelMatrix.rotate(30*j, 1, 1, 0);
        modelMatrix.scale(0.02f, 0.02f, 0.02f);
        modelMatrix.bind();

        atual = (JWavefrontObject) it.next();
        //model.draw();
        atual.draw();
        //model.draw();

        x = ((float) Math.cos(j*Math.PI/80)) * 0.7f;
        y = ((float) Math.sin(j*Math.PI/80)) * 0.7f;

        modelMatrix.loadIdentity();
        modelMatrix.rotate(60, 1, 0, 0);
        modelMatrix.translate(x, y, 0);
        modelMatrix.rotate((15.0f)*(j+0.6f), 1, 1, 0);
        modelMatrix.scale(0.07f, 0.07f, 0.07f);
        modelMatrix.bind();
        atual = (JWavefrontObject) it.next();
        atual.draw();//venus

        atualEllipse = (Ellipse) itEllipse.next();
        modelMatrix.loadIdentity();
        modelMatrix.rotate(60, 1, 0, 0);
        modelMatrix.bind();
        material.bind();
        atualEllipse.bind();
        atualEllipse.draw(GL3.GL_LINE_LOOP);
        
        x = ((float) Math.cos(j*Math.PI/80)) * 0.2f;
        y = ((float) Math.sin(j*Math.PI/80)) * 0.3f;

        modelMatrix.loadIdentity();
        modelMatrix.rotate(10, 1, 0, 0);
        modelMatrix.translate(x, y, 0);
        modelMatrix.rotate((15.0f)*(j+0.6f), 1, 1, 0);
        modelMatrix.scale(0.05f, 0.05f, 0.05f);
        modelMatrix.bind();
        atual = (JWavefrontObject) it.next();
        atual.draw();//venus
        
        x = ((float) Math.cos(j*Math.PI/80)) * 0.7f;
        y = ((float) Math.sin(j*Math.PI/80)) * 0.9f;

        atualEllipse = (Ellipse) itEllipse.next();
        modelMatrix.loadIdentity();
        modelMatrix.rotate(10, 1, 0, 0);
        modelMatrix.bind();
        material.bind();
        atualEllipse.bind();
        atualEllipse.draw(GL3.GL_LINE_LOOP);
        
        modelMatrix.loadIdentity();
        modelMatrix.rotate(10, 1, 0, 0);
        modelMatrix.translate(x, y, 0);
        modelMatrix.rotate((15.0f)*(j+0.6f), 1, 1, 0);
        modelMatrix.scale(0.02f, 0.02f, 0.02f);
        modelMatrix.bind();
        atual = (JWavefrontObject) it.next();
        atual.draw();

        atualEllipse = (Ellipse) itEllipse.next();
        modelMatrix.loadIdentity();
        modelMatrix.rotate(10, 1, 0, 0);
        modelMatrix.bind();
        material.bind();
        atualEllipse.bind();
        atualEllipse.draw(GL3.GL_LINE_LOOP);
        
        //model.draw();
        
        viewMatrix.loadIdentity();
        float viewUpy = (float) (Math.cos(epslon*Math.PI/180.0f));
        if ( Math.abs(viewUpy) < 0.01){
            viewUpy = -0.01f;
        }
        float x = (float) (beta*2);
        float y = (float) (alpha*2);
        float z = 1;
        viewMatrix.lookAt(x, y, z ,
                beta, alpha, 0, 
                0, viewUpy, 0);
        viewMatrix.bind();
        System.out.println("x: " + x + " y: " + y + " z: " + z);
        light.bind();


        // Força execução das operações declaradas
        gl.glFlush();
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
        Iterator it = objVector.iterator();
        while (it.hasNext()) {
            JWavefrontObject atual = (JWavefrontObject) it.next();
            atual.dispose();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_PAGE_UP://faz zoom-in
                delta = delta - 5f;
                if (delta < -40.0f){
                    delta = -40.0f;
                }
                break;
            case KeyEvent.VK_PAGE_DOWN://faz zoom-out
                delta = delta + 5f;
                if (delta > 45.0f){
                    delta = 45.0f;
                }
                break;
            case KeyEvent.VK_UP://gira sobre o eixo-x
                alpha = alpha - 0.1f;
                break;
            case KeyEvent.VK_DOWN://gira sobre o eixo-x
                alpha = alpha + 0.1f;
                break;
            case KeyEvent.VK_LEFT://gira sobre o eixo-y
                beta = beta - 0.1f;
                break;
            case KeyEvent.VK_RIGHT://gira sobre o eixo-y
                beta = beta + 0.1f;
                break;
            case KeyEvent.VK_A:
                epslon = epslon - 1;
                break;
            case KeyEvent.VK_D:
                epslon = epslon + 1;
                break;
            case KeyEvent.VK_W:
                gama = gama + 1;
                break;
            case KeyEvent.VK_S:
                gama = gama - 1;
                break;
            case KeyEvent.VK_J:
                mult = mult - 0.01f;
                if (mult < 0){
                    mult = 0;
                }
                break;
            case KeyEvent.VK_K:
                mult = mult + 0.01f;
                break;
            case KeyEvent.VK_P:
                mult = 0;
                break;
            case KeyEvent.VK_C:
                gama = 0;
                epslon = 0;
                alpha = 0;
                beta = 0;
                break;
        }
    }
    
    public static void main(String[] args) {
        // Get GL3 profile (to work with OpenGL 4.0)
        GLProfile profile = GLProfile.get(GLProfile.GL3);
        
        // Configurations
        GLCapabilities glcaps = new GLCapabilities(profile);
        glcaps.setDoubleBuffered(true);
        glcaps.setHardwareAccelerated(true);
        
        // Create canvas
        GLCanvas glCanvas = new GLCanvas(glcaps);
        
        // Add listener to panel
        Example12 listener = new Example12();
        glCanvas.addGLEventListener(listener);
        
        Frame frame = new Frame("CG - Uquê");
        frame.setSize(600, 600);
        frame.add(glCanvas);
        frame.addKeyListener(listener);
        final AnimatorBase animator = new FPSAnimator(glCanvas, 60);
        
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        animator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        frame.setVisible(true);
        animator.start();
    }
}
