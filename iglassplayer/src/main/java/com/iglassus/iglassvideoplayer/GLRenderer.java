package com.iglassus.iglassvideoplayer;

/**
 * Created by AdminUser on 3/14/2018.
 */

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.Surface;

import com.google.android.exoplayer2.SimpleExoPlayer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


/**
 * Created by Ads on 2016/11/13.
 */
public class GLRenderer implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private final float[] leftTextureVertexData;
    private final float[] rightTextureVertexData;
    private final FloatBuffer leftTextureVertexBuffer;
    private final FloatBuffer rightTextureVertexBuffer;
    private final float[] vertexDataNoDis;
    private final FloatBuffer vertexBufferNoDis;
    private Grid grid;
    private int[] indiceData;
    private IntBuffer indiceBuffer;
    private FloatBuffer vertexBuffer;
    private  float[] vertexData;
    private  float[] textureVertexData;
    private FloatBuffer textureVertexBuffer;

    private static final String TAG = "GLRenderer";
    private Context context;
    private int aPositionHandle;
    private int programId;
    private final float[] projectionMatrix=new float[16];
    private int uMatrixHandle;
    private int uTextureSamplerHandle;
    private int aTextureCoordHandle;
    private int textureId;
    private SurfaceTexture surfaceTexture;
    public MediaPlayer mediaPlayer;
    private float[] mSTMatrix = new float[16];
    private int uSTMMatrixHandle;
    private boolean updateSurface;
    private boolean playerPrepared;
    private int screenWidth,screenHeight;
    public boolean is2D=true;
    public int offset=-0;
    private SimpleExoPlayer exoPlayer;
    public boolean distortion=true;
    public boolean is169=false;

    public GLRenderer(Context context, Grid grid,SimpleExoPlayer player) {
        this.context = context;
        this.grid=grid;
        this.exoPlayer=player;

        vertexData=grid.getVertices();
        vertexDataNoDis=grid.getVerticesNoDistortion();
        textureVertexData=grid.getTexels();
        leftTextureVertexData=grid.getLeftTexels();
        rightTextureVertexData=grid.getRightTexels();
        indiceData=grid.getIndices();
        vertexBuffer = ByteBuffer.allocateDirect(vertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexData);
        vertexBuffer.position(0);
        vertexBufferNoDis = ByteBuffer.allocateDirect(vertexDataNoDis.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(vertexDataNoDis);
        vertexBufferNoDis.position(0);
        textureVertexBuffer = ByteBuffer.allocateDirect(textureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(textureVertexData);
        textureVertexBuffer.position(0);
        leftTextureVertexBuffer = ByteBuffer.allocateDirect(leftTextureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(leftTextureVertexData);
        leftTextureVertexBuffer.position(0);
        rightTextureVertexBuffer = ByteBuffer.allocateDirect(rightTextureVertexData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(rightTextureVertexData);
        rightTextureVertexBuffer.position(0);
        indiceBuffer=ByteBuffer.allocateDirect(indiceData.length * 4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer()
                .put(indiceData);
        indiceBuffer.position(0);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        String vertexShader = ShaderUtils.readRawTextFile(context, R.raw.vertex_shader);
        String fragmentShader= ShaderUtils.readRawTextFile(context, R.raw.fragment_shader);
        programId=ShaderUtils.createProgram(vertexShader,fragmentShader);
        aPositionHandle= GLES20.glGetAttribLocation(programId,"aPosition");

        //uMatrixHandle=GLES20.glGetUniformLocation(programId,"uMatrix");
        uSTMMatrixHandle = GLES20.glGetUniformLocation(programId, "uSTMatrix");
        uTextureSamplerHandle=GLES20.glGetUniformLocation(programId,"sTexture");
        aTextureCoordHandle=GLES20.glGetAttribLocation(programId,"aTexCoord");

        GLES20.glEnable(GLES20.GL_DEPTH_TEST);
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        textureId = textures[0];
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textureId);
        ShaderUtils.checkGlError("glBindTexture mTextureID");
         GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);

        surfaceTexture = new SurfaceTexture(textureId);
        surfaceTexture.setOnFrameAvailableListener(this);
        exoPlayer.setVideoSurface(new Surface(surfaceTexture));
    }


    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        synchronized (this){
            if (updateSurface){
                surfaceTexture.updateTexImage();
                surfaceTexture.getTransformMatrix(mSTMatrix);
                updateSurface = false;
            }
        }
        GLES20.glUseProgram(programId);
        //GLES20.glUniformMatrix4fv(uMatrixHandle,1,false,projectionMatrix,0);
        GLES20.glUniformMatrix4fv(uSTMMatrixHandle, 1, false, mSTMatrix, 0);
        GLES20.glUniform1i(uTextureSamplerHandle,0);
        GLES20.glEnableVertexAttribArray(aPositionHandle);
        GLES20.glEnableVertexAttribArray(aTextureCoordHandle);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,textureId);
        //GLES20.glBindTexture(GL_TEXTURE_2D, textureId);
        if(is2D) GLES20.glVertexAttribPointer(aTextureCoordHandle,2,GLES20.GL_FLOAT,false,8,textureVertexBuffer);

////////////////////////////////////Left///////////////////////////////////////////////
        GLES20.glViewport(screenWidth,is169?offset:0,screenWidth,screenHeight-2*(is169?offset:0));  //由于vertex x坐标都取反了，所以这里左右也翻转
        if(!is2D) GLES20.glVertexAttribPointer(aTextureCoordHandle,2,GLES20.GL_FLOAT,false,8,leftTextureVertexBuffer);
        GLES20.glVertexAttribPointer(aPositionHandle, 3, GLES20.GL_FLOAT, false, 12, distortion?vertexBufferNoDis:vertexBuffer);
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, grid.getIndicesCount(), GLES20.GL_UNSIGNED_INT, indiceBuffer);
////////////////////////////////////Right///////////////////////////////////////////////
        if(!is2D) GLES20.glVertexAttribPointer(aTextureCoordHandle,2,GLES20.GL_FLOAT,false,8,rightTextureVertexBuffer);
        GLES20.glViewport(0,is169?offset:0,screenWidth,screenHeight-2*(is169?offset:0));
        GLES20.glDrawElements(GLES20.GL_TRIANGLE_STRIP, grid.getIndicesCount(), GLES20.GL_UNSIGNED_INT, indiceBuffer);
    }

    @Override
    synchronized public void onFrameAvailable(SurfaceTexture surface) {
        updateSurface = true;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        screenWidth=width/2; screenHeight=height;
        //因为是duplicate，所以width取一半
        offset=(int)(screenHeight*0.09);
        Log.d("获得屏幕尺寸哈哈哈哈或", "onSurfaceChanged width = " + width + "  height = " + height);
    }
}
