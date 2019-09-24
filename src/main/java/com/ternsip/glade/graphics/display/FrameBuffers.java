package com.ternsip.glade.graphics.display;

import com.ternsip.glade.common.events.display.ResizeEvent;
import com.ternsip.glade.graphics.interfaces.IGraphics;
import lombok.Getter;

import static org.lwjgl.opengl.ARBFramebufferObject.GL_RENDERBUFFER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL30C.GL_FRAMEBUFFER;

@Getter
public class FrameBuffers implements IGraphics {

    private int colorRenderBufferFirst;
    private int colorRenderBufferSecond;
    private int depthRenderBuffer;
    private int fbo;
    private int maxSamples;
    private int samples;
    private int width;
    private int height;

    public FrameBuffers() {
        maxSamples = glGetInteger(GL_MAX_SAMPLES);
        samples = 4;
        getGraphics().getEventIOReceiverGraphics().registerCallback(ResizeEvent.class, (resizeEvent) -> resizeFBOs());
        resetSize();
        createFBOs();
    }

    public void resizeFBOs() {
        resetSize();
        if (getWidth() * getHeight() == 0) {
            return;
        }
        glDeleteRenderbuffers(depthRenderBuffer);
        glDeleteRenderbuffers(colorRenderBufferFirst);
        glDeleteRenderbuffers(colorRenderBufferSecond);
        glDeleteFramebuffers(fbo);
        createFBOs();
    }

    public void bindBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
    }

    public void resolveBuffer() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glBlitFramebuffer(0, 0, width, height, 0, 0, width, height, GL_COLOR_BUFFER_BIT, GL_NEAREST);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }

    private void resetSize() {
        width = getGraphics().getWindowData().getWidth();
        height = getGraphics().getWindowData().getHeight();
    }

    private void createFBOs() {

        colorRenderBufferFirst = glGenRenderbuffers();
        colorRenderBufferSecond = glGenRenderbuffers();
        depthRenderBuffer = glGenRenderbuffers();
        fbo = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        glBindRenderbuffer(GL_RENDERBUFFER, colorRenderBufferFirst);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_RGBA16F, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, colorRenderBufferFirst);

        glBindRenderbuffer(GL_RENDERBUFFER, colorRenderBufferSecond);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_R8, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT1, GL_RENDERBUFFER, colorRenderBufferSecond);

        glBindRenderbuffer(GL_RENDERBUFFER, depthRenderBuffer);
        glRenderbufferStorageMultisample(GL_RENDERBUFFER, samples, GL_DEPTH_COMPONENT32F, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, depthRenderBuffer);

        int fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);
        if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
            throw new AssertionError("Could not create FBO: " + fboStatus);
        }

        glBindRenderbuffer(GL_RENDERBUFFER, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

    }


}
