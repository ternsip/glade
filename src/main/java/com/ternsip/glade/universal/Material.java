package com.ternsip.glade.universal;

import org.joml.Vector4f;

import java.io.File;

import static com.ternsip.glade.universal.Mesh.SKIP_TEXTURE;
import static org.lwjgl.opengl.GL11.glDeleteTextures;

public class Material {

    public static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private Vector4f diffuseColour;

    private Vector4f specularColour;

    private float shininess;

    private float reflectance;

    private Texture texture;

    private Texture normalMap;

    public Material(File textureFile) {
        if (textureFile != SKIP_TEXTURE) {
            try {
                texture = new Texture(textureFile);
            } catch (Exception e) {
                System.out.println(e.getMessage()); // TODO to logs
            }
        }
    }

    public Material() {
        this.diffuseColour = DEFAULT_COLOUR;
        this.specularColour = DEFAULT_COLOUR;
        this.texture = null;
        this.reflectance = 0;
    }

    public Material(Vector4f colour, float reflectance) {
        this(colour, colour, null, reflectance);
    }

    public Material(Texture texture) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, texture, 0);
    }

    public Material(Texture texture, float reflectance) {
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, texture, reflectance);
    }

    public Material(Vector4f diffuseColour, Vector4f specularColour, Texture texture, float reflectance) {
        this.diffuseColour = diffuseColour;
        this.specularColour = specularColour;
        this.texture = texture;
        this.reflectance = reflectance;
    }

    public Vector4f getDiffuseColour() {
        return diffuseColour;
    }

    public void setDiffuseColour(Vector4f diffuseColour) {
        this.diffuseColour = diffuseColour;
    }

    public Vector4f getSpecularColour() {
        return specularColour;
    }

    public void setSpecularColour(Vector4f specularColour) {
        this.specularColour = specularColour;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance) {
        this.reflectance = reflectance;
    }

    public boolean isTextured() {
        return this.texture != null;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public boolean hasNormalMap() {
        return this.normalMap != null;
    }

    public Texture getNormalMap() {
        return normalMap;
    }

    public void setNormalMap(Texture normalMap) {
        this.normalMap = normalMap;
    }

    public void cleanUp() {
        if (isTextured()) {
            glDeleteTextures(getTexture().getId());
        }
        if (hasNormalMap()) {
            glDeleteTextures(getNormalMap().getId());
        }
    }
}