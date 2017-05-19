package com.android.airhockey;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.android.airhockey.objects.Mallet;
import com.android.airhockey.objects.Table;
import com.android.airhockey.programs.ColorShaderProgram;
import com.android.airhockey.programs.TextureShaderProgram;
import com.android.airhockey.util.MatrixHelper;
import com.android.airhockey.util.ShaderHelper;
import com.android.airhockey.util.TextResourceReader;
import com.android.airhockey.util.TextureHelper;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_FLOAT;
import static android.opengl.GLES20.GL_LINES;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glDrawArrays;
import static android.opengl.GLES20.glEnableVertexAttribArray;
import static android.opengl.GLES20.glGetAttribLocation;
import static android.opengl.GLES20.glGetUniformLocation;
import static android.opengl.GLES20.glUniform4f;
import static android.opengl.GLES20.glUniform4fv;
import static android.opengl.GLES20.glUniformMatrix4fv;
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.orthoM;
import static android.opengl.Matrix.rotateM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.translateM;

/**
 * Created by pengfu on 13/05/2017.
 */

public class AirHockeyRenderer implements GLSurfaceView.Renderer {


    private final float [] projectionMatrix = new float[16] ;
    private final float [] modelMatrix      = new float[16] ;


    private Context mContext ;

    private Table table ;
    private Mallet mallet ;

    private TextureShaderProgram textureShaderProgram ;
    private ColorShaderProgram colorShaderProgram ;

    private int texture ;

    public AirHockeyRenderer(Context context){
        mContext = context ;
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 0f);

        table = new Table() ;
        mallet = new Mallet() ;

        textureShaderProgram = new TextureShaderProgram(mContext) ;
        colorShaderProgram = new ColorShaderProgram(mContext) ;

        texture = TextureHelper.loadTexture(mContext ,R.drawable.air_hockey_surface) ;
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0,0,width ,height);

        MatrixHelper.perspectiveM(projectionMatrix ,45 ,width/(height*1f) ,1f ,10f);
        setIdentityM(modelMatrix ,0);
        translateM(modelMatrix ,0 ,0 ,0 ,-2.5f);
        rotateM(modelMatrix ,0 ,-50f ,1 ,0 ,0);

        final float[] temp = new float[16] ;
        multiplyMM(temp ,0 ,projectionMatrix ,0 ,modelMatrix ,0);
        System.arraycopy(temp ,0 ,projectionMatrix ,0 ,temp.length);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        // draw table
        textureShaderProgram.useProgram();
        textureShaderProgram.setUniform(projectionMatrix ,texture);
        table.bindData(textureShaderProgram);
        table.draw();

        // draw mallet
        colorShaderProgram.useProgram();
        colorShaderProgram.setUniforms(projectionMatrix);
        mallet.bindData(colorShaderProgram);
        mallet.draw();

    }
}
