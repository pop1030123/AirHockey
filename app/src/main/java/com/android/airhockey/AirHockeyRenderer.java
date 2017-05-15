package com.android.airhockey;

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.android.airhockey.util.ShaderHelper;
import com.android.airhockey.util.TextResourceReader;

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
import static android.opengl.GLES20.glUseProgram;
import static android.opengl.GLES20.glVertexAttribPointer;
import static android.opengl.GLES20.glViewport;

/**
 * Created by pengfu on 13/05/2017.
 */

public class AirHockeyRenderer implements GLSurfaceView.Renderer {

    private static final int POSITION_COMPONENT_COUNT = 2 ;
    private static final int COLOR_COMPONENT_COUNT = 3 ;
    private static final int BYTES_PER_FLOAT = 4 ;
    private static final int STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) *BYTES_PER_FLOAT ;
    private final FloatBuffer vertexData ;

    private static final String A_COLOR = "a_Color" ;
    private int aColorLocation ;

    private static final String A_POSITION = "a_Position" ;
    private int aPositionLocation ;

    private int program ;

    private Context mContext ;
    public AirHockeyRenderer(Context context){
        mContext = context ;
        float [] tableVertices = {
                // triangle fan
                   0f ,   0f ,1f ,1f ,1f ,
                -0.5f ,-0.5f ,0.7f,0.7f,0.7f,
                 0.5f ,-0.5f ,0.7f,0.7f,0.7f,
                 0.5f , 0.5f ,0.7f,0.7f,0.7f,
                -0.5f , 0.5f ,0.7f,0.7f,0.7f,
                -0.5f, -0.5f ,0.7f,0.7f,0.7f,
                // line
                -0.5f ,0f ,1f,0f,0f,
                 0.5f ,0f ,1f,0f,0f,
                // mallets
                0f ,-0.25f ,0f,0f,1f,
                0f , 0.25f ,1f,0f,0f
        } ;
        vertexData = ByteBuffer.allocateDirect(tableVertices.length* BYTES_PER_FLOAT)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer() ;
        vertexData.put(tableVertices) ;
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        glClearColor(0f, 0f, 0f, 0f);

        String vertexShaderSource = TextResourceReader.readTextFileFromResource(mContext ,R.raw.simple_vertex_shader)  ;
        String fragmentShaderSource = TextResourceReader.readTextFileFromResource(mContext ,R.raw.simple_fragment_shader) ;

        int vertexShader = ShaderHelper.compileVertexShader(vertexShaderSource) ;
        int fragmentShader = ShaderHelper.compileFragmentShader(fragmentShaderSource) ;

        program = ShaderHelper.linkProgram(vertexShader ,fragmentShader) ;

        ShaderHelper.validateProgram(program) ;
        glUseProgram(program);

        aColorLocation = glGetAttribLocation(program ,A_COLOR) ;
        aPositionLocation = glGetAttribLocation(program ,A_POSITION) ;

        vertexData.position(0) ;
        glVertexAttribPointer(aPositionLocation ,POSITION_COMPONENT_COUNT ,GL_FLOAT ,false ,STRIDE ,vertexData);

        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT) ;
        glVertexAttribPointer(aColorLocation ,COLOR_COMPONENT_COUNT ,GL_FLOAT ,false ,STRIDE ,vertexData);

        glEnableVertexAttribArray(aColorLocation);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        glViewport(0,0,width ,height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        glClear(GL_COLOR_BUFFER_BIT);

        glDrawArrays(GL_TRIANGLE_FAN ,0 ,6);

        glDrawArrays(GL_LINES ,6 ,2);

        glDrawArrays(GL_POINTS ,8,1);
        glDrawArrays(GL_POINTS ,9 ,1);
    }
}
