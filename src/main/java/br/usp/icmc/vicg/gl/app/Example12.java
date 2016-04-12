package br.usp.icmc.vicg.gl.app;

import br.usp.icmc.vicg.gl.core.Light;
import br.usp.icmc.vicg.gl.jwavefront.JWavefrontObject;
import br.usp.icmc.vicg.gl.matrix.Matrix4;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Example12 extends KeyAdapter implements GLEventListener {
    
    private final Shader shader; // Gerenciador dos shaders
    private final Matrix4 modelMatrix;
    private final Matrix4 projectionMatrix;
    private final Matrix4 viewMatrix;
    private final JWavefrontObject model;
    private final Light light;
    private float alpha;
    private float beta;
    private float delta;
    private float i;
    private float j;
    private float x;
    private float y;
    private float z;
    private float xlua;
    private float ylua;
    private float epslon;
    private float gama;
    
    public Example12() {
        // Carrega os shaders
        shader = ShaderFactory.getInstance(ShaderType.COMPLETE_SHADER);
        modelMatrix = new Matrix4();
        projectionMatrix = new Matrix4();
        viewMatrix = new Matrix4();
        
        model = new JWavefrontObject(new File("./data/al.obj"));
        light = new Light();
        
        alpha = 0;
        beta = gama = epslon = 0;
        delta = 1.0f;
        i = 0.1f;
        j = 0.1f;
        x = y = z = xlua = ylua = 0.0f;
    }
    
    @Override
    public void init(GLAutoDrawable drawable) {
        // Get pipeline
        GL3 gl = drawable.getGL().getGL3();
        
        // Print OpenGL version
        System.out.println("OpenGL Version: " + gl.glGetString(GL.GL_VERSION) + "\n");
        
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
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
        
        try {
            //init the model
            model.init(gl, shader);
            model.unitize();
            model.dump();
        } catch (IOException ex) {
            Logger.getLogger(Example12.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // init the light
        light.setPosition(new float[]{10, 10, 50, 1.0f});
        light.setAmbientColor(new float[]{0.1f, 0.1f, 0.1f, 1.0f});
        light.setDiffuseColor(new float[]{0.75f, 0.75f, 0.75f, 1.0f});
        light.setSpecularColor(new float[]{0.7f, 0.7f, 0.7f, 1.0f});
        light.init(gl, shader);
    }
    
    @Override
    public void display(GLAutoDrawable drawable) {
        // Recupera o pipeline
        GL3 gl = drawable.getGL().getGL3();
        
        // Limpa o frame buffer com a cor definida
        gl.glClear(GL3.GL_COLOR_BUFFER_BIT | GL3.GL_DEPTH_BUFFER_BIT);
        
        projectionMatrix.loadIdentity();
        //    projectionMatrix.ortho(
        //            -delta, delta,
        //            -delta, delta,
        //            -2*delta, 2*delta);
        projectionMatrix.perspective(45.0f+delta, 1.0f, 0.1f, 10.0f);
        projectionMatrix.bind();

        modelMatrix.loadIdentity();
        //modelMatrix.rotate(30, 1, 0, 0);
        //modelMatrix.rotate(10*i, 1, 1, 0);
        modelMatrix.scale(0.03f, 0.03f, 0.03f);
        modelMatrix.bind();
        i+=0.1f;

        model.draw();

        x = ((float) Math.cos(j*Math.PI/40)) * 0.5f;
        y = ((float) Math.sin(j*Math.PI/40)) * 0.5f;
        x *= 0.7f;
        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(x, y, 0);
        modelMatrix.rotate(30*j, 1, 1, 0);
        modelMatrix.scale(0.02f, 0.02f, 0.02f);
        modelMatrix.bind();
        model.draw();
        j+=0.1f;

        xlua = ((float) Math.cos(j*Math.PI/15)) * 0.1f;
        ylua = ((float) Math.sin(j*Math.PI/15)) * 0.1f;

        modelMatrix.loadIdentity();
        modelMatrix.rotate(30, 1, 0, 0);
        modelMatrix.translate(x+xlua, y+ylua, 0);
        modelMatrix.rotate(30*j, 1, 1, 0);
        modelMatrix.scale(0.025f, 0.025f, 0.025f);
        modelMatrix.bind();

        model.draw();

        x = ((float) Math.cos(j*Math.PI/80)) * 0.7f;
        y = ((float) Math.sin(j*Math.PI/80)) * 0.7f;

        modelMatrix.loadIdentity();
        modelMatrix.rotate(60, 1, 1, 0);
        modelMatrix.translate(x, y, 0);
        modelMatrix.rotate((15.0f)*(j+0.6f), 1, 1, 0);
        modelMatrix.scale(0.07f, 0.07f, 0.07f);
        modelMatrix.bind();

        model.draw();
        viewMatrix.loadIdentity();
        viewMatrix.lookAt((float) (beta*2 + Math.cos(epslon)), (float) (alpha*2 + Math.sin(epslon)), 1,
                beta, alpha, 0,
                0, 1, 0);
        viewMatrix.bind();

        light.bind();


        // Força execução das operações declaradas
        gl.glFlush();
    }
    
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }
    
    @Override
    public void dispose(GLAutoDrawable drawable) {
        model.dispose();
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
