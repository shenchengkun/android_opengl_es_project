package com.iglassus.epf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class MyGrid {
    private float[] vertices;
    private float[] texels;
    private int[] indices;
    private int indicesCount;

    private IntBuffer indiceBuffer;
    private FloatBuffer vertexBuffer;
    private FloatBuffer textureVertexBuffer;
    public IntBuffer getIndiceBuffer() {
        return indiceBuffer;
    }

    public FloatBuffer getVertexBuffer() {
        return vertexBuffer;
    }

    public FloatBuffer getTextureVertexBuffer() {
        return textureVertexBuffer;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getTexels() {
        return texels;
    }

    public int[] getIndices() {
        return indices;
    }

    public int getIndicesCount() {
        return indicesCount;
    }

    public MyGrid(float[] vertices, float[] texels, int[] indices, int indicesCount) {
        this.vertices = vertices;
        this.texels = texels;
        this.indices = indices;
        this.indicesCount = indicesCount;


        vertexBuffer = ByteBuffer.allocateDirect(vertices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertices);
        vertexBuffer.position(0);
        textureVertexBuffer = ByteBuffer.allocateDirect(texels.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(texels);
        textureVertexBuffer.position(0);
        indiceBuffer=ByteBuffer.allocateDirect(indices.length * 4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(indices);
        indiceBuffer.position(0);
    }
}
