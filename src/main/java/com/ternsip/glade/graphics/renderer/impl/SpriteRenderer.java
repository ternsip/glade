package com.ternsip.glade.graphics.renderer.impl;

import com.ternsip.glade.graphics.renderer.base.Renderer;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

import static com.ternsip.glade.Glade.UNIVERSE;
import static org.lwjgl.opengl.GL11.*;

@SuppressWarnings("unused")
public class SpriteRenderer implements Renderer {

    private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
    //private EntityShader shader = ShaderProgram.createShader(SpriteShader.class);

    public void render() {
        drawAxis();
    }

    public void finish() {
        //shader.finish();
    }

    @Override
    public int getPriority() {
        return 1;
    }

    private void drawAxis() {
        // draw some lines
        UNIVERSE.getCamera().getProjectionViewMatrix().get(matrixBuffer);
        glLoadMatrixf(matrixBuffer);
        glColor3f(1.0f, 0.0f, 0.0f); // red x
        glBegin(GL_LINES);
        // x aix

        glVertex3f(-4.0f, 0.0f, 0.0f);
        glVertex3f(4.0f, 0.0f, 0.0f);

        glVertex3f(4.0f, 0.0f, 0.0f);
        glVertex3f(3.0f, 1.0f, 0.0f);

        glVertex3f(4.0f, 0.0f, 0.0f);
        glVertex3f(3.0f, -1.0f, 0.0f);
        glEnd();

        // y
        glColor3f(0.0f, 1.0f, 0.0f); // green y
        glBegin(GL_LINES);
        glVertex3f(0.0f, -4.0f, 0.0f);
        glVertex3f(0.0f, 4.0f, 0.0f);

        glVertex3f(0.0f, 4.0f, 0.0f);
        glVertex3f(1.0f, 3.0f, 0.0f);

        glVertex3f(0.0f, 4.0f, 0.0f);
        glVertex3f(-1.0f, 3.0f, 0.0f);
        glEnd();

        // z
        glColor3f(0.0f, 0.0f, 1.0f); // blue z
        glBegin(GL_LINES);
        glVertex3f(0.0f, 0.0f, -4.0f);
        glVertex3f(0.0f, 0.0f, 4.0f);


        glVertex3f(0.0f, 0.0f, 4.0f);
        glVertex3f(0.0f, 1.0f, 3.0f);

        glVertex3f(0.0f, 0.0f, 4.0f);
        glVertex3f(0.0f, -1.0f, 3.0f);
        glEnd();

        glPopMatrix();
    }

}
